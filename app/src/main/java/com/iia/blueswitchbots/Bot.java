/**
 * Blue Switch Bots
 * Copyright (C) 2019 Ishraq Ibne Ashraf <ishraq.i.ashraf@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

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

    public String getKey() {
        return mKey;
    }

    public String getMac() {
        return mMac;
    }

    public String getName() {
        return mName;
    }

    public Boolean getIsEnabled() {
        return mIsEnabled;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void setMac(String mac) {
        mMac = mac;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setIsEnabled(Boolean isEnabled) {
        mIsEnabled = isEnabled;
    }
}
