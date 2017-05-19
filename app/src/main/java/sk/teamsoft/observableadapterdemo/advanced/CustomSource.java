package sk.teamsoft.observableadapterdemo.advanced;

import java.util.List;

import sk.teamsoft.observableadapterdemo.R;
import sk.teamsoft.observablecollection.AdapterSource;

/**
 * @author Dusan Bartos
 *         Created on 19.04.2017.
 */

public class CustomSource extends AdapterSource<AdvancedData> {

    public CustomSource(List<AdvancedData> data) {
        super(data);
    }

    @Override protected int getViewType(int pos) {
        switch (get(pos).viewType) {
            case First:
                return 1;
            case Second:
                return 2;
            case Third:
            default:
                return 3;
        }
    }

    @Override public int getLayout(int viewType) {
        switch (viewType) {
            case 1:
                return R.layout.view_advanced_first;
            case 2:
                return R.layout.view_advanced_second;
            case 3:
                return R.layout.view_advanced_third;
            default:
                return -1;
        }
    }
}
