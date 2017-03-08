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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.image_view);
        setUpAccelerometerGraph();

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

    public void setUpAccelerometerGraph()
    {
        accelerometerLineChart = (LineChart) findViewById(R.id.accelerometer_lineGraph);
        accelerometerLineChart.setScaleEnabled(true);
        accelerometerLineChart.setPinchZoom(true);
        accelerometerLineChart.setBackgroundColor(Color.GRAY);
        accelerometerLineChart.setDescription(null);

        LineData accelerometerData = new LineData();
        accelerometerData.setValueTextColor(Color.WHITE);

        accelerometerLineChart.setData(accelerometerData);

        Legend accelerometerLegend = accelerometerLineChart.getLegend();
        accelerometerLegend.setForm(Legend.LegendForm.LINE);
        accelerometerLegend.setTextColor(Color.WHITE);

        XAxis accelerometerAxisX = accelerometerLineChart.getXAxis();
        accelerometerAxisX.setTextColor(Color.WHITE);
        accelerometerAxisX.setAvoidFirstLastClipping(true);

        YAxis accelerometerAxisYLeft = accelerometerLineChart.getAxisLeft();
        accelerometerAxisYLeft.setTextColor(Color.WHITE);
        //accelerometerAxisYLeft.setAxisMaximum(3.f);

        YAxis accelerometerAxisYRight = accelerometerLineChart.getAxisRight();
        accelerometerAxisYRight.setEnabled(false);
    }


    float x;
    float y;
    float z;
    long time;
    int counter = 0;

    public void updateAccelerometer(String inputLine)
    {
        String[] values = inputLine.split(",");
        x = Float.parseFloat(values[0]);
        y = Float.parseFloat(values[1]);
        z = Float.parseFloat(values[2]);
        time = Long.parseLong(values[3]);
        LineData data = accelerometerLineChart.getData();

        if(data != null)
        {
            ILineDataSet set = data.getDataSetByIndex(0);
            ILineDataSet set2 = data.getDataSetByIndex(1);
            ILineDataSet set3 = data.getDataSetByIndex(2);

            if(set == null)
            {
                set = createSetX(Color.BLUE);
                data.addDataSet(set);
            }

            if(set2 == null)
            {
                set2 = createSetX(Color.RED);
                data.addDataSet(set2);
            }

            if(set3 == null)
            {
                set3 = createSetX(Color.GREEN);
                data.addDataSet(set3);
            }

            data.addEntry(new Entry(set.getEntryCount(), x), 0);
            data.addEntry(new Entry(set2.getEntryCount(), y), 1);
            data.addEntry(new Entry(set3.getEntryCount(), z), 2);

            data.notifyDataChanged();
            accelerometerLineChart.notifyDataSetChanged();
            accelerometerLineChart.setVisibleXRangeMaximum(50);
            accelerometerLineChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSetX(int colour)
    {
        LineDataSet set = new LineDataSet(null, "Accelerometer X");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(colour);
        set.setCircleColor(colour);
        set.setCircleRadius(0.1f);
        set.setLineWidth(0.1f);
        set.setDrawValues(false);
        return set;
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




