package com.nsl.clientwifip2p;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;


/** Client Side Code
 *
 * *
 */


public class MainActivity extends AppCompatActivity {


    private final String appName = "WiFiP2P";

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;


    NsdManager.RegistrationListener mRegistrationListener;
    IntentFilter mIntentFilter;


    int mLocalPort;


    /*

    WiFi P2P Method

    initialize()	Registers the application with the Wi-Fi framework. This must be called before calling any other Wi-Fi P2P method.
    connect()	Starts a peer-to-peer connection with a device with the specified configuration.
    cancelConnect()	Cancels any ongoing peer-to-peer group negotiation.
    requestConnectInfo()	Requests a device's connection information.
    createGroup()	Creates a peer-to-peer group with the current device as the group owner.
    removeGroup()	Removes the current peer-to-peer group.
    requestGroupInfo()	Requests peer-to-peer group information.
    discoverPeers()	Initiates peer discovery
    requestPeers()	Requests the current list of discovered peers.


     */



    /*

    WiFi P2P Listeners

    WifiP2pManager.ActionListener	connect(), cancelConnect(), createGroup(), removeGroup(), and discoverPeers()
    WifiP2pManager.ChannelListener	initialize()
    WifiP2pManager.ConnectionInfoListener	requestConnectInfo()
    WifiP2pManager.GroupInfoListener	requestGroupInfo()
    WifiP2pManager.PeerListListener	requestPeers()

     */



    public Context returnContext ()
    {
        return getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(appName, "onCreate ()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);









        //WiFi P2P Registration

        Log.d(appName, "Registering with WiFi");
        //get an instance of WiFiP2P Manager
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        //Initialize it, now we can listen to it in its onReceive function
        mChannel = mManager.initialize(this,getMainLooper(),null);
        Log.d (appName, "new broadcast receiver");
        mReceiver = new WiFiDirectBroadcastReceiver(mManager,mChannel,this, getApplicationContext());





        //To use WIFI P2P listen for broadcast intents
        //Listen only for WiFi broadcasts by attaching them to the instantiated intent


        //Intent Filters
        Log.d (appName, "Intent Filters part");
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);//change in WiFi state
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);//change in WiFi peers list
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);//change in WiFi P2P connectivity
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);//change in configuration of this device


        TextView textView = (TextView) findViewById(R.id.HelloWorldTextField);

        textView.setText("Client Side");




    }



    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        Log.d(appName, "On Resume Called");
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        Log.d(appName, "On Pause Called");
        super.onPause();

    }







}



//Activites: dictate UI and handle user interface
//Servies: handle BG processing
//Broadcast Receivers: handle communication between app & OS
//Content Providers: handle data & DB management

//Activity is always implemented as subclass of Activity
//Using extends


//Services run in the BG
//Started (startService())
//Bound (bindService())

