package sk.teamsoft.observableadapterdemo.simple

import androidx.annotation.NonNull
import sk.teamsoft.observablecollection.DiffResolver

/**
 * @author Dusan Bartos
 */
data class Data(var label: String, var detail: String) : DiffResolver<Data> {

    override fun equalsItem(@NonNull other: Data): Boolean = label == other.label
    override fun areContentsTheSame(@NonNull other: Data): Boolean = detail == other.detail
}