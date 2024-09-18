package com.example.finalnews.newsmaterial.model;

import java.util.ArrayList;

public class Root{
    public Meta meta;
    public ArrayList<Datum> data;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }
}
