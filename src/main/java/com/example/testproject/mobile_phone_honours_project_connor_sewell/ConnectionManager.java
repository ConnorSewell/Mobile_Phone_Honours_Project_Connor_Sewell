package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * Created by Connor on 18/02/2017.
 * Using: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ For all network related code. Accessed: 10/02/2017 @ 03:00
 * */

public class ConnectionManager implements Runnable
{
    Socket socket;
    String ip;
    MainActivity activity;
    VideoView vd;

    private String TAG = "Connection Manager: ";

    public ConnectionManager(String ip, MainActivity activity)
    {
        this.ip = ip;
        this.activity = activity;
        socket = new Socket();
    }

    @Override
    public void run()
    {
        activity.updateVideo();
        try
        {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 8888)), 10000);
            Log.i(TAG, "Connected to server!");
            while(socket.isConnected())
            {

            }
            socket.close();
        } catch (Exception e)
        {
            Log.e("Error: ", e.toString());
        }
    }
}