package de.goblin.chelltalkapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class DashboardActivity extends AppCompatActivity {

    private BluetoothManager BTManager;
    private BluetoothAdapter BTAdapter;
    private Set<BluetoothDevice> pairedDevices;
    Spinner sp_pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BTManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        BTAdapter = BTManager.getAdapter();
        checkBluetoothPermisson();
        sp_pairedDevices = (Spinner) findViewById(R.id.spinner_pairedDevices);
        deviceList();
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
        deviceList();
    }

    @SuppressLint("MissingPermission")
    public void deviceList() {
        ArrayList deviceList = new ArrayList();
        deviceList.add("Select device");
        checkBluetoothPermisson();
        pairedDevices = BTAdapter.getBondedDevices();

        if (pairedDevices.size() < 1) {
            Toast.makeText(getApplicationContext(), "No paired devices found", Toast.LENGTH_SHORT).show();
            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList);
            sp_pairedDevices.setAdapter(adapter);
            sp_pairedDevices.setSelection(0);
        }
        else {
            for (BluetoothDevice bt : pairedDevices) deviceList.add(bt.getName() + " " + bt.getAddress());
            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList);
            sp_pairedDevices.setAdapter(adapter);
            sp_pairedDevices.setSelection(0);
        }
    }
}