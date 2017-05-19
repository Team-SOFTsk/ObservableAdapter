# ObservableCollection
RecyclerView Adapter for RxJava, separates list's model from Android framework dependencies

[ ![Download](https://api.bintray.com/packages/team-softsk/maven/observablecollection/images/download.svg) ](https://bintray.com/team-softsk/maven/observablecollection/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/sk.teamsoft/observablecollection/badge.svg)](https://maven-badges.herokuapp.com/maven-central/sk.teamsoft/observablecollection)

## Introduction
Supports RxJava2 and automatic adapter notify dispatching (via DiffUtils comparison)

## Usage

First, import the library via gradle dependency
```
compile 'sk.teamsoft:observablecollection:{latest_version}'
```

Check Sample app for both simple and advanced use-cases.
 
### Simple case

1. Create source (here, `Data` is the model class)
```
SimpleAdapterSource<Data> source = new SimpleAdapterSource<>(Collections.<Data>emptyList(), R.layout.view_data);
```
2. Create Adapter and set to RecyclerView
```
recyclerView = (RecyclerView) findViewById(...)             //or use Butterknife
recyclerView.setAdapter(new ObservableAdapter<>(source));
```
3. Prepare View
```
<sk.teamsoft.observableadapterdemo.simple.DataView ...>
    <TextView android:id="@+id/data" .../>
</sk.teamsoft.observableadapterdemo.simple.DataView>
```
```
public class DataView extends LinearLayout implements BindableView<Data> {

    private TextView label;

    public DataView(@NonNull Context context) {
        super(context);
    }

    ...other constructors...

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        label = (TextView) findViewById(R.id.data);
    }

    @Override public void bindItem(Data item) {
        label.setText(item.label);
    }

    @Override public Observable<Object> onObservableEvent() {
        return null;
    }
}
```

4. Set data
```
source.setData(data1);
...
source.setData(data2);
...
source.setData(data3);
```

### Advanced use case

Take a look at [Advanced demo](https://github.com/Team-SOFTsk/ObservableAdapter/tree/master/app/src/main/java/sk/teamsoft/observableadapterdemo/advanced)

### Looking for RxJava1 version?
Coming soon... contact us for more info

### Looking for Kotlin version?
Coming soon... contact us for more info

### Contact
Team-SOFT s.r.o.<br/>
[dusan@teamsoft.sk](mailto:dusan@teamsoft.sk)
