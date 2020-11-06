package sk.teamsoft.observablecollection

/**
 * @author Dusan Bartos
 */
interface SubscribeableView<T> {
    fun subscribe(item: T)
    fun unsubscribe()
}
