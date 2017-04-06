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
    float valCounter = 15;
    int counter = 0;
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

            float accumulatedX = 0, accumulatedY = 0, accumulatedZ = 0, accumulatedTimestamp = 0;
            float x = 0, y = 0, z = 0;
            long timestamp = 0;

            while(true)
            {
                line = is.readLine();
                String[] sensorVals = line.split(",");
                x = Float.parseFloat(sensorVals[0]);
                y = Float.parseFloat(sensorVals[1]);
                z = Float.parseFloat(sensorVals[2]);
                timestamp = Long.parseLong(sensorVals[3]);
                counter++;

                System.out.println("x : " + x);
                System.out.println("y : " + y);
                System.out.println("z : " + z);

                accumulatedX+=x;
                accumulatedY+=y;
                accumulatedZ+=z;
                accumulatedTimestamp+=timestamp;

                if(counter == valCounter)
                {
                    float averagedX = accumulatedX/15.f;
                    float averagedY = accumulatedY/15.f;
                    float averagedZ = accumulatedZ/15.f;
                    float averagedTimestamp = (accumulatedTimestamp/15.f)/1000000000;
                    gyroscopeLineChart = graphing.update3SeriesGraph(averagedX, averagedY, averagedZ, averagedTimestamp, gyroscopeLineChart, 1);
                    activity.updateGyroscope(gyroscopeLineChart);
                    accumulatedX = 0;
                    accumulatedY = 0;
                    accumulatedZ = 0;
                    accumulatedTimestamp = 0;
                    counter = 0;
                }

            }
        } catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
}

