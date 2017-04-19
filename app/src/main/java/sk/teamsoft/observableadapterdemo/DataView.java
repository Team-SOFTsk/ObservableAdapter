package sk.teamsoft.observableadapterdemo;

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
import sk.teamsoft.observablecollection.BindableView;

/**
 * @author Dusan Bartos
 *         Created on 15.04.2017.
 */

public class DataView extends LinearLayout implements BindableView<Data> {

    /**
     * No need for strong reference, because internal viewHolder implementation
     * keeps strong reference to shown item
     */
    private WeakReference<Data> itemReference;

    private TextView label;
    private TextView detail;

    public DataView(@NonNull Context context) {
        super(context);
    }

    public DataView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DataView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        label = (TextView) findViewById(R.id.data);
        detail = (TextView) findViewById(R.id.data_detail);
    }

    @Override public void bindItem(Data item) {
        label.setText(item.label);
        detail.setText(item.detail);

        itemReference = new WeakReference<Data>(item);
    }

    @Override public Observable<Object> onObservableEvent() {
        return RxView.clicks(this)
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(@io.reactivex.annotations.NonNull Object o) throws Exception {
                        return itemReference.get();
                    }
                });
    }
}
