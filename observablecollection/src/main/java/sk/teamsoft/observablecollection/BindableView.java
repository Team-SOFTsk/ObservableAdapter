package sk.teamsoft.observablecollection;

import io.reactivex.Observable;

/**
 * Base View class used in Observable Adapter
 * This delegates bind mechanism directly to custom view instead of adapter implementation
 *
 * @author Dusan Bartos
 */
@SuppressWarnings("WeakerAccess")
public interface BindableView<T> {
    /**
     * Event invoked when viewHolder is attached with data object
     *
     * @param item adapter item
     */
    void bindItem(T item);

    /**
     * Return observable events (cast to Object class) here if you want to handle clicks and
     * other events from viewHolder
     * Output of this stream can be observed through {@link ObservableAdapter#onItemEvent()}
     *
     * @return observable events
     */
    Observable<Object> onObservableEvent();
}
