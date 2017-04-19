package sk.teamsoft.observableadapterdemo.advanced;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import sk.teamsoft.observableadapterdemo.R;
import sk.teamsoft.observablecollection.BindableView;

/**
 * @author Dusan Bartos
 *         Created on 15.04.2017.
 */

public class AdvancedDataView extends LinearLayout implements BindableView<AdvancedData> {

    /**
     * No need for strong reference, because internal viewHolder implementation
     * keeps strong reference to shown item
     */
    private WeakReference<AdvancedData> itemReference;

    private TextView label;

    public AdvancedDataView(@NonNull Context context) {
        super(context);
    }

    public AdvancedDataView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AdvancedDataView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        label = (TextView) findViewById(R.id.data);
    }

    @Override public void bindItem(AdvancedData item) {
        label.setText(item.label);

        itemReference = new WeakReference<AdvancedData>(item);
    }

    @Override public Observable<Object> onObservableEvent() {
        return Observable.merge(
                RxView.clicks(this)
                        .map(new Function<Object, Object>() {
                            @Override
                            public Object apply(@io.reactivex.annotations.NonNull Object o) throws Exception {
                                return new EventTypeObject(EventType.Click, itemReference.get());
                            }
                        }),
                RxView.longClicks(this)
                        .map(new Function<Object, Object>() {
                            @Override
                            public Object apply(@io.reactivex.annotations.NonNull Object o) throws Exception {
                                return new EventTypeObject(EventType.LongClick, itemReference.get());
                            }
                        })
        );
    }
}
