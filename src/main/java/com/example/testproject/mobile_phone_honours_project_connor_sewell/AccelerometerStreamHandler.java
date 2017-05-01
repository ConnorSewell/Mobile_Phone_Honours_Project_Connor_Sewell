package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.VideoView;

import com.github.mikephil.charting.charts.LineChart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connor on 01/03/2017.
 * Class handles the receiving, and usage of accelerometer data
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
    int valCounter = 7;
    int counter = 0;
    @Override
    public void run()
    {
        try
        {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 7777)), 10000);

            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = null;

            List<Float> xVals = new ArrayList<Float>();
            List<Float> yVals = new ArrayList<Float>();
            List<Float> zVals = new ArrayList<Float>();
            List<Float> timeStamps = new ArrayList<Float>();

            accelerometerLineChart = graphing.updateYAxisLabels(accelerometerLineChart, 3, -20, 20, true);

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
                    accelerometerLineChart = graphing.update3SeriesGraph(xVals, yVals, zVals, timeStamps, accelerometerLineChart, 0);
                    activity.updateAccelerometer(accelerometerLineChart);
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
            activity.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    activity.notifyConnectionError();
                }
            });
        }
    }
}
