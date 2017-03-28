package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.util.Log;
import android.widget.VideoView;

import com.github.mikephil.charting.charts.LineChart;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Connor on 09/03/2017.
 * Using: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ For all network related code. Accessed: 10/02/2017 @ 03:00
 */

public class GyroscopeStreamHandler implements Runnable
{
    Socket socket;
    String ip;
    MainActivity activity;
    LineChart gyroscopeLineChart;
    Graphing graphing;

    private String TAG = "GyroscopeStreamHandler:";

    public GyroscopeStreamHandler(String ip, MainActivity activity, LineChart gyroscopeLineChart)
    {
        this.ip = ip;
        this.activity = activity;
        this.gyroscopeLineChart = gyroscopeLineChart;
        graphing = new Graphing();
        socket = new Socket();
    }

    BufferedReader is;
    @Override
    public void run()
    {
        try
        {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 4444)), 10000);
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Log.i(TAG, " Connected to server...");
            String line = is.readLine();
            while(true)
            {
                line = is.readLine();
                gyroscopeLineChart = graphing.update3SeriesGraph(line, gyroscopeLineChart, "Gyroscope: ");
                activity.updateGyroscope(gyroscopeLineChart);
            }
        } catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
}

