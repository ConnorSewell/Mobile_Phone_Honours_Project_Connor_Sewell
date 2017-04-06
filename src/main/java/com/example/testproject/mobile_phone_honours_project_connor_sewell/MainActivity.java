package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.session.MediaController;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.io.File;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.duration;
import static android.R.id.input;

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

    List<WifiP2pConfig> configs = new ArrayList<WifiP2pConfig>();
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    LineChart accelerometerLineChart;
    LineChart gyroscopeLineChart;
    LineChart audioDataLineChart;

    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    private MediaPlayer mr;
    public ImageView iv;

    final MainActivity activity = this;
    private VideoView vd;
    WifiP2pConfig config = new WifiP2pConfig();

    PrintWriter optionWriter;
    Graphing graphing = new Graphing();

    private int wifiState = 0;
    private String wifiIPAddress;
    private String wifiDirecthostIP;
    private boolean streamStarted = false;

    SubMenu sm;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iv = (ImageView) findViewById(R.id.image_view);
        iv.setBackgroundColor(Color.GRAY);

        accelerometerLineChart = (LineChart) findViewById(R.id.accelerometer_lineGraph);
        gyroscopeLineChart = (LineChart) findViewById(R.id.gyroscope_lineGraph);
        audioDataLineChart = (LineChart) findViewById(R.id.audioData_lineGraph);

        accelerometerLineChart = graphing.setUpGraph(accelerometerLineChart, 0);
        gyroscopeLineChart = graphing.setUpGraph(gyroscopeLineChart, 0);
        audioDataLineChart = graphing.setUpGraph(audioDataLineChart, 1);

        accelerometerLineChart.setBackgroundColor(Color.BLACK);
        gyroscopeLineChart.setBackgroundColor(Color.BLACK);
        audioDataLineChart.setBackgroundColor(Color.BLACK);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new NetworkManager(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.EXTRA_NETWORK_INFO);

        File mediaStorageDir;
        Time currTime = new Time(Time.getCurrentTimezone());
        currTime.setToNow();
        mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/lol");

        //mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Sample");
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                System.out.println("Failed to create directory...");
            }
        }

    }

    public void wifiState(int state)
    {
        wifiState = state;

        if(state == 1)
        {
            Toast.makeText(this, "Valid WiFi Direct Connection", Toast.LENGTH_SHORT).show();
        }

    }

    //https://www.youtube.com/watch?v=EZ-sNN7UWFU
    //^Used for toolbar options. Accessed 09/03/2017 @ 18:00 Also used for menu.xml in menu folder in res folder
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
      if(item.getItemId() == R.id.wifiIPEntry)
        {
            ipDialogue();
        }
        else if(item.getItemId() == R.id.startOption)
        {
            if(streamStarted)
            {
                stopStreams();
            }
            else {
                if (wifiState == 1) {
                    startStreams(wifiDirecthostIP, 0);
                    menu.getItem(1).setTitle("Stop");
                } else if (wifiState == 2) {
                    startStreams(wifiIPAddress, 1);
                } else if (wifiState == 3) {
                    Toast.makeText(this, "Please connect before continuing... If error persists, please restart connection",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if(item.getGroupId() == 2)
      {
          if (item.getItemId() == 0)
          {
              requestPeers();
          } else
          {
              mManager.connect(mChannel, configs.get(item.getItemId() - 1), new WifiP2pManager.ActionListener() {
                  @Override
                  public void onSuccess()
                  {
                      Log.i("INFO", "Connection made");
                      Toast.makeText(activity, "WiFi direct connection established", Toast.LENGTH_LONG).show();
                  }

                  @Override
                  public void onFailure(int reason) {
                      Log.i("INFO", "Failed to connect");
                  }
              });

          }
      }

        return true;
    }

    //http://stackoverflow.com/questions/10903754/input-text-dialog-android
    //^ Used for below method (dialogue box). Accessed: 06/04/2017 @ 18:11
    private void ipDialogue()
    {

        AlertDialog.Builder alertDialogueBuilder = new AlertDialog.Builder(this);
        alertDialogueBuilder.setTitle("Enter IP address of device on WiFi network");
        final EditText inputArea = new EditText(this);
        inputArea.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialogueBuilder.setView(inputArea);

        alertDialogueBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                wifiIPAddress = inputArea.getText().toString();
                wifiState = 2;
            }
        });
        alertDialogueBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialogueBuilder.show();
    }


    boolean setup = false;
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        this.menu = menu;
        //menu.getItem(1).setEnabled(false);
        sm = menu.getItem(2).getSubMenu();
        sm.add(2, 0, 0, "Important Info");
        sm.add(2, 1, 0, "Rediscover");
        setup = true;
        requestPeers();
        return true;
    }

    public void changeButtonStates()
    {
        menu.getItem(1).setEnabled(!menu.getItem(1).isEnabled());
        menu.getItem(2).setEnabled(!menu.getItem(2).isEnabled());

        if(menu.getItem(2).isEnabled())
        {
            for(int i = 1; i < sm.size(); i++)
            {
                sm.removeItem(i);
            }
            requestPeers();
        }
    }

    private void requestPeers()
    {
        mManager.requestPeers(mChannel, peerListListener);
    }

    VideoStreamHandler ds;
    AccelerometerStreamHandler ash;
    GyroscopeStreamHandler gsh;
    AudioLevelStreamHandler alsh;

    Thread videoSendReceiveThread;
    Thread accelerometerSendReceiveThread;
    Thread gyroscopeSendReceiveThread;
    Thread audioLevelSendReceiveThread

    private void startStreams(String IP, int connectionType)
    {
        ds = new VideoStreamHandler(IP, activity);
        videoSendReceiveThread = new Thread(ds, "Thread: Video");
        videoSendReceiveThread.start();

        ash = new AccelerometerStreamHandler(IP, activity, accelerometerLineChart);
        accelerometerSendReceiveThread = new Thread(ash, "Thread: Accelerometer");
        accelerometerSendReceiveThread.start();

        gsh = new GyroscopeStreamHandler(IP, activity, gyroscopeLineChart);
        gyroscopeSendReceiveThread = new Thread(gsh, "Thread: Gyroscope");
        gyroscopeSendReceiveThread.start();

        alsh = new AudioLevelStreamHandler(IP, activity, audioDataLineChart);
        audioLevelSendReceiveThread = new Thread(alsh, "Thread: Audio Level");
        audioLevelSendReceiveThread.start();

        streamStarted = true;
        //AudioStreamHandler audioSH = new AudioStreamHandler(hostIP, activity);
        //Thread audioReceiveThread = new Thread(audioSH, "Thread: Audio");
        //audioReceiveThread.start();

    }

    private void stopStreams()
    {
        streamStarted = false;

        videoSendReceiveThread.interrupt();
        videoSendReceiveThread = null;

        accelerometerSendReceiveThread.interrupt();
        accelerometerSendReceiveThread = null;

        gyroscopeSendReceiveThread.interrupt();
        gyroscopeSendReceiveThread = null;

        audioLevelSendReceiveThread.interrupt();
        audioLevelSendReceiveThread = null;

        ds.closeSocket();
        ash.closeSocket();
        alsh.closeSocket();
        gsh.closeSocket();
    }

    private void getWiFiGroupOwnerIP()
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
                        wifiDirecthostIP = groupOwnerAddress.getHostAddress();
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

    boolean deviceConnected = false;
    int i;
    int deviceCount;

       private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                configs.clear();
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                for (i = 0; i < peerList.getDeviceList().size(); i++)
                {
                        WifiP2pConfig config = new WifiP2pConfig();
                        WifiP2pDevice device = peers.get(i);
                        config.deviceAddress = device.deviceAddress;
                        config.groupOwnerIntent = 0; //0~15...
                        config.wps.setup = WpsInfo.PBC;
                        configs.add(config);

                        System.out.println("Size: " + configs.size());
                        runOnUiThread(
                                new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                sm.add(2, sm.size(), 0, peers.get(sm.size() - 1).deviceName);

                            }
                        });

                        deviceCount++;

                }
                if (peers.size() == 0)
                {
                    Log.i("Info...", "No devices found");
                    return;
                }
               }};


    public void updateAccelerometer(LineChart accelerometerLineChart)
    {
        this.accelerometerLineChart = accelerometerLineChart;
    }

    public void updateGyroscope(LineChart gyroscopeLineChart)
    {
        this.gyroscopeLineChart = gyroscopeLineChart;
    }

    public void updateAudioLevel(LineChart audioDataLineChart)
    {
        this.audioDataLineChart = audioDataLineChart;
    }

    public void setImage(byte[] imgBytes)
    {
        Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        iv.setImageBitmap(Bitmap.createScaledBitmap(bmp, iv.getWidth(), iv.getHeight(), false));
    }

    public void updateGPS(){}

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





