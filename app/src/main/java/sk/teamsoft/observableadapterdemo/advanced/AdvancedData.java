package sk.teamsoft.observableadapterdemo.advanced;

import sk.teamsoft.observablecollection.DiffResolver;

/**
 * @author Dusan Bartos
 *         Created on 19.04.2017.
 */

public class AdvancedData implements DiffResolver<AdvancedData> {
    public String label;
    public String detail;
    public ViewType viewType;

    public AdvancedData(String label, String detail, ViewType viewType) {
        this.label = label;
        this.detail = detail;
        this.viewType = viewType;
    }

    @Override public boolean equalsItem(AdvancedData other) {
        return viewType.equals(other.viewType) && label.equals(other.label);
    }

    @Override public boolean areContentsTheSame(AdvancedData other) {
        return detail.equals(other.detail);
    }
}