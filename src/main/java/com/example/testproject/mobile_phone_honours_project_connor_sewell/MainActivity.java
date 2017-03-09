package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.session.MediaController;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
    LineChart gyroscopeLineChart;

    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    private MediaPlayer mr;
    public ImageView iv;

    final MainActivity activity = this;
    private VideoView vd;
    WifiP2pConfig config = new WifiP2pConfig();

    int tester;
    PrintWriter optionWriter;
    Graphing graphing = new Graphing();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iv = (ImageView) findViewById(R.id.image_view);
        //setUpAccelerometerGraph();
        accelerometerLineChart = (LineChart) findViewById(R.id.accelerometer_lineGraph);
        gyroscopeLineChart = (LineChart) findViewById(R.id.gyroscope_lineGraph);

        accelerometerLineChart = graphing.setUpGraph(accelerometerLineChart);
        gyroscopeLineChart = graphing.setUpGraph(gyroscopeLineChart);

        //Button clickButton = (Button) findViewById(R.id.button);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new NetworkManager(mManager, mChannel, this);

        //discoverPeers();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.EXTRA_NETWORK_INFO);
    }

    //https://www.youtube.com/watch?v=EZ-sNN7UWFU
    //^Used for toolbar options. Accessed 09/03/2017 @ 18:00 Also used for menu.xml in menu folder in res folder
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.modeOption)
        {
            mManager.requestPeers(mChannel, peerListListener);
        }else if(item.getItemId() == R.id.connectOption)
        {
            connectDevices();
        }
        else if(item.getItemId() == R.id.startOption)
        {
            startStreams();
        }

        return true;
    }

    //Also taken from above reference (toolbar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void startStreams()
    {
        //started = true;
        Log.e("Inside Netowrk info, ", "...");
        mManager.requestConnectionInfo(mChannel,
                new WifiP2pManager.ConnectionInfoListener()
                {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info)
                    {
                        InetAddress groupOwnerAddress = info.groupOwnerAddress;
                        String hostIP = groupOwnerAddress.getHostAddress();

                        VideoStreamHandler ds = new VideoStreamHandler(hostIP, activity);
                        Thread videoSendReceiveThread = new Thread(ds, "Thread: Video");
                        videoSendReceiveThread.start();

                        AccelerometerStreamHandler ash = new AccelerometerStreamHandler(hostIP, activity);
                        Thread accelerometerSendReceiveThread = new Thread(ash, "Thread: Accelerometer");
                        accelerometerSendReceiveThread.start();

                        GyroscopeStreamHandler gsh = new GyroscopeStreamHandler(hostIP, activity);
                        Thread gyroscopeSendReceiveThread = new Thread(gsh, "Thread: Gyroscope");
                        gyroscopeSendReceiveThread.start();

                    }
                });
    }

    private void connectDevices()
    {
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener()
        {
            @Override
            public void onSuccess()
            {
                Log.i("INFO", "Connection made");
            }

            @Override
            public void onFailure(int reason) {
                Log.i("INFO", "Failed to connect");
            }
        });
    }

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    boolean deviceConnected = false;

       private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                for (int i = 0; i < peerList.getDeviceList().size(); i++) {
                    if (peers.get(i).deviceName.toString().equals("Android_9c2d")) ;
                    {
                        Log.i("Tag..." + ": Device: ", peers.get(i).deviceName.toString());
                        Log.i("Tag..." + ": Address: ", peers.get(i).deviceAddress.toString());

                        WifiP2pDevice device = peers.get(i);
                        config.deviceAddress = device.deviceAddress;
                        config.groupOwnerIntent = 0; //0~15...
                        config.wps.setup = WpsInfo.PBC;
                    }
                }

                if (peers.size() == 0) {
                    Log.i("Info...", "No devices found");
                    return;
                }

                }};


    public void updateAccelerometer(String line)
    {
        accelerometerLineChart = graphing.updateGraph(line, accelerometerLineChart, "Accelerometer: ");
    }

    public void updateGyroscope(String line)
    {
        gyroscopeLineChart = graphing.updateGraph(line, gyroscopeLineChart, "Gyroscope: ");
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




