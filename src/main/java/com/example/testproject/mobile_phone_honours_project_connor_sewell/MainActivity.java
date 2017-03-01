package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.session.MediaController;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;


//https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
//^Used for network related code (WifiP2pManager, Channel, BroadcastReceiver...). Accessed 08/02/2017 @ 14:55
public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback
{
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    private MediaPlayer mr;

    private VideoView vd;

    int tester;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.video_view);
        //VideoView vd;
        //vd = (VideoView) findViewById(R.id.video_test);
        //mr = new MediaPlayer();
        //mHolder = surfaceView.getHolder();
        //mHolder.addCallback(this);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new NetworkManager(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.EXTRA_NETWORK_INFO);
    }

    public void setVideo(ParcelFileDescriptor pfd)
    {
        //mr.setDisplay(mHolder);

        mr = new MediaPlayer();

        if(pfd == null)
        {
            Log.e("Pdf: ", "Null!");
        }

            Log.e("First", "lel");
            try
            {
                mr.setDataSource(pfd.getFileDescriptor());
            }
            catch(Exception e)
            {
                Log.e("Error: ", e.toString());
            }

        try
        {
            mr.prepareAsync();
        }
        catch(Exception e)
        {
            Log.e("Error at prepare: ", e.toString());
        }
            Log.e("Second", "lel");
            //mr.prepare();
            Log.e("Third", "lel");
            //
            //mr.setDataSource(hey.getFileDescriptor());
            //mr.start();
    }

    public void surfaceCreated(SurfaceHolder holder)
    {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {

    }

    public void updateVideo()
    {
        tester = 1234;
    }
    public void updateAccelerometer(){}
    public void updateGPS(){}
    public void updateGyroscope(){}

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

}




