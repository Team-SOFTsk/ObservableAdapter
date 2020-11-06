package sk.teamsoft.observablecollection

/**
 * Mutable Diff Resolver is used for diff computation in adapter with mutable data
 *
 * Since you can not compare values of mutable data directly (since the object reference is the same)
 * this interface provides a caching method for hash of values, so we can compare them accordingly
 *
 * When adapter is notified about changes and runs diff-calculating algorithm, we cache values
 * of items which implement [MutableDiffResolver], and the next time change is detected,
 * we compare those cached values with new ones (to determine if item contents changed).
 *
 * This is only used in android.support.v7.util.DiffUtil.Callback#areContentsTheSame(int, int)
 * so only for items, which return true in android.support.v7.util.DiffUtil.Callback#areItemsTheSame(int, int)
 *
 * Implementation of this resolver is very similar to generating/writing [Object.hashCode]
 * method for regular data objects
 *
 * Before using this class, consider if you really want to use mutable data in the source directly,
 * and if using a mutable-to-immutable converter before setting the data to source would not be
 * more efficient
 *
 * @author Dusan Bartos
 */
interface MutableDiffResolver {
    /**
     * @return hashed value, which will be cached and compared with current values once adapter
     * is notified about changes
     */
    fun valueHash(): Int
}