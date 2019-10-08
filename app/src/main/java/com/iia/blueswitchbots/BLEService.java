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

import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import androidx.annotation.Nullable;
import java.util.concurrent.Semaphore;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;

public class BLEService extends Service {
    private Context mContext;
    private Semaphore mSyncSemaphore;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattCallback mGattCallback;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        mSyncSemaphore = new Semaphore(1, true);

        BluetoothManager bluetoothManager =
            (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);

                    if (newState == BluetoothGatt.STATE_CONNECTED) {
                        gatt.discoverServices();
                    }
                    else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                        gatt.abortReliableWrite();
                        gatt.close();

                        try {
                            Thread.sleep(Constants.BLE_DELAY_CONNECTION_CLOSE_AFTER);
                        }
                        catch (InterruptedException exception) {}
                        finally {
                            mSyncSemaphore.release();
                        }
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);

                    Boolean wrote = false;

                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        for(BluetoothGattService bluetoothGattService : gatt.getServices()) {
                            if (wrote) {
                                break;
                            }

                            for(BluetoothGattCharacteristic characteristic : bluetoothGattService.getCharacteristics()) {
                                if (
                                    characteristic.getUuid().toString().equals(
                                        Constants.BLE_UUID_CHARACTERISTIC_BOT_CONTROL
                                    )
                                )
                                {
                                    characteristic.setValue(Constants.BLE_COMMAND_BOT_CLICK);
                                    gatt.writeCharacteristic(characteristic);

                                    wrote = true;

                                    break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCharacteristicWrite(
                    BluetoothGatt gatt,
                    BluetoothGattCharacteristic characteristic,
                    int status
                )
                {
                    super.onCharacteristicWrite(gatt, characteristic, status);

                    if (characteristic.getValue().equals(Constants.BLE_COMMAND_BOT_CLICK)) {
                        gatt.executeReliableWrite();
                    }
                    else {
                        gatt.disconnect();
                    }
                }

                @Override
                public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                    super.onReliableWriteCompleted(gatt, status);

                    gatt.disconnect();
                }
            };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        BluetoothDevice bluetoothDevice =
            mBluetoothAdapter.getRemoteDevice(
                intent.getStringExtra(Constants.BLE_SERVICE_INTENT_EXTRA_MAC)
            );

        try {
            mSyncSemaphore.acquire();
        }
        catch (InterruptedException exception) {
            stopSelf();

            return Service.START_NOT_STICKY;
        }

        bluetoothDevice.connectGatt(mContext, false, mGattCallback);

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
