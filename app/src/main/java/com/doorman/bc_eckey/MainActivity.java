package com.doorman.bc_eckey;

import java.util.ArrayList;
import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    private BroadcastReceiver mReceiver;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // do something if you want } else { Toast.makeText(getApplicationContext(), "DYING ....... ", Toast.LENGTH_LONG).show();
        } else {
                sleep(500);

                // .getButton(DialogInterface.BUTTON_POSITIVE).performClick();

                // int pid = android.os.Process.myPid();
                // android.os.Process.sendSignal(pid, android.os.Process.SIGNAL_KILL);
                // finish();
                return;
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button turnOnEC = (Button) findViewById(R.id.turnONEC);
        final Button discoverableEC = (Button) findViewById(R.id.discoverableEC);
        final Button turnOffEC = (Button) findViewById(R.id.turnOFFEC);
        final Button openEC = (Button) findViewById(R.id.openEC);
        final Button openQuitEC = (Button) findViewById(R.id.openQuitEC);
        final Button quitEC = (Button) findViewById(R.id.quitEC);
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final Button[] btnList = { turnOnEC, turnOffEC, openQuitEC, openEC };

        final Boolean btOn = mBluetoothAdapter.isEnabled();

        btnEnable(btnList, false);
        discoverableEC.setEnabled(false);
        if (btOn) {
            discoverableEC.setEnabled(true);
            openEC.setEnabled(true);
            openQuitEC.setEnabled(true);
            turnOffEC.setEnabled(true);
        } else {
            turnOnEC.setEnabled(true);
        }

        final ArrayList<Object> NewDeviceList = new ArrayList<Object>();

        final Activity activity = this;

        // Broadcast Receiver will find the Available devices and the discovery finished
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Toast.makeText(getApplicationContext(), "BROADCASTING ....... " + action, Toast.LENGTH_SHORT).show();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action.trim())) {
                    // Toast.makeText(getApplicationContext(), "FOUND ....... " +action, Toast.LENGTH_LONG).show();
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // If it's already paired, skip it, because it's been listed already

                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        // Toast.makeText(getApplicationContext(), "ADD ....... " +device.getName(), Toast.LENGTH_LONG).show();
                        NewDeviceList.add(device);
                    }

                    // When discovery is finished, change the Activity title
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Toast.makeText(getApplicationContext(), "FINISHED .......", Toast.LENGTH_SHORT).show();
                    if (NewDeviceList.isEmpty() == true) {
                        Toast.makeText(getApplicationContext(), "NO NEW DEVICES ....... ", Toast.LENGTH_LONG).show();

                    }
                    // Toast.makeText(getApplicationContext(), "FINISHED ....... " +NewDeviceList.size(), Toast.LENGTH_LONG).show();
                    NewDeviceList.add("End");
                    unregisterReceiver(mBluetoothAdapter, activity);
                    btnEnable(btnList, true);
                    discoverableEC.setEnabled(true);
                }
            }

        };

        openEC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!mBluetoothAdapter.isDiscovering()) {
                    btnEnable(btnList, false);
                    discoverableEC.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "OPENING .......", Toast.LENGTH_LONG).show();
                    // startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), 0);
                    discover(activity, mBluetoothAdapter);
                    btnEnable(btnList, true);
                    discoverableEC.setEnabled(true);
                } else
                    Toast.makeText(getApplicationContext(), "Accessing building, please wait .......", Toast.LENGTH_LONG).show();
            }
        });

        openQuitEC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!mBluetoothAdapter.isDiscovering()) {
                    btnEnable(btnList, false);
                    discoverableEC.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "OPENING .......", Toast.LENGTH_LONG).show();
                    // startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), 0);
                    discover(activity, mBluetoothAdapter);
                    sleep(5000);
                    goodbye(btOn, mBluetoothAdapter);
                } else
                    Toast.makeText(getApplicationContext(), "Accessing building, please wait .......", Toast.LENGTH_LONG).show();
            }
        });

        turnOnEC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!mBluetoothAdapter.isEnabled()) {
                    btnEnable(btnList, false);
                    discoverableEC.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "TURNING BLUETOOTH ON", Toast.LENGTH_LONG).show();
                    // Intent enableBtIntent = new
                    // Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    // startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
                    mBluetoothAdapter.enable();
                    discoverableEC.setEnabled(true);
                    btnEnable(btnList, true);
                } else
                    btnEnable(btnList, true);
            }
        });

        discoverableEC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!mBluetoothAdapter.isDiscovering()) {
                    btnEnable(btnList, false);
                    discoverableEC.setEnabled(false);
                    final IntentFilter actionFoundFilter;
                    final IntentFilter discoveryFinishedFilter;

                    Toast.makeText(getApplicationContext(), "VERIFYING .......", Toast.LENGTH_LONG).show();

                    actionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    activity.registerReceiver(mReceiver, actionFoundFilter);
                    // Don't forget to unregister during onDestroy
                    Toast.makeText(getApplicationContext(), "FILTER .......", Toast.LENGTH_SHORT).show();
                    discoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    activity.registerReceiver(mReceiver, discoveryFinishedFilter);
                    // Don't forget to unregister during onDestroy

                    final Set<BluetoothDevice> pairedDevices;
                    final ArrayList<BluetoothDevice> BondedDeviceList = new ArrayList<BluetoothDevice>();

                    // queryPairedDevice();
                    pairedDevices = mBluetoothAdapter.getBondedDevices();
                    Toast.makeText(getApplicationContext(), "# PAIRED ....... " + pairedDevices.size(), Toast.LENGTH_SHORT).show();
                    // If there are paired devices
                    if (pairedDevices.size() == 0) {
                        // No Bonded Devices
                        Toast.makeText(getApplicationContext(), "NO PAIRED DEVICES. " + pairedDevices.toArray(), Toast.LENGTH_SHORT).show();

                    } else {
                        if (pairedDevices.size() > 0) {
                            // Loop through paired devices
                            String str = "";
                            for (BluetoothDevice device : pairedDevices) {
                                BondedDeviceList.add(device);
                                str += "\n" + device.getName();
                            }
                            Toast.makeText(getApplicationContext(), "PAIRED DEVICES" + str, Toast.LENGTH_LONG).show();
                            // /////////BondedDeviceList.add("End");
                        }
                    }

                    Toast.makeText(getApplicationContext(), "START ....... " + pairedDevices.toArray()[0].toString(), Toast.LENGTH_SHORT).show();

                    mBluetoothAdapter.startDiscovery();

                    /*
                     * // Toast.makeText(getApplicationContext(),"MAKING YOUR DEVICE DISCOVERABLE",Toast.LENGTH_LONG); // Intent enableBtIntent = new // Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                     * 
                     * if (!bluetooth.isEnabled()) { Toast.makeText(getApplicationContext(), "TURNING BLUETOOTH ON", Toast.LENGTH_LONG).show(); // Intent enableBtIntent = new // Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); startActivityForResult(new
                     * Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0); }
                     * 
                     * Toast.makeText(getApplicationContext(), "VERIFYING .......", Toast.LENGTH_LONG).show(); startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), 0);
                     */
                } else
                    Toast.makeText(getApplicationContext(), "VALIDATING .......", Toast.LENGTH_SHORT).show();
            }
        });

        turnOffEC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnEnable(btnList, false);
                discoverableEC.setEnabled(false);
                Toast.makeText(getApplicationContext(), "TURNING BLUETOOTH OFF", Toast.LENGTH_LONG).show();
                mBluetoothAdapter.disable();
                turnOnEC.setEnabled(true);
            }
        });

        quitEC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnEnable(btnList, false);
                discoverableEC.setEnabled(false);
                Toast.makeText(getApplicationContext(), "QUITING "+btOn, Toast.LENGTH_SHORT).show();
                goodbye(btOn, mBluetoothAdapter);
            }
        });

    }

    private void goodbye(Boolean on, BluetoothAdapter mBluetoothAdapter) {
        if (!on)
            mBluetoothAdapter.disable();
        else
            mBluetoothAdapter.enable();
        sleep(1000);
        finish();                
    }
    
    // Unregister the receivers
    public void unregisterReceiver(BluetoothAdapter mBluetoothAdapter, Activity activity) {
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // Unregister broadcast listeners
        activity.unregisterReceiver(mReceiver);
    }

    private void btnEnable(Button[] bl, boolean b) {
        for (int i = 0; i < bl.length; i++)
            bl[i].setEnabled(b);
        sleep(100);
    }

    private void discover(Activity activity, BluetoothAdapter adapt) {
        adapt.enable();

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 15);
        activity.startActivity(discoverableIntent);
    }
    
    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
