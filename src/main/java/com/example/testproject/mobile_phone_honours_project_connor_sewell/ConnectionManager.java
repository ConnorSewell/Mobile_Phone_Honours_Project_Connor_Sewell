package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.content.Context;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 10/02/2017.
 *
 * Code for networking taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ Accessed: 10/02/2017 @ 01:29
 */
public class ConnectionManager extends AsyncTask<Void, Void, String>
{
    Context context;

    public ConnectionManager(Context context)
    {
        this.context = context;
    }

    //Returns "CannotConnect" when connection establish failed
    //Returns "StreamEnded"  when streaming fails/ends for whatever reason
    @Override
    protected String doInBackground(Void... params)
    {
        ServerSocket sv;
        Socket client;
        InputStream inputStream;

        //Need to update statements to catch exact errors
        try
        {
            sv = new ServerSocket(8888);
            client = sv.accept();
            inputStream = client.getInputStream();
        }
        catch(Exception e)
        {
            return "CannotConnect";
        }

        try
        {
            //Receive input streams, then close stream.

            sv.close();
        }
        catch(Exception e)
        {
            return "StreamInterrupted";
        }

        //Return if stream ended
        return "StreamEnded";
    }

    @Override
    protected void onPostExecute(String exitResult)
    {

    }
}
