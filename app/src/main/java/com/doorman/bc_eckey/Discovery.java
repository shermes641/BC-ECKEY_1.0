package com.doorman.bc_eckey;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

public class Discovery  extends Activity {
    
    private Set<BluetoothDevice> pairedDevices;
    public static ArrayList<Object> BondedDeviceList;
    public static ArrayList<Object> NewDeviceList;
    
    Intent discoverableIntent;
    Activity activity;
    BluetoothAdapter mBluetoothAdapter;

     public void makeDiscoverable()
    {
         //discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);       
        //activity.startActivity(discoverableIntent);
         Toast.makeText(getApplicationContext(), "VALIDATING .......", Toast.LENGTH_SHORT).show();
         startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), 0);
         queryDevices();
    }

    //It will Add the paired device in the BondedDeviceList
    public void queryPairedDevice(){
        
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        Toast.makeText(getApplicationContext(), "QUERY ....... "+pairedDevices.toArray(), Toast.LENGTH_SHORT).show();
        // If there are paired devices
        if(pairedDevices==null)
        {
            //No Bonded Devices 

        }else
        {
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    BondedDeviceList.add(device);
                }
                BondedDeviceList.add("End");
            }
        }
    }

    //Broadcast Receiver will find the Available devices and the discovery finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        int cnt = 1;
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Toast.makeText(getApplicationContext(), "BROADCAST ....... " +action, Toast.LENGTH_SHORT).show();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action.trim())) {
                Toast.makeText(getApplicationContext(), "ACTION .......", Toast.LENGTH_SHORT).show();
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                System.out.println("Discovered !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! "+(cnt++));
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    NewDeviceList.add(device);
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(getApplicationContext(), "FINISHED .......", Toast.LENGTH_SHORT).show();
                if (NewDeviceList.isEmpty() == true) {
                    String noDevices = "No Devices";
                    NewDeviceList.add(noDevices);
                }
                System.out.println("Discovery Finished!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! "+NewDeviceList.size());
                NewDeviceList.add("End");
            }
        }
    };

    IntentFilter actionFoundFilter, discoveryFinishedFilter;
    //This is query for the bluetooth devices 
    public void queryDevices(){
        actionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mReceiver, actionFoundFilter);
        // Don't forget to unregister during onDestroy
        Toast.makeText(getApplicationContext(), "FILTER .......", Toast.LENGTH_SHORT).show();
        discoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(mReceiver, discoveryFinishedFilter); 
        // Don't forget to unregister during onDestroy
        queryPairedDevice();
        Toast.makeText(getApplicationContext(), "START ....... "+pairedDevices.toArray(), Toast.LENGTH_SHORT).show();
        mBluetoothAdapter.startDiscovery();
    }


    //Unregister the receivers
    public void unregisterReceiver() {
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
                mBluetoothAdapter.cancelDiscovery();
        }
        // Unregister broadcast listeners
        activity.unregisterReceiver(mReceiver);
    }

}
