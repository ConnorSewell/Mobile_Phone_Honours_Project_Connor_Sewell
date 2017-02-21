package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connor on 18/02/2017.
 * Majority of class code taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html
 * ^ Accessed 08/02/2017 @ 14:55
 */

public class NetworkManager extends BroadcastReceiver
{
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private MainActivity mActivity;
    String infoLogTag = "INFO: ";
    String errorLogTag = "ERROR: ";
    WifiP2pConfig config = new WifiP2pConfig();

    public NetworkManager(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity mActivity)
    {
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = mActivity;

        mManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(infoLogTag, "Successful");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.e(errorLogTag, "Failed. Reason Code = " + String.valueOf(reasonCode));
           }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {

        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {
            mManager.requestPeers(mChannel, peerListListener);
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected())
            {
                //Taken from: http://stackoverflow.com/questions/15621247/wifi-direct-group-owner-address
                //^ Accessed: 12/02/2017 @ 22:40
                mManager.requestConnectionInfo(mChannel,
                        new WifiP2pManager.ConnectionInfoListener()
                        {
                            @Override
                            public void onConnectionInfoAvailable(WifiP2pInfo info)
                            {
                                InetAddress groupOwnerAddress = info.groupOwnerAddress;
                                String hostIP = groupOwnerAddress.getHostAddress();
                                Log.i(infoLogTag, hostIP);

                                ConnectionManager ds = new ConnectionManager(hostIP);
                                Thread dataSendReceiveThread = new Thread(ds, "Thread One");
                                dataSendReceiveThread.start();
                            }
                        });
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
        {
            Log.i("Info: ", action);
        }
    }

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    boolean deviceConnected = false;

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if (!deviceConnected)
            {
                Log.i(infoLogTag, "No of peers: " + String.valueOf(peerList.getDeviceList().size()));

                peers.clear();
                peers.addAll(peerList.getDeviceList());

                for (int i = 0; i < peerList.getDeviceList().size(); i++) {
                    if (peers.get(i).deviceName.toString().equals("Android_9c2d")) ;
                    {
                        Log.i(infoLogTag + ": Device: ", peers.get(i).deviceName.toString());
                        Log.i(infoLogTag + ": Address: ", peers.get(i).deviceAddress.toString());

                        WifiP2pDevice device = peers.get(i);
                        config.deviceAddress = device.deviceAddress;
                        config.groupOwnerIntent = 0; //0~15...
                        config.wps.setup = WpsInfo.PBC;
                        deviceConnected = true;

                        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener()
                        {
                            @Override
                            public void onSuccess()
                            {
                                Log.i(infoLogTag, "Connection made");
                            }

                            @Override
                            public void onFailure(int reason)
                            {
                                Log.i(infoLogTag, "Failed to connect");
                            }
                        });

                    }
                }

                if (peers.size() == 0)
                {
                    Log.i(infoLogTag, "No devices found");
                    return;
                }
            }
        }

    };
}
