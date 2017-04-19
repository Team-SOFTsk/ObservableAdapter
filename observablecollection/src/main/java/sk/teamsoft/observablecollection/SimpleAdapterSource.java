package sk.teamsoft.observablecollection;

import android.support.annotation.LayoutRes;

import java.util.List;

/**
 * Simple {@link ObservableAdapter} Source implementation which requires only one view type
 * and therefore only one view layout
 * @author Dusan Bartos
 */
@SuppressWarnings("WeakerAccess")
public final class SimpleAdapterSource<T> extends AdapterSource<T> {
    @LayoutRes private final int layout;

    /**
     * Creates simple source for {@link ObservableAdapter} with just single view type
     * Uses 'true' flag for resolving movement in DiffUtil
     * @param data   default data set
     * @param layout viewholder layout resource
     */
    public SimpleAdapterSource(List<T> data, @LayoutRes int layout) {
        this(data, layout, false);
    }

    /**
     * Creates simple source for {@link ObservableAdapter} with just single view type
     * @param data           default data set
     * @param layout         viewholder layout resource
     * @param detectMovement false to disable movement detection during updates (for better performance)
     */
    public SimpleAdapterSource(List<T> data, @LayoutRes int layout, boolean detectMovement) {
        super(data, detectMovement);
        this.layout = layout;
    }

    @Override protected int getViewType(int pos) {
        return -1;
    }

    @Override public int getLayout(int viewType) {
        return layout;
    }
}
