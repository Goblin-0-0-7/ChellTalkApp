package de.goblin.chelltalkapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class DashboardActivity extends AppCompatActivity {

    private static final  String TAG = "DashboardActivity";

    private SharedPreferences.Editor prefEditor;

    private BluetoothManager BTManager;
    private BluetoothAdapter BTAdapter;
    private BluetoothDevice BTDevice;
    private BluetoothSocket BTSocket;
    private Set<BluetoothDevice> pairedDevices;
    AutoCompleteTextView acTextView_pairedDevices;

    private Button btn_scan;
    private Button btn_connectHande;

    private ArrayList deviceList = new ArrayList();
    private ArrayAdapter<String> adapter_deviceList;
    private String selectedDevice = null;
    private static Boolean connected2Device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        acTextView_pairedDevices = (AutoCompleteTextView) findViewById(R.id.autocompletetextview_pairedDevices);

        adapter_deviceList = new ArrayAdapter(this, R.layout.drop_down_item, deviceList);

        btn_scan = (Button) findViewById(R.id.button_scan);
        btn_connectHande = (Button) findViewById(R.id.button_connectHandle);

        // get values from SharedPreferences TODO: use https://developer.android.com/training/run-background-service/create-service.html instead of Pref so values are deleted on closing the app
        SharedPreferences pref = this.getSharedPreferences("Connection Values", Context.MODE_PRIVATE);
        prefEditor = pref.edit();
        connected2Device = pref.getBoolean("connected2Device", false);

        // adjust screen depending on saved values

        if (connected2Device){
            btn_connectHande.setText("Disconnect");
        }
        else {
            btn_connectHande.setText("Connect");
        }


        BTManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        BTAdapter = BTManager.getAdapter();
        checkBluetoothPermisson();
        updateDeviceList();
    }

    public void checkBluetoothPermisson() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) +
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) +
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_SCAN}, 101);
            return;
        }
        return;
    }

    public void scanDevices(View view) {
        updateDeviceList();
    }

    @SuppressLint("MissingPermission")
    public void updateDeviceList() {
        checkBluetoothPermisson();
        pairedDevices = BTAdapter.getBondedDevices();

        if (pairedDevices.size() < 1) {
            Toast.makeText(getApplicationContext(), "No paired devices found", Toast.LENGTH_SHORT).show();
        }
        else {
            for (BluetoothDevice bt : pairedDevices) deviceList.add(bt.getName() + " " + bt.getAddress());
            acTextView_pairedDevices.setAdapter(adapter_deviceList);
            acTextView_pairedDevices.setSelection(0);
        }
    }

    public void handleConnectionDevice(View view) {
        if (connected2Device) { // disconnect device
            btn_connectHande.setText("Connect");
            connected2Device = false;
            prefEditor.putBoolean("connected2Device", false).commit();
            disconnectFromServer();
        }
        else { // connect device
            String info = acTextView_pairedDevices.getText().toString();
            String address = info.substring(info.length() - 17);
            btn_connectHande.setText("Disconnect");
            connected2Device = true;
            prefEditor.putBoolean("connected2Device", true).commit();

            connectToServer(address);
        }
    };

    public void connectToServer(String address){
        BTDevice = BTAdapter.getRemoteDevice(address);
        checkBluetoothPermisson();
        try {
            UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
            BTSocket = BTDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        BTAdapter.cancelDiscovery();
        try {
            BTSocket.connect();
        } catch (IOException connectException) {
            Log.v(TAG, "Connection exception!");
            try {
                BTSocket.close();
            } catch (IOException closeException) {
                Log.v(TAG, "Close Exception!");
            }
        }
    }

    public void disconnectFromServer() {
        try {
            BTSocket.close();
        } catch (IOException closeException) {
            Log.v(TAG, "Close Exception!");
        }
    }

}