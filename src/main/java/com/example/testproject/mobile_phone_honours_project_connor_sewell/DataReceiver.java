package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.app.Activity;
import android.util.Log;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 20/02/2017.
 *
 * For threading related tasks: http://stackoverflow.com/questions/11140285/how-to-use-runonuithread
 * ^ Accessed 20/02/2017 @ 21:37
 *
 * Code for networking taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ Accessed: 10/02/2017 @ 01:29
 * */

public class DataReceiver implements Runnable
{

    Activity activity;

    public DataReceiver(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public void run()
    {
        activity.runOnUiThread(new Runnable()
        {
           @Override
           public void run()
           {
               ServerSocket sv;
               Socket client;
               InputStream inputStream;

               //try
               //{
               //    Log.i("Connection State: ", "Waiting for connection...");
               //    sv = new ServerSocket(8888);
               //    client = sv.accept();
               //    InputStream is = client.getInputStream();
               //    String result = is.toString();
               //    sv.close();
              // }
              // catch(Exception e) {Log.e("Error: ", e.toString());}
           }
        });
    }
}
