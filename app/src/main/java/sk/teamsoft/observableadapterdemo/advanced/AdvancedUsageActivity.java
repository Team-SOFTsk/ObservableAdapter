package sk.teamsoft.observableadapterdemo.advanced;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import sk.teamsoft.observableadapterdemo.R;
import sk.teamsoft.observablecollection.ObservableAdapter;
import timber.log.Timber;

public class AdvancedUsageActivity extends AppCompatActivity {

    private final CustomSource source = new CustomSource(Collections.<AdvancedData>emptyList());
    private final ObservableAdapter<AdvancedData> adapter = new ObservableAdapter<>(source);
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_usage);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        source.setData(Arrays.asList(
                new AdvancedData("Data 1", "detail 1", ViewType.First),
                new AdvancedData("Data 2", "detail 2", ViewType.Second),
                new AdvancedData("Data 3", "detail 3", ViewType.Second),
                new AdvancedData("Data 4", "detail 4", ViewType.Second),
                new AdvancedData("Data 5", "detail 5", ViewType.Third),
                new AdvancedData("Data 6", "detail 6", ViewType.Third)
        ));
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
                .ofType(EventTypeObject.class)
                .map(new Function<EventTypeObject, Observable<EventTypeObject>>() {
                    @Override
                    public Observable<EventTypeObject> apply(@NonNull EventTypeObject eventTypeObject) throws Exception {
                        return Observable.just(eventTypeObject);
                    }
                })
                .switchMap(new Function<Observable<EventTypeObject>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Observable<EventTypeObject> eventTypeObjectObservable) throws Exception {
                        return Observable.merge(
                                eventTypeObjectObservable
                                        .filter(new Predicate<EventTypeObject>() {
                                            @Override
                                            public boolean test(@NonNull EventTypeObject eventTypeObject) throws Exception {
                                                return EventType.Click.equals(eventTypeObject.getEventType());
                                            }
                                        })
                                        .map(new Function<EventTypeObject, String>() {
                                            @Override
                                            public String apply(@NonNull EventTypeObject eventTypeObject) throws Exception {
                                                return "Clicked " + ((AdvancedData) eventTypeObject.getData()).label;
                                            }
                                        }),
                                eventTypeObjectObservable
                                        .filter(new Predicate<EventTypeObject>() {
                                            @Override
                                            public boolean test(@NonNull EventTypeObject eventTypeObject) throws Exception {
                                                return EventType.LongClick.equals(eventTypeObject.getEventType());
                                            }
                                        })
                                        .map(new Function<EventTypeObject, String>() {
                                            @Override
                                            public String apply(@NonNull EventTypeObject eventTypeObject) throws Exception {
                                                return "Long Clicked " + ((AdvancedData) eventTypeObject.getData()).label;
                                            }
                                        })
                        );
                    }
                })
                .ofType(String.class)
                .subscribe(new Consumer<String>() {
                    @Override public void accept(@NonNull String data) throws Exception {
                        Toast.makeText(AdvancedUsageActivity.this, data, Toast.LENGTH_SHORT).show();
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
