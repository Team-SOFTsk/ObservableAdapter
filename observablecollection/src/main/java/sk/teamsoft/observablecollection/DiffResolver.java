package sk.teamsoft.observablecollection;

/**
 * Interface intended for non-trivial comparison for DiffUtils
 * If object does not implement this interface, it will be compared by default with
 * {@link Object#equals(Object)} and {@link Object#hashCode()} for reference and content
 * comparison respectively
 * @author Dusan Bartos
 */
@SuppressWarnings("WeakerAccess")
public interface DiffResolver<T> {

    /**
     * Method used to determine item equality
     * Determines it viewHolder should be replaced
     * @param other new item to compare with the old one
     * @return true if objects are considered equal
     */
    boolean equalsItem(T other);

    /**
     * Method used to determine contents equality
     * Determines if viewHolder should be updated
     * This is only checked if {@link #equalsItem(Object)} returns true
     * @param other new item to compare with the old one
     * @return true if contents of these objects are equal
     */
    boolean areContentsTheSame(T other);
}
