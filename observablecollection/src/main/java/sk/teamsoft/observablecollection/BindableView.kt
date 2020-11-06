package sk.teamsoft.observablecollection

import io.reactivex.Observable

/**
 * Base View class used in Observable Adapter
 * This delegates bind mechanism directly to custom view instead of adapter implementation

 * @author Dusan Bartos
 */
interface BindableView<T> {
    /**
     * Event invoked when viewHolder is attached with data object

     * @param item adapter item
     */
    fun bindTo(item: T)

    /**
     * Return observable events (cast to Object class) here if you want to handle clicks and
     * other events from viewHolder
     * Output of this stream can be observed through [ObservableAdapter.onItemEvent]

     * @return observable events
     */
    val observableEvent: Observable<Any>?
}
