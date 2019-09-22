package com.iia.blueswitchbots;

import androidx.collection.ArrayMap;

public class Bot {
    private String mName;
    private String mAddress;

    public Bot(String name, String address) {
        this.mName = name;
        this.mAddress = address;
    }

    public String getmName() {
        return mName;
    }

    public String getmAddress() {
        return mAddress;
    }
}
