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

    int valCounter = 15;
    int counter = 0;
    @Override
    public void run()
    {
        try
        {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 7777)), 10000);
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

                accumulatedX+=x;
                accumulatedY+=y;
                accumulatedZ+=z;
                accumulatedTimestamp+=timestamp;

                if(counter == valCounter)
                {
                    float averagedX = accumulatedX/valCounter;
                    float averagedY = accumulatedY/valCounter;
                    float averagedZ = accumulatedZ/valCounter;
                    float averagedTimestamp = (accumulatedTimestamp/valCounter)/1000000000;
                    accelerometerLineChart = graphing.update3SeriesGraph(averagedX, averagedY, averagedZ, averagedTimestamp, accelerometerLineChart, 0);
                    activity.updateAccelerometer(accelerometerLineChart);
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
