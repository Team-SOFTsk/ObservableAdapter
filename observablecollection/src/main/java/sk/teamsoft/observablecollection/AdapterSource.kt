package sk.teamsoft.observablecollection

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Data source for ObservableAdapter
 * Automatically notifies adapter when data is changed
 * It can implement different layouts in single adapter - similar to basic
 * recyclerView's adapter
 *
 *
 * If you need just a simple source with one view type and one layout, check [SimpleAdapterSource]
 * @author Dusan Bartos
 * *
 * @see .getViewType
 * @see .getLayout
 */
abstract class AdapterSource<T> private constructor(protected var _data: List<T>) {

    private val dataChangeSubject = PublishSubject.create<List<T>>()
    private val notifyDiffSubject = PublishSubject.create<DiffUtil.DiffResult>()
    private var valueHashCache: List<Int?>? = null

    /**
     * Parameter which determines if diffUtils checks for movement
     * This is true by default, but if you need extra optimization (i.e. when there is no need
     * for movement check - when only remove/add actions are possible)
     */
    private var diffDetectMovement = true

    protected constructor(data: List<T>, detectMovement: Boolean) : this(data) {
        diffDetectMovement = detectMovement
    }

    var data: List<T>
        get() = _data
        set(value) {
            val diffResult = calculateDiff(this._data, value, valueHashCache)
            this._data = value
            this.valueHashCache = createValueHashList()
            notifyDiffSubject.onNext(diffResult)
            dataChangeSubject.onNext(value)
        }

    operator fun get(position: Int): T? {
        if (position >= 0 && position < data.size) {
            return data[position]
        } else {
            return null
        }
    }

    /**
     * Implement viewType determination from the data item at specific position
     */
    abstract fun getViewType(pos: Int): Int

    /**
     * Implement resource layout type determination regarding its viewType
     */
    @LayoutRes
    abstract fun getLayout(viewType: Int): Int

    val count: Int
        get() = data.size

    /**
     * Change notifier
     * @return Observable dataSet
     */
    fun onDataChange(): Observable<List<T>> = dataChangeSubject

    /**
     * Internal helper to tell adapter that it needs to be notified about changes from diff
     * result
     * @return observable diff result
     */
    fun onNotifyRequested(): Observable<DiffUtil.DiffResult> = notifyDiffSubject

    /**
     * Prepares cached value for MutableResolvers
     * @return list of hashes
     */
    private fun createValueHashList(): List<Int?> {
        val list = ArrayList<Int?>(data.size)
        for (item in data) {
            //in case there are multiple types of data in adapter, and only some of them implement
            //MutableDiffResolver, we need to check each item's type
            if (item is MutableDiffResolver) {
                list.add((item as MutableDiffResolver).valueHash())
            } else {
                list.add(null)
            }
        }
        return list
    }

    /**
     * Calculates dataSet diff internally, to be able to dispatch notify events to adapter
     * automatically without any need to monitor insert/remove/move events explicitly
     *
     * Uses Diff utils from support library internally
     * @param oldData old dataSet
     * @param newData new dataSet
     * @return diff result
     */
    private fun calculateDiff(
        oldData: List<T>,
        newData: List<T>,
        cachedValueHashes: List<Int?>?
    ): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            private fun cached(position: Int): Int = cachedValueHashes?.get(position) ?: -1
            override fun getOldListSize(): Int = oldData.size
            override fun getNewListSize(): Int = newData.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldData[oldItemPosition]
                if (oldItem is DiffResolver<*>) {
                    @Suppress("UNCHECKED_CAST")
                    return (oldItem as DiffResolver<T>).equalsItem(newData[newItemPosition])
                }
                return oldItem == newData[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldData[oldItemPosition]
                val newItem = newData[newItemPosition]
                if (newItem is MutableDiffResolver) {
                    return (newItem as MutableDiffResolver).valueHash() == cached(oldItemPosition)
                } else if (oldItem is DiffResolver<*>) {
                    @Suppress("UNCHECKED_CAST")
                    return (oldItem as DiffResolver<T>).areContentsTheSame(newItem)
                }
                return oldItem?.hashCode() == newItem?.hashCode()
            }
        }, diffDetectMovement)
    }
}
