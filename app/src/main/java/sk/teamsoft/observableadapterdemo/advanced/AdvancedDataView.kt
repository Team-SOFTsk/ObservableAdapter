package sk.teamsoft.observableadapterdemo.advanced

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
class AdvancedDataView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), BindableView<AdvancedData> {

    private val label: TextView by lazy { findViewById(R.id.data) }
    private val detail: TextView by lazy { findViewById(R.id.data_detail) }

    override val observableEvent: Observable<Any>?
        get() = Observable.merge(
            RxView.clicks(this).map { EventType.Click },
            RxView.longClicks(this).map { EventType.LongClick }
        )

    override fun bindTo(item: AdvancedData) {
        label.text = "${item.label}[${item.viewType}]"
        detail.text = item.detail
    }
}