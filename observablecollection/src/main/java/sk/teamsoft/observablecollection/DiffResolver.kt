package sk.teamsoft.observablecollection

/**
 * Interface intended for non-trivial comparison for DiffUtils
 * If object does not implement this interface, it will be compared by default with
 * [Object.equals] and [Object.hashCode] for reference and content
 * comparison respectively
 * @author Dusan Bartos
 */
interface DiffResolver<T> {
    /**
     * Method used to determine item equality
     * Determines it viewHolder should be replaced
     * @param other new item to compare with the old one
     * @return true if objects are considered equal
     */
    fun equalsItem(other: T): Boolean

    /**
     * Method used to determine contents equality
     * Determines if viewHolder should be updated
     * This is only checked if [.equalsItem] returns true
     * @param other new item to compare with the old one
     * @return true if contents of these objects are equal
     */
    fun areContentsTheSame(other: T): Boolean
}