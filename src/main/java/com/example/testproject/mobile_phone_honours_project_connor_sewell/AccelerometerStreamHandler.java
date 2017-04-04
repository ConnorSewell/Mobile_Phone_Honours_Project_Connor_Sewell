package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.VideoView;

import com.github.mikephil.charting.charts.LineChart;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Connor on 01/03/2017.
 *
 * Using: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ For all network related code. Accessed: 10/02/2017 @ 03:00
 */

public class AccelerometerStreamHandler implements Runnable
{
    Socket socket;
    String ip;
    MainActivity activity;
    VideoView vd;
    LineChart accelerometerLineChart;

    private String TAG = "AccelStreamHandler: ";
    private Graphing graphing;

    public AccelerometerStreamHandler(String ip, MainActivity activity, LineChart accelerometerLineChart)
    {
        this.ip = ip;
        this.activity = activity;
        socket = new Socket();
        this.accelerometerLineChart = accelerometerLineChart;
        graphing = new Graphing();
    }

    BufferedReader is;
    @Override
    public void run()
    {
        try
        {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 7777)), 10000);
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = is.readLine();
            Log.i(TAG, "Connected to server...");

            while(true)
            {
                line = is.readLine();
                accelerometerLineChart = graphing.update3SeriesGraph(line, accelerometerLineChart, 0);
                activity.updateAccelerometer(accelerometerLineChart);
            }
        } catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
}
