package sk.teamsoft.observablecollection;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import sk.teamsoft.rxlog.RxLog;
import timber.log.Timber;

/**
 * Observable Adapter is a generic class for use with the recyclerView
 * <p>
 * This class separates UI related code from Data source logic.
 * <p>
 * usage:
 * - create an implementation of abstract {@link AdapterSource} class
 * - pass this source into the constructor of this class
 * - when modifying data source, list is notified appropriately
 * @author Dusan Bartos
 */
@SuppressWarnings("WeakerAccess")
public class ObservableAdapter<T> extends RecyclerView.Adapter<ObservableAdapter.ViewHolder<T>> {

    private final AdapterSource<T> source;
    private final PublishSubject<ViewEvent> itemEvent = PublishSubject.create();
    private final CompositeDisposable changeWatcher = new CompositeDisposable();

    public ObservableAdapter(AdapterSource<T> source) {
        this.source = source;
    }

    @Override public ViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = source.getLayout(viewType);
        try {
            @SuppressWarnings("unchecked")
            final View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            //noinspection unchecked
            return new ViewHolder<>((BindableView<T>) v);
        } catch (InflateException e) {
            Timber.e(e, "Error inflating view %s %s", layout, parent.getContext().getResources().getResourceEntryName(layout));
            throw e;
        }
    }

    @Override public void onBindViewHolder(ViewHolder<T> holder, int position) {
        holder.bindTo(source.get(position), position);
    }

    @Override public int getItemViewType(int position) {
        return source.getViewType(position);
    }

    @Override public int getItemCount() {
        return source.getCount();
    }

    @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        //initial notify
        notifyDataSetChanged();
        changeWatcher.add(source.onNotifyRequested()
                .compose(RxLog.<DiffUtil.DiffResult>log("notifyRequested"))
                .subscribe(new Consumer<DiffUtil.DiffResult>() {
                               @Override
                               public void accept(@NonNull DiffUtil.DiffResult diffResult) throws Exception {
                                   diffResult.dispatchUpdatesTo(ObservableAdapter.this);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                Timber.e(throwable, "Error watching source data");
                            }
                        }));
    }

    @Override public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        //TODO check if all holders are also detached at this point
        changeWatcher.clear();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override public void onViewAttachedToWindow(final ViewHolder<T> holder) {
        super.onViewAttachedToWindow(holder);
        final Observable<Object> objectObservable = holder.holderView.onObservableEvent();
        if (objectObservable != null) {
            holder.disposable = objectObservable
                    .compose(RxLog.log("viewHolder:disposable"))
                    .filter(new Predicate<Object>() {
                        @Override public boolean test(@NonNull Object o) throws Exception {
                            return o != null;
                        }
                    })
                    .map(new Function<Object, ViewEvent>() {
                        @Override public ViewEvent apply(@NonNull Object o) throws Exception {
                            return new ViewEvent(holder, o);
                        }
                    })
                    .doOnError(new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            Timber.e("ObservableAdapter Error: %s", throwable.getMessage());
                        }
                    })
                    .subscribe(new Consumer<ViewEvent>() {
                        @Override
                        public void accept(@NonNull ViewEvent viewEvent) throws Exception {
                            itemEvent.onNext(viewEvent);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            Timber.e(throwable, "Error:viewHolder:event");
                        }
                    });
            holder.watcher.add(holder.disposable);
        } else {
            Timber.v("onAttached:noObservable");
            //TODO check if we need to dispose observable if there was any
        }
    }

    @Override public void onViewDetachedFromWindow(ViewHolder<T> holder) {
        holder.detach();
        super.onViewDetachedFromWindow(holder);
    }

    @Override public void onViewRecycled(ViewHolder<T> holder) {
        holder.recycle();
        super.onViewRecycled(holder);
    }

    /**
     * Generic method which dispatches viewHolder events (click, longtap, whatever) to
     * the listeners (typically presenters).
     * Data object is of type Object, but the real type depends on an implementation inside
     * specific view (makes use of {@link BindableView#onObservableEvent()} method)
     * @return observable event dispatcher
     */
    public Observable<ViewEvent> onItemEvent() {
        return itemEvent;
    }

    /**
     * Generic ViewHolder for the purposes of ObservableAdapter use
     */
    @SuppressWarnings("unused")
    static class ViewHolder<T> extends RecyclerView.ViewHolder {

        /**
         * AutoCasts interface to {@link View} type
         * @return View to construct ViewHolder
         */
        private static View autoCast(BindableView view) {
            if (view instanceof View) {
                return (View) view;
            }
            throw new IllegalStateException("BindableView cannot be cast automatically [" + view + "]");
        }

        private BindableView<T> holderView;
        private T item;
        private int position = -1;

        private final CompositeDisposable watcher = new CompositeDisposable();
        private Disposable disposable;

        ViewHolder(BindableView<T> itemView) {
            super(autoCast(itemView));
            this.holderView = itemView;
        }

        void bindTo(T item, int pos) {
            this.item = item;
            this.position = pos;
            holderView.bindItem(item);
        }

        void recycle() {
            watcher.clear();
            this.position = -1;

            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
            disposable = null;
        }

        void detach() {
            watcher.clear();
            this.item = null;
            this.position = -1;

            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
            disposable = null;
        }

        @Override public String toString() {
            return "ViewHolder{" +
                    "holderView=" + holderView +
                    ",item=" + item +
                    ",position=" + position +
                    '}';
        }
    }

    /**
     * Handler class for adapter item events
     */
    public static class ViewEvent {
        private WeakReference<ViewHolder> holder;
        private Object data;

        ViewEvent(ViewHolder holder, Object data) {
            this.holder = new WeakReference<>(holder);
            this.data = data;
        }

        @SuppressWarnings("unused")
        @Nullable
        public ViewHolder getView() {
            return holder.get();
        }

        public Object getData() {
            return data;
        }

        @Override public String toString() {
            return "ViewEvent{" +
                    "holder=" + holder.get() +
                    ",data=" + data +
                    '}';
        }
    }
}
