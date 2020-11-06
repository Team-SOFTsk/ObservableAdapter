package sk.teamsoft.observableadapterdemo.advanced

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.disposables.CompositeDisposable
import sk.teamsoft.observableadapterdemo.R
import sk.teamsoft.observablecollection.ObservableAdapter
import kotlin.math.roundToInt

class AdvancedUsageActivity : AppCompatActivity() {

    private val source: CustomSource = CustomSource(emptyList())
    private val adapter: ObservableAdapter<AdvancedData> = ObservableAdapter(source)
    private val disposable = CompositeDisposable()

    private lateinit var data: List<AdvancedData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_usage)

        findViewById<RecyclerView>(R.id.list).let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
        }

        data = listOf(
            AdvancedData("Data 1", "detail 1", ViewType.First),
            AdvancedData("Data 2", "detail 2", ViewType.Second),
            AdvancedData("Data 3", "detail 3", ViewType.Second),
            AdvancedData("Data 4", "detail 4", ViewType.Second),
            AdvancedData("Data 5", "detail 5", ViewType.Third),
            AdvancedData("Data 6", "detail 6", ViewType.Third)
        )
        source.data = data

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val round = (Math.random() * (data.size - 1)).roundToInt()
            val d = data[round]
            d.viewType = ViewType.Mutated
            source.data = data
            Toast.makeText(this, "Mutation at $round", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        disposable.add(
            adapter.onItemEvent()
                .subscribe {
                    val message = when (it.data as EventType) {
                        EventType.Click -> "Clicked ${it.requireItem().label}"
                        EventType.LongClick -> "Long Clicked ${it.requireItem().label}"
                    }
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
        )
    }

    override fun onPause() {
        disposable.clear()
        super.onPause()
    }
}