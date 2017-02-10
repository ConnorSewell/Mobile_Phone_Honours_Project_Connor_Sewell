package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.content.Context;
import android.os.AsyncTask;

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
        try
        {
            return "CannotConnect";
        }
        catch(Exception e){}

        //Return if stream ended
        return "StreamEnded";
    }

    @Override
    protected void onPostExecute(String exitResult)
    {

    }
}
