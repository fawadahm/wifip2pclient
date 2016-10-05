package com.nsl.clientwifip2p;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.content.Intent;
import android.view.View;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 *
 * Client Side Code
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver{


    /**
     * CONSTANTS
     */

    private final String connectToDevice = "Device: NSL-android-1";
    private final boolean debug = false;
    private final boolean isServer = false;
    private TextView deviceNameTextView;
    private String thisDeviceName = "";
    public InetAddress groupOwner;


    private int deviceIndex;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    private WifiP2pManager.PeerListListener peerListener;
    private List peers;
    private final String className = "Broadcast Receiver";
    private WifiP2pConfig peerToConnectTo = new WifiP2pConfig();
    private Context baseContext;

    private boolean alreadyListening;
    TextView textView;

    //This is the class constructor, the arguments are the WiFi manager, the channel and the activity we want to respond to
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity activity, Context baseContext) {


        super();
        Log.d (className, "Constructor ()");
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        peers = new ArrayList();
        this.baseContext = baseContext;
        alreadyListening = false;

        textView = (TextView) activity.findViewById (R.id.WiFiDevicesField);
        deviceNameTextView = (TextView) activity.findViewById(R.id.HelloWorldTextField);




    }

    public WiFiDirectBroadcastReceiver ()
    {
        Log.d (className, "Dummy Constructor");
    }

    private void onCreate ()
    {
        Log.d (className, "onCreate()");
    }

    //Peer listener interface to implement onPeersAvailable
    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {


        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {


            if (debug)
                Log.d (className, "onPeersAvailable()");


            peers.clear();
            peers.addAll(peerList.getDeviceList());

            if (debug)
                Log.d(className, "Peers = " + peers.size() + "");


            String listOfPeers = "";
            for (int counter = 0; counter < peers.size(); counter++)
            {
                String peerPhrase = "" + peers.get(counter);
                String delimiter = "\n";
                String[] peerInfo = peerPhrase.split(delimiter);


                if (debug)
                    Log.d(className, "Peer = " + counter + " " + peerInfo[0]);
                listOfPeers += peerInfo[0] + "\n";


                if (peerInfo[0].equals(connectToDevice))
                {
                    deviceIndex = counter;
                    FileServerAsyncTask.deviceToConnectTo = (WifiP2pDevice) peers.get(counter);
                    //if (debug)
                    Log.d (className, "Connecting to " + connectToDevice);
                    connectToPeer((WifiP2pDevice) (peers.get(counter)), peerInfo[0]);
                }
                else
                if (debug)
                    Log.d(className, "String error :" + peerInfo[0]);
            }

            textView.setText("Counter = " + deviceIndex + "\n" + listOfPeers);

        }
    };



    private WifiP2pManager.ConnectionInfoListener p2pListener = new WifiP2pManager.ConnectionInfoListener ()
    {

        @Override
        public void onConnectionInfoAvailable (final WifiP2pInfo info)
        {
            // InetAddress from WifiP2pInfo struct.
            //InetAddress groupOwnerAddress = (InetAddress) info.groupOwnerAddress.getHostAddress();

            Log.d (className, "Connection Info Available listener");

            if (info.groupFormed)
            {
                Log.d (className, "Group owner address : " + info.groupOwnerAddress);
                FileServerAsyncTask.groupOwnerAddress = info.groupOwnerAddress;




            }
            else
            {
                Log.d (className, "Group not formed");
            }

            // After the group negotiation, we can determine the group owner.
            if (info.groupFormed && info.isGroupOwner) {
                Log.d (className, "Group owner");
                // Do whatever tasks are specific to the group owner.
                // One common case is creating a server thread and accepting
                // incoming connections.
            } else if (info.groupFormed)
            {
                Log.d (className, "Client");
                // The other device acts as the client. In this case,
                // you'll want to create a client thread that connects to the group
                // owner.
            }
        }
    };





    //Function to connect a specific peer
    private void connectToPeer(final WifiP2pDevice deviceToConnectTo, final String deviceName) {
        //obtain a peer from the WifiP2pDeviceList

        if (debug)
            Log.d(className, "connectToPeer()");

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceToConnectTo.deviceAddress;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                if (debug)
                    Log.d(className, "Connection Status : " + "Connected to " + deviceName);



                // if (alreadyListening == false) {
                Intent mIntent = new Intent(baseContext, FileServerAsyncTask.class);

                baseContext.startService(mIntent);
                alreadyListening = true;
                //}
            }

            @Override
            public void onFailure(int reason) {
                //if (debug)
                Log.d(className, "Connection Status : " + "Failed to connect to " + deviceName);

            }
        });

    }




    //Function to start looking for peers
    public void discoverPeers() {
        //remains active until stopped (by forming a connection or initiating a P2P group)
        if (debug)
            Log.d(className, "discoverPeers ()");
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {


                if (debug)
                    Log.d(className, "Peers discovered");
                if (mManager != null) {

                    if (debug)
                        Log.d(className, "requestPeers ()");
                    mManager.requestPeers(mChannel, peerListListener);
                }
            }

            @Override
            public void onFailure(int reasonCode) {
                if (debug)
                    Log.d("Discover Peers", "Failed because of reasonCode");
            }
        });

    }

    //onReceiver function for BroadCast Receiver
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (debug)
            Log.d (className, "onReceive");
        //For change in the wifi state, whether its supported or not
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            //Get current state of WiFi
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                //  if (debug)
                Log.d(className, "Wifi State : " + "Enabled");

                discoverPeers();

            } else {
                //if (debug)
                Log.d(className, "Wifi State : " + "Disabled");
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            //if (debug)
            // Log.d(className, "New Peers = " + intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST));

            // discoverPeers();

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()



        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (debug)
                Log.d(className, "P2P Connection : " + "Changed");

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);


            if (networkInfo.isConnected()) {

                Log.d(className, "Network Connected");

                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, p2pListener);
            }




            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            if (debug)
                Log.d(className, "P2P Connection : " +  "This Device Changed");

            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            thisDeviceName = device.deviceName;
            deviceNameTextView.setText ("Client : " + thisDeviceName);
        }
        else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
            Log.d(className, "P2P Discovery Changed");
        }

    }





    //Setting up a Socket to communicate with Clients



}
