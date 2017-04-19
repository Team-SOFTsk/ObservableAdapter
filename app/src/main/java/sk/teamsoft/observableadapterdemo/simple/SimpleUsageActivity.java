package sk.teamsoft.observableadapterdemo.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import sk.teamsoft.observableadapterdemo.advanced.AdvancedUsageActivity;
import sk.teamsoft.observableadapterdemo.R;
import sk.teamsoft.observablecollection.ObservableAdapter;
import sk.teamsoft.observablecollection.SimpleAdapterSource;
import timber.log.Timber;

public class SimpleUsageActivity extends AppCompatActivity {

    private final SimpleAdapterSource<Data> source = new SimpleAdapterSource<>(Collections.<Data>emptyList(), R.layout.view_data);
    private final ObservableAdapter<Data> adapter = new ObservableAdapter<>(source);
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        source.setData(Arrays.asList(
                new Data("Data 1", "detail 1"),
                new Data("Data 2", "detail 2"),
                new Data("Data 3", "detail 3"),
                new Data("Data 4", "detail 4"),
                new Data("Data 5", "detail 5"),
                new Data("Data 6", "detail 6")
        ));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(SimpleUsageActivity.this, AdvancedUsageActivity.class));
            }
        });
    }

    @Override protected void onResume() {
        super.onResume();
        disposable.add(adapter.onItemEvent()
                .map(new Function<ObservableAdapter.ViewEvent, Object>() {
                    @Override
                    public Object apply(@NonNull ObservableAdapter.ViewEvent viewEvent) throws Exception {
                        return viewEvent.getData();
                    }
                })
                .ofType(Data.class)
                .subscribe(new Consumer<Data>() {
                    @Override public void accept(@NonNull Data data) throws Exception {
                        Toast.makeText(SimpleUsageActivity.this, "Clicked " + data.label, Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override public void accept(@NonNull Throwable throwable) throws Exception {
                        Timber.e(throwable, "Error watching adapter");
                    }
                }));
    }

    @Override protected void onPause() {
        disposable.clear();
        super.onPause();
    }
}
