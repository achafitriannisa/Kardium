package com.example.asuspc.wecg;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements FragmentMessage {

    //define for identifying shared types between calling functions
    //public final static String BT_ENABLED = "1";
    public final static String VIEW_MODE = "2";
    public final static String BLUETOOTH_DEVICE = "3";
    public final static String BT_NOT_FOUND = "4";
    public final static String LOGOUT = "5";
    public final static String FILE_UPLOADED = "6";
    public final static String DISCOVER = "7";

    //define fragments
    RecordFragment mRecordFragment = new RecordFragment();
    HistoryFragment mHistoryFragment = new HistoryFragment();
    SettingsFragment mSettingsFragment = new SettingsFragment();
    AccountFragment mAccountFragment = new AccountFragment();
    ShareFragment mShareFragment = new ShareFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //set initial fragment
        displaySelectedScreen(R.id.nav_record);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Handle navigation view item clicks here.
                displaySelectedScreen(menuItem.getItemId());
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_record:
                fragment = mRecordFragment;
                break;
            case R.id.nav_history:
                fragment = mHistoryFragment;
                break;
            case R.id.nav_settings:
                fragment = mSettingsFragment;
                break;
            case R.id.nav_account:
                fragment = mAccountFragment;
                break;
            case R.id.nav_share:
                fragment = mShareFragment;
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void sentMessage(Bundle bundle) {
        if (bundle.getString("address") != null && bundle.getString("name") != null) {
            mRecordFragment.address = bundle.getString("address");
            mRecordFragment.name = bundle.getString("name");
            //change back to record fragment
            displaySelectedScreen(R.id.nav_record);
            mRecordFragment.connectingDevices();
            bundle.clear();
        } else if (bundle.getString(BLUETOOTH_DEVICE) != null) {
            //change to settings fragment
            displaySelectedScreen(R.id.nav_settings);
            bundle.clear();
        } else if (bundle.getString(VIEW_MODE) != null) {
            if(bundle.getString(VIEW_MODE) == "on"){
                mRecordFragment.viewMode = true;
            } else {
                mRecordFragment.viewMode = false;
            }
            bundle.clear();
        } /*else if (bundle.getString(BT_NOT_FOUND) != null){
            mRecordFragment.mBluetoothStatus.setText(bundle.getShort(BT_NOT_FOUND));
            bundle.clear();
        } else if (bundle.getString(BT_ENABLED) != null) {
            mRecordFragment.mBluetoothStatus.setText(bundle.getString(BT_ENABLED));
            bundle.clear();
        }*/ else if (bundle.getString(LOGOUT) != null){
            startActivity(new Intent(this, FirstScreenActivity.class));
            bundle.clear();
            finish();
        } else if (bundle.getString(DISCOVER) != null){
            discover();
        }
    }

    private void discover(){
        // Check if the device is already discovering
        if(mSettingsFragment.mBTAdapter.isDiscovering()){
            mSettingsFragment.mBTAdapter.cancelDiscovery();
            Toast.makeText(this,"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mSettingsFragment.mBTAdapter.isEnabled()) {
                mSettingsFragment.mBTArrayAdapter.clear(); // clear items
                mSettingsFragment.mBTAdapter.startDiscovery();
                Toast.makeText(this, "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(this, "Bluetooth not on", Toast.LENGTH_SHORT).show();
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
                mSettingsFragment.mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mSettingsFragment.mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

}
