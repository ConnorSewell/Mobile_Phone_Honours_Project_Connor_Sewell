package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.VideoView;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Connor on 01/03/2017.
 *
 * Using: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ For all network related code. Accessed: 10/02/2017 @ 03:00
 */

public class AccelerometerStreamHandler implements Runnable
{
    Socket socket;
    String ip;
    MainActivity activity;
    VideoView vd;

    private String TAG = "AccelStreamHandler: ";

    public AccelerometerStreamHandler(String ip, MainActivity activity)
    {
        this.ip = ip;
        this.activity = activity;
        socket = new Socket();
    }

    @Override
    public void run()
    {
        try
        {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 7777)), 10000);
            Log.i(TAG, "Connected to server!");

            //socket.close();
        } catch (Exception e)
        {
            Log.e("Error: ", e.toString());
        }
    }
}
