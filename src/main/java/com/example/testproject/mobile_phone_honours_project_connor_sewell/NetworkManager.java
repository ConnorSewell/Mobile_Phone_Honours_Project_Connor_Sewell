package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connor on 18/02/2017.
 * Broadcast receiver for Wi-Fi P2P
 *
 * Majority of class code taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html
 * ^ Accessed 08/02/2017 @ 14:55
 */

public class NetworkManager extends BroadcastReceiver
{
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    final MainActivity mActivity;
    String infoLogTag = "INFO: ";
    String errorLogTag = "ERROR: ";
    WifiP2pConfig config = new WifiP2pConfig();
    WifiP2pDevice device = new WifiP2pDevice();
    Intent intent;
    boolean connected = true;
    boolean devicesConnected = false;
    Button connectBtn;

    Thread connectionThread;

    public NetworkManager(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity mActivity)
    {
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = mActivity;

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener()
        {

            @Override
            public void onSuccess()
            {
                Log.i("Discover: ", "Successful");
            }

            @Override
            public void onFailure(int reasonCode)
            {
                Log.e("Discover: ", "Failed. Reason Code = " + String.valueOf(reasonCode));
            }

        });
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        NetworkInfo networkInfo = null;

        try
        {
            networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        }
        catch(Exception e){Log.e("Network Manager: ", e.toString());}

        Log.i("NetworkManager: ", "Changed: " + action.toString());

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {

        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {

        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {
            if (networkInfo.isConnected() && !devicesConnected)
            {
                devicesConnected = true;
                mActivity.wifiState(1);
                mActivity.getWiFiGroupOwnerIP();
            }
            else
            {
                mActivity.wifiState(3);
                devicesConnected = false;
            }
            Log.e("Connection changed", "...");

            Log.e("Started loop", "dasdasd");
                //Taken parts of below from: http://stackoverflow.com/questions/15621247/wifi-direct-group-owner-address
                //^ Accessed: 12/02/2017 @ 22:40
                //NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                Log.i("Info: ", action);
            }
        }

}
