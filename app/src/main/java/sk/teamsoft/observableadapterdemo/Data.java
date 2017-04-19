package sk.teamsoft.observableadapterdemo;

import sk.teamsoft.observablecollection.DiffResolver;

/**
 * @author Dusan Bartos
 *         Created on 15.04.2017.
 */

public class Data implements DiffResolver<Data> {
    public String label;
    public String detail;

    public Data(String label, String detail) {
        this.label = label;
        this.detail = detail;
    }

    @Override public boolean equalsItem(Data other) {
        return label.equals(other.label);
    }

    @Override public boolean areContentsTheSame(Data other) {
        return detail.equals(other.detail);
    }
}
