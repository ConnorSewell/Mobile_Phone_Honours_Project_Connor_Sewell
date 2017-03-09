package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.duration;

/**https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 *^Used for network related code (WifiP2pManager, Channel, BroadcastReceiver...). Accessed 08/02/2017 @ 14:55
 *
 * https://www.youtube.com/watch?v=a20EchSQgpw Referenced 02/03/2017 @ 02:59
 * ^ AND https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/RealtimeLineChartActivity.java
 *       ^ Referenced 02/03/2017 @ 03:00 used for all graphing code
*/

public class MainActivity extends AppCompatActivity
{
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    LineChart accelerometerLineChart;

    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    private MediaPlayer mr;
    public ImageView iv;

    private VideoView vd;

    int tester;
    PrintWriter optionWriter;
    Graphing graphing = new Graphing();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.image_view);
        //setUpAccelerometerGraph();
        accelerometerLineChart = (LineChart) findViewById(R.id.accelerometer_lineGraph);
        accelerometerLineChart = graphing.setUpGraph(accelerometerLineChart);

        Button clickButton = (Button) findViewById(R.id.button);


        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new NetworkManager(mManager, mChannel, this, clickButton);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.EXTRA_NETWORK_INFO);
    }

    public void updateAccelerometer(String line)
    {
        accelerometerLineChart = graphing.updateGraph(line, accelerometerLineChart);
    }

    boolean imageDisplayInProgress = false;

    public void doToast()
    {
        Toast toast = Toast.makeText(this, "lol 100", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void setImage(byte[] imgBytes)
    {
        Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        iv.setImageBitmap(Bitmap.createScaledBitmap(bmp, iv.getWidth(), iv.getHeight(), false));
    }

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

    public void setOptionWriter(PrintWriter out)
    {
        this.optionWriter = out;
    }

}




