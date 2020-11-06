package sk.teamsoft.observableadapterdemo.simple

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.disposables.CompositeDisposable
import sk.teamsoft.observableadapterdemo.R
import sk.teamsoft.observableadapterdemo.advanced.AdvancedUsageActivity
import sk.teamsoft.observablecollection.ObservableAdapter
import sk.teamsoft.observablecollection.SimpleAdapterSource
import timber.log.Timber

class SimpleUsageActivity : AppCompatActivity() {

    private val source: SimpleAdapterSource<Data> = SimpleAdapterSource(emptyList<Data>(), R.layout.view_data)
    private val adapter: ObservableAdapter<Data> = ObservableAdapter(source)
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        findViewById<RecyclerView>(R.id.list).let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
        }

        source.data = listOf(
            Data("Data 1", "detail 1"),
            Data("Data 2", "detail 2"),
            Data("Data 3", "detail 3"),
            Data("Data 4", "detail 4"),
            Data("Data 5", "detail 5"),
            Data("Data 6", "detail 6")
        )

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            startActivity(Intent(this, AdvancedUsageActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        disposable.add(
            adapter.onItemEvent()
                .map { it.requireItem() }
                .subscribe(
                    { data -> Toast.makeText(this, "Clicked ${data.label}", Toast.LENGTH_SHORT).show() },
                    { Timber.e(it, "Error watching adapter") })
        )
    }

    override fun onPause() {
        disposable.clear()
        super.onPause()
    }
}