package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Connor on 08/03/2017.
 * Using: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ For all network related code. Accessed: 10/02/2017 @ 03:00
 */

public class UserOptionsHandler implements Runnable
{

    Socket socket;
    String ip;
    String TAG = "UserOptionsHandler: ";
    MainActivity ma;

    int option; // 0 -> start, 1 -> stop, 2 -> ChangeMode?

    public UserOptionsHandler(int option, MainActivity ma)
    {
        this.option = option;
        this.ma = ma;
    }

    PrintWriter out;
    @Override
    public void run()
    {
        try
        {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 6666)), 10000);
            Log.i(TAG, "Connected to server...");
            out = new PrintWriter(socket.getOutputStream(), true);
            ma.setOptionWriter(out);
        } catch (Exception e)
        {
            Log.e("Error: ", e.toString());
        }
    }
    }



