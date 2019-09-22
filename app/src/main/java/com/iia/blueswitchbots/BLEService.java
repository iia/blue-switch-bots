package com.iia.blueswitchbots;

import android.util.Log;
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
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattCallback mGattCallback;

    static final String sysLogTag = String.format("<BSB :: %s>", BLEService.class.getName());

    @Override
    public void onCreate() {
        // Invoked if there are no existing running instances of the service.

        super.onCreate();

        mContext = this;

        // Fairness ensures FIFO during contention.
        mSyncSemaphore = new Semaphore(1, true);

        mBluetoothManager =
            (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);

                    if (newState == BluetoothGatt.STATE_CONNECTED) {
                        gatt.discoverServices();
                    }
                    else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                        gatt.close();

                        /**
                         * The following delay is required so that the connection closing can finish
                         * before next connection attempt.
                         */
                        try {
                            Thread.sleep(Constants.BLE_SERVICE_DELAY_AFTER_CLOSE);
                        }
                        catch (InterruptedException exception) {
                            Log.e(sysLogTag, exception.getMessage());
                        }
                        finally {
                            mSyncSemaphore.release();
                            stopSelf();
                        }
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);

                    Boolean wrote = false;

                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        for(BluetoothGattService s : gatt.getServices()) {
                            if (wrote) {
                                break;
                            }

                            for(BluetoothGattCharacteristic characteristic : s.getCharacteristics()) {
                                if (
                                    characteristic.getUuid().toString().equals(
                                        Constants.BLE_SERVICE_BOT_CONTROL_CHARACTERISTIC
                                    )
                                ) {
                                    characteristic.setValue(Constants.BLE_SERVICE_COMMAND_CLICK);
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
                ) {
                    super.onCharacteristicWrite(gatt, characteristic, status);

                    if (status != BluetoothGatt.GATT_SUCCESS) {
                        Log.e(
                                sysLogTag,
                            String.format(
                                "GATT characteristic write failed with status code: %d",
                                status
                            )
                        );
                    }

                    gatt.disconnect();
                }
            };
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        /**
         * Invoked each time a service is started.
         *
         * If there is a running instance of the service then a new object of the class is not
         * instantiated but this method will be invoked for each service run (when a 'SMS received'
         * broadcast is received).
         */

        super.onStartCommand(intent, flags, startId);

        BluetoothDevice bluetoothDevice =
            mBluetoothAdapter.getRemoteDevice(intent.getStringExtra("MAC"));

        try{
            mSyncSemaphore.acquire();
        }
        catch (InterruptedException exception) {
            Log.e(sysLogTag, exception.getMessage());

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
