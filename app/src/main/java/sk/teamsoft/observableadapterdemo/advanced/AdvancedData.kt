package sk.teamsoft.observableadapterdemo.advanced

import androidx.annotation.NonNull
import sk.teamsoft.observablecollection.DiffResolver
import sk.teamsoft.observablecollection.MutableDiffResolver

/**
 * @author Dusan Bartos
 */
class AdvancedData(
    var label: String?,
    var detail: String?,
    var viewType: ViewType?
) : DiffResolver<AdvancedData>, MutableDiffResolver {

    override fun equalsItem(@NonNull other: AdvancedData): Boolean = viewType == other.viewType && label == other.label
    override fun areContentsTheSame(@NonNull other: AdvancedData): Boolean = detail == other.detail

    override fun valueHash(): Int {
        var result = if (label != null) label.hashCode() else 0
        result = 31 * result + if (detail != null) detail.hashCode() else 0
        result = 31 * result + if (viewType != null) viewType.hashCode() else 0
        return result
    }
}