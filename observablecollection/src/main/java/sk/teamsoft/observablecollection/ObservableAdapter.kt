package sk.teamsoft.observablecollection

import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * Observable Adapter is a generic class for use with the recyclerView
 *
 * This class separates UI related code from Data source logic.
 *
 * usage:
 * - create an implementation of abstract [AdapterSource] class
 * - pass this source into the constructor of this class
 * - when modifying data source, list is notified appropriately
 * @author Dusan Bartos
 */
class ObservableAdapter<T>(private val source: AdapterSource<T>) : RecyclerView.Adapter<ObservableAdapter.ViewHolder<T>>() {
    private val itemEvent = PublishSubject.create<ObjectEvent<T>>()
    private val requestScrollEvent = PublishSubject.create<Boolean>()
    private val changeWatcher = CompositeDisposable()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        val layout = source.getLayout(viewType)
        try {
            val v = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            @Suppress("UNCHECKED_CAST")
            return ViewHolder(v as BindableView<T>)
        } catch (e: InflateException) {
            Timber.e(e, "Error inflating view %s %s", layout, parent.context.resources.getResourceEntryName(layout))
            throw e
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bindTo(source[position]!!)
    }

    override fun getItemViewType(position: Int): Int = source.getViewType(position)

    override fun getItemCount(): Int = source.count

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        notifyDataSetChanged()
        changeWatcher.clear()
        changeWatcher.add(source.onNotifyRequested()
//                .onBackpressureLatest()
                .subscribe({ diffResult ->
                    diffResult.dispatchUpdatesTo(this)
                    diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
                        override fun onInserted(position: Int, count: Int) {
                            if (position == 0) {
                                requestScrollEvent.onNext(java.lang.Boolean.TRUE)
                            }
                        }

                        override fun onRemoved(position: Int, count: Int) {}
                        override fun onMoved(fromPosition: Int, toPosition: Int) {}
                        override fun onChanged(position: Int, count: Int, payload: Any?) {}
                    })
                }, { Timber.e(it, "Error watching source data") }))
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        changeWatcher.clear()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder<T>) {
        super.onViewAttachedToWindow(holder)
        holder.holderView.observableEvent?.let { objectObservable ->
            //reattach item reference when holder shown without bind change event
            val adapterPosition = holder.adapterPosition
            if (holder.item == null) {
                holder.reAttach(source[adapterPosition]!!)
            }

            holder.disposable = objectObservable
                    .filter { it != null }
                    .map { ObjectEvent(holder, it) }
                    .doOnError { e -> Timber.e("ObservableAdapter Error: %s", e.message) }
                    .subscribe({ itemEvent.onNext(it) }, { Timber.e(it, "Error:viewHolder:event") })
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder<T>) {
        holder.detach()
        super.onViewDetachedFromWindow(holder)
    }

    override fun onViewRecycled(holder: ViewHolder<T>) {
        holder.recycle()
        super.onViewRecycled(holder)
    }

    fun onRequestScrollUp(): Observable<Boolean> = requestScrollEvent.delay(50, TimeUnit.MILLISECONDS)

    /**
     * Generic method which dispatches viewHolder events (click, longtap, whatever) to
     * the listeners (typically presenters).
     * Data object is of type Object, but the real type depends on an implementation inside
     * specific view (makes use of [BindableView.getObservableEvent] method)
     * @return observable event dispatcher
     */
    fun onItemEvent(): Observable<ObjectEvent<T>> = itemEvent

    /**
     * Generic ViewHolder for the purposes of ObservableAdapter use
     */
    class ViewHolder<T> internal constructor(
        internal val holderView: BindableView<T>
    ) : RecyclerView.ViewHolder(autoCast(holderView)) {

        companion object {
            /**
             * AutoCasts interface to [View] type
             * @return View to construct ViewHolder
             */
            fun autoCast(view: BindableView<*>): View {
                if (view is View) {
                    return view
                }
                throw IllegalStateException("BindableView cannot be cast automatically [$view]")
            }
        }

        var item: T? = null
            private set

        internal var disposable: Disposable? = null

        internal fun bindTo(item: T) {
            this.item = item
            holderView.bindTo(item)

            (holderView as? SubscribeableView<T>)?.subscribe(item)
        }

        internal fun recycle() = detach()

        internal fun reAttach(item: T) {
            this.item = item

            (holderView as? SubscribeableView<T>)?.subscribe(item)
        }

        internal fun detach() {
            this.item = null

            disposable?.takeIf { !it.isDisposed }?.dispose()
            disposable = null

            (holderView as? SubscribeableView<*>)?.unsubscribe()
        }

        override fun toString() = "ViewHolder{view=$holderView,position=$adapterPosition,item=$item}"
    }

    /**
     * Handler class for adapter item events
     */
    class ObjectEvent<T> internal constructor(holder: ViewHolder<T>, val data: Any) {

        internal val holder: WeakReference<ViewHolder<T>> = WeakReference(holder)

        val view: ViewHolder<T>? get() = holder.get()
        val item: T? get() = view?.item

        fun requireView(): ViewHolder<T> = requireNotNull(view) { "View not attached to event" }
        fun requireItem(): T = requireNotNull(item) { "Item not attached to event" }

        override fun toString(): String {
            return "ObjectEvent{" +
                    "holder=" + holder.get() +
                    ",data=" + data +
                    '}'
        }
    }
}
