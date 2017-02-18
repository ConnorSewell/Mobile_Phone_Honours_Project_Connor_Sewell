package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Connor on 18/02/2017.
 * Using: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ For all network related code. Accessed: 10/02/2017 @ 03:00
 */

public class DataSender {
    Socket socket;
    String ip;

    public DataSender(String ip)
    {
        this.ip = ip;
        socket = new Socket();
        sendData();
    }

    private void sendData()
    {
        try
        {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 7777)), 1000);
            Log.i("Success: ", "Connected to server!");
            OutputStream os = socket.getOutputStream();
            PrintWriter out = new PrintWriter(os);
            out.flush();
            out.close();
            socket.close();
        } catch (IOException e) {Log.e("Error: ", e.toString());}

        try {if (socket.isConnected()) {socket.close();}}
        catch(IOException e) {Log.e("Error: ", e.toString());}
    }
}

