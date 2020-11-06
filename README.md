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
```kotlin
val source = SimpleAdapterSource<Data>(emptyList(), R.layout.view_data)
```
2. Create Adapter and set to RecyclerView
```kotlin
recyclerView.adapter = ObservableAdapter(source)
```
3. Prepare View
```
<sk.teamsoft.observableadapterdemo.simple.DataView ...>
    <TextView android:id="@+id/data" .../>
</sk.teamsoft.observableadapterdemo.simple.DataView>
```
```kotlin
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
```

4. Set data
```kotlin
source.data = listOf(data1, data2, data3)
```

### Advanced use case

Take a look at [Advanced demo](https://github.com/Team-SOFTsk/ObservableAdapter/tree/master/app/src/main/java/sk/teamsoft/observableadapterdemo/advanced)

### Contact
Team-SOFT s.r.o.<br/>
[dusan@teamsoft.sk](mailto:dusan@teamsoft.sk)
