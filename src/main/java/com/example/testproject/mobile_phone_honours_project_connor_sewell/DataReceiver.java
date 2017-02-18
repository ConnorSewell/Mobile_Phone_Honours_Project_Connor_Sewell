package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.content.Context;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 10/02/2017.
 *
 * Code for networking taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ Accessed: 10/02/2017 @ 01:29
 */
public class DataReceiver extends AsyncTask<Void, Void, String>
{
    Context context;

    public DataReceiver(Context context)
    {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params)
    {
        ServerSocket sv;
        Socket client;
        InputStream inputStream;

        try
        {
            Log.i("Connection State: ", "Waiting for connection...");
            sv = new ServerSocket(8888);
            client = sv.accept();
            InputStream is = client.getInputStream();
            sv.close();
        }
        catch(Exception e) {return e.toString();}

        return "Transaction Complete";
    }

    @Override
    protected void onPostExecute(String exitResult)
    {
        System.out.println("Connection result: " + exitResult);
        Toast.makeText(context,"Result: " + exitResult, Toast.LENGTH_LONG).show();
    }
}
