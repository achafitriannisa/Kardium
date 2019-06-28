package com.example.asuspc.wecg;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import static com.example.asuspc.wecg.MainActivity.BT_NOT_FOUND;
import static com.example.asuspc.wecg.MainActivity.VIEW_MODE;

public class SettingsFragment extends Fragment {

    // GUI Components
    private TextView textViewTap;
    private Button mDiscoverBtn;
    public BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    public ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    private Switch mSwitchView;
    public Switch mSwitchBluetooth;
    private FragmentMessage mCallback;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final String BT_ENABLED = "1";
    private static final String DISCOVER = "7";

    //fragment view
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewTap = (TextView) view.findViewById(R.id.textViewTap);
        mDiscoverBtn = (Button)view.findViewById(R.id.scan);
        mSwitchBluetooth = (Switch)view.findViewById(R.id.switchBT);
        mSwitchView = (Switch)view.findViewById(R.id.switchView);

        mBTArrayAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = (ListView)view.findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        //set title for toolbar
        getActivity().setTitle(R.string.action_settings);

        //define bluetooth adapter
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
        if (mBTAdapter.isEnabled()){
            mSwitchBluetooth.setText(R.string.on);
            mSwitchBluetooth.setChecked(true);
            listPairedDevices(view);
        } else {
            mSwitchBluetooth.setText(R.string.off);
            mSwitchBluetooth.setChecked(false);
        }

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(view.getContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString(BT_NOT_FOUND,"Bluetooth not found");
        }

        mSwitchBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(mSwitchBluetooth.isChecked()){
                    mSwitchBluetooth.setText(R.string.on);
                    listPairedDevices(v);
                    bluetoothOn(v);
                } else {
                    mSwitchBluetooth.setText(R.string.off);
                    bluetoothOff(v);
                }
            }
        });

        mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Bundle discover = new Bundle();
                //discover.putString(DISCOVER, "start discovery");
                discover(v);
                listPairedDevices(v);
            }
        });

        mSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                if(mSwitchView.isChecked()){
                    mSwitchView.setText(R.string.lead12);
                    bundle.putString(VIEW_MODE,"on");
                    mCallback.sentMessage(bundle);
                } else {
                    mSwitchView.setText(R.string.leadII);
                    bundle.putString(VIEW_MODE,"off");
                    mCallback.sentMessage(bundle);
                }
            }
        });
    }


    private void discover(View view){
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(view.getContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(view.getContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                getActivity().registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(view.getContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void listPairedDevices(View view){
        textViewTap.setVisibility(View.VISIBLE);
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(view.getContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(view.getContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(view.getContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            } else {
                mSwitchBluetooth.setChecked(true);
            }

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            Bundle bundle = new Bundle();
            bundle.putString("address", address);
            bundle.putString("name", name);
            mCallback.sentMessage(bundle);

        }
    };

    //method to turn bluetooth on
    public void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mSwitchBluetooth.setChecked(true);
        }
    }

    public void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
        Toast.makeText(view.getContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data){
        Bundle bundle = new Bundle();
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                bundle.putString(BT_ENABLED,"Bluetooth enabled");
                mSwitchBluetooth.setChecked(true);
            } else{
                bundle.putString(BT_ENABLED,"Bluetooth disabled");
            }

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentMessage) {
            mCallback = (FragmentMessage) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentMessage");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
