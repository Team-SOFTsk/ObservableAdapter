package sk.teamsoft.observablecollection

import androidx.annotation.LayoutRes

/**
 * Simple [ObservableAdapter] Source implementation which requires only one view type
 * and therefore only one view layout

 * @author Dusan Bartos
 */
class SimpleAdapterSource<T> constructor(
    data: List<T>,
    @LayoutRes private val layout: Int,
    detectMove: Boolean = false
) : AdapterSource<T>(data, detectMove) {

    constructor(data: List<T>, @LayoutRes layout: Int) : this(data, layout, false)
    constructor(@LayoutRes layout: Int) : this(emptyList(), layout, false)

    override fun getViewType(pos: Int) = -1
    override fun getLayout(viewType: Int) = layout
}
