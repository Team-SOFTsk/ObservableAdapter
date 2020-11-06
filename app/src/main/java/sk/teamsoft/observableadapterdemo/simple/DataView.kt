package sk.teamsoft.observableadapterdemo.simple

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import sk.teamsoft.observableadapterdemo.R
import sk.teamsoft.observablecollection.BindableView

/**
 * @author Dusan Bartos
 */
class DataView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), BindableView<Data> {

    private val label: TextView by lazy { findViewById(R.id.data) }
    private val detail: TextView by lazy { findViewById(R.id.data_detail) }

    override val observableEvent: Observable<Any>?
        get() = RxView.clicks(this)

    override fun bindTo(item: Data) {
        label.text = item.label
        detail.text = item.detail
    }
}