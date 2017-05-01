package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.util.Log;
import android.widget.VideoView;

import com.github.mikephil.charting.charts.LineChart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

    public void closeSocket()
    {
        try {
            socket.close();
        }
        catch(IOException e)
        {
            Log.e(TAG, "Error occurred when closing socket");
        }
    }


    BufferedReader is;
    float valCounter = 7;
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

            List<Float> xVals = new ArrayList<Float>();
            List<Float> yVals = new ArrayList<Float>();
            List<Float> zVals = new ArrayList<Float>();
            List<Float> timeStamps = new ArrayList<Float>();

            String line = null;

            gyroscopeLineChart = graphing.updateYAxisLabels(gyroscopeLineChart, 3, -1.8f, 1.8f, true);

            while(true && !socket.isClosed())
            {
                line = is.readLine();
                String[] sensorVals = line.split(",");
                xVals.add(Float.parseFloat(sensorVals[0]));
                yVals.add(Float.parseFloat(sensorVals[1]));
                zVals.add(Float.parseFloat(sensorVals[2]));
                timeStamps.add(Float.parseFloat(sensorVals[3])/1000000000);
                counter++;


                if(counter == valCounter)
                {
                    gyroscopeLineChart = graphing.update3SeriesGraph(xVals, yVals, zVals, timeStamps, gyroscopeLineChart, 1);
                    activity.updateGyroscope(gyroscopeLineChart);
                    counter = 0;
                    xVals.clear();
                    yVals.clear();
                    zVals.clear();
                    timeStamps.clear();
                }

            }
        } catch (IOException e)
        {
            Log.e(TAG, e.toString());
            //activity.notifyConnectionError();
            //closeSocket();
        }
    }
}

