package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.in;


/**
 * Created by Connor on 18/02/2017.
 * Using: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ For all network related code (sockets). Accessed: 10/02/2017 @ 03:00
 * */

public class VideoStreamHandler implements Runnable
{
    Socket socket;
    String ip;
    MainActivity activity;

    private String TAG = "Connection Manager: ";

    public VideoStreamHandler(String ip, MainActivity activity)
    {
        this.ip = ip;
        this.activity = activity;
        socket = new Socket();
        Log.e("ANDREW: ","CUNT");
    }

    InputStream is;
    DataInputStream dis;
    ImageView iv;

    @Override
    public void run()
    {
        //http://stackoverflow.com/questions/2878867/how-to-send-an-array-of-bytes-over-a-tcp-connection-java-programming
        //Method of sending byte stream through socket taken from above
        //Accessed: 08/03/2017 @ 21:00
        try
        {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 8888)), 10000);
            is = socket.getInputStream();
            dis = new DataInputStream(is);
            while(true)
            {
                    int len = dis.readInt();
                    byte[] imageData = new byte[len];
                    if (len > 0)
                    {
                        dis.readFully(imageData);
                        setImage(imageData);
                    }
            }
        } catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    public void setImage(final byte[] imageBytes)
    {
        activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                activity.setImage(imageBytes);
            }
        });
    }
}
