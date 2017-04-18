package sk.teamsoft.observablecollection;

import android.support.annotation.LayoutRes;

import java.util.List;

/**
 * Simple {@link ObservableAdapter} Source implementation which requires only one view type
 * and therefore only one view layout
 *
 * @author Dusan Bartos
 */
public final class SimpleAdapterSource<T> extends AdapterSource<T> {
    @LayoutRes
    private final int layout;

    public SimpleAdapterSource(List<T> data, @LayoutRes int layout) {
        super(data);
        this.layout = layout;
    }

    @Override
    protected int getViewType(int pos) {
        return -1;
    }

    @Override
    public int getLayout(int viewType) {
        return layout;
    }
}
