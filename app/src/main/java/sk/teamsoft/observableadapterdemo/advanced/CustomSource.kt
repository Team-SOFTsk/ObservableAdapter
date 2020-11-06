package sk.teamsoft.observableadapterdemo.advanced

import sk.teamsoft.observableadapterdemo.R
import sk.teamsoft.observablecollection.AdapterSource

/**
 * @author Dusan Bartos
 */
class CustomSource(data: List<AdvancedData>) : AdapterSource<AdvancedData>(data, false) {

    override fun getViewType(pos: Int): Int {
        return when (get(pos)!!.viewType) {
            ViewType.First -> 1
            ViewType.Second -> 2
            ViewType.Third -> 3
            else -> 3
        }
    }

    override fun getLayout(viewType: Int): Int {
        return when (viewType) {
            1 -> R.layout.view_advanced_first
            2 -> R.layout.view_advanced_second
            3 -> R.layout.view_advanced_third
            else -> -1
        }
    }
}