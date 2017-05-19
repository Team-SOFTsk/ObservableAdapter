package sk.teamsoft.observableadapterdemo.advanced;

import android.support.annotation.NonNull;

import sk.teamsoft.observablecollection.DiffResolver;
import sk.teamsoft.observablecollection.MutableDiffResolver;

/**
 * @author Dusan Bartos
 *         Created on 19.04.2017.
 */

public class AdvancedData implements DiffResolver<AdvancedData>, MutableDiffResolver {
    public String label;
    public String detail;
    public ViewType viewType;

    public AdvancedData(String label, String detail, ViewType viewType) {
        this.label = label;
        this.detail = detail;
        this.viewType = viewType;
    }

    @Override public boolean equalsItem(@NonNull AdvancedData other) {
        return viewType.equals(other.viewType) && label.equals(other.label);
    }

    @Override public boolean areContentsTheSame(@NonNull AdvancedData other) {
        return detail.equals(other.detail);
    }

    @Override public int valueHash() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (detail != null ? detail.hashCode() : 0);
        result = 31 * result + (viewType != null ? viewType.hashCode() : 0);
        return result;
    }
}
