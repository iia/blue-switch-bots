package com.iia.blueswitchbots;

public class Bot {
    private String mKey;
    private String mMac;
    private String mName;
    private Boolean mIsEnabled;

    public Bot(String key, String mac, String name, Boolean isEnabled) {
        mKey = key;
        mMac = mac;
        mName = name;
        mIsEnabled = isEnabled;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setMAC(String mac) {
        mMac = mac;
    }

    public void setIsEnabled(Boolean isEnabled) {
        mIsEnabled = isEnabled;
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

    public String getMAC() {
        return mMac;
    }

    public Boolean getIsEnabled() {
        return mIsEnabled;
    }
}
