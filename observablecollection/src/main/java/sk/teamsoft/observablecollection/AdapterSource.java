package sk.teamsoft.observablecollection;

import android.support.annotation.LayoutRes;
import android.support.v7.util.DiffUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Data source for ObservableAdapter
 * Automatically notifies adapter when data is changed
 * It can implement different layouts in single adapter - similar to basic
 * recyclerView's adapter
 * <p>
 * If you need just a simple source with one view type and one layout, check {@link SimpleAdapterSource}
 * @author Dusan Bartos
 * @see #getViewType(int)
 * @see #getLayout(int)
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AdapterSource<T> {

    private final PublishSubject<List<T>> dataChangeSubject = PublishSubject.create();
    private final PublishSubject<DiffUtil.DiffResult> notifyDiffSubject = PublishSubject.create();
    protected List<T> data;
    /**
     * Parameter which determines if diffUtils checks for movement
     * This is true by default, but if you need extra optimization (i.e. when there is no need
     * for movement check - when only remove/add actions are possible)
     */
    private boolean diffDetectMovement = true;

    /**
     * Constructs observable source
     * @param data default data set
     */
    public AdapterSource(List<T> data) {
        this.data = data;
    }

    /**
     * Constructs observable source
     * @param data           default data set
     * @param detectMovement flag to detect movement during data set updates - DiffUtil
     */
    public AdapterSource(List<T> data, boolean detectMovement) {
        this(data);
        diffDetectMovement = detectMovement;
    }

    /**
     * Sets the data set to the adapter and automatically dispatches an update
     * via DiffUtil.DiffResult
     * @param data new data set
     */
    public void setData(List<T> data) {
        final DiffUtil.DiffResult diffResult = calculateDiff(this.data, data);
        this.data = data;
        notifyDiffSubject.onNext(diffResult);
        dataChangeSubject.onNext(data);
    }

    public T get(int position) {
        if (position >= 0 && position < data.size()) {
            return data.get(position);
        } else {
            return null;
        }
    }

    public int getIndexOf(T item) {
        return data.indexOf(item);
    }

    /**
     * Implement viewType determination from the data item at specific position
     */
    protected abstract int getViewType(int pos);

    /**
     * Implement resource layout type determination regarding its viewType
     */
    @LayoutRes public abstract int getLayout(int viewType);

    public int getCount() {
        return data.size();
    }

    /**
     * Change notifier
     * @return Observable dataSet
     */
    public Observable<List<T>> onDataChange() {
        return dataChangeSubject;
    }

    /**
     * Internal helper to tell adapter that it needs to be notified about changes from diff
     * result
     * @return observable diff result
     */
    Observable<DiffUtil.DiffResult> onNotifyRequested() {
        return notifyDiffSubject;
    }

    /**
     * Calculates dataSet diff internally, to be able to dispatch notify events to adapter
     * automatically without any need to monitor insert/remove/move events explicitly
     * <p>
     * Uses Diff utils from support library internally
     * @param oldData old dataSet
     * @param newData new dataSet
     * @return diff result
     */
    private DiffUtil.DiffResult calculateDiff(final List<T> oldData, final List<T> newData) {
        return DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldData.size();
            }

            @Override
            public int getNewListSize() {
                return newData.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                final T oldItem = oldData.get(oldItemPosition);
                if (oldItem instanceof DiffResolver) {
                    //noinspection unchecked
                    return ((DiffResolver) oldItem).equalsItem(newData.get(newItemPosition));
                }
                return oldItem.equals(newData.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                final T oldItem = oldData.get(oldItemPosition);
                if (oldItem instanceof DiffResolver) {
                    //noinspection unchecked
                    return ((DiffResolver) oldItem).areContentsTheSame(newData.get(newItemPosition));
                }
                return oldItem.hashCode() == newData.get(newItemPosition).hashCode();
            }
        }, diffDetectMovement);
    }
}
