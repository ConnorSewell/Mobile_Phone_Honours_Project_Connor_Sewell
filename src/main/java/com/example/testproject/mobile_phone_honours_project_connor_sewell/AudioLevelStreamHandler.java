package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.util.Log;
import android.widget.VideoView;

import com.github.mikephil.charting.charts.LineChart;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Connor on 28/03/2017.
 * Class handles the receiving, and usage of audio amplitude data
 *
 * Using: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ For all network related code. Accessed: 10/02/2017 @ 03:00
 */

public class AudioLevelStreamHandler implements Runnable
{
    Socket socket;
    String ip;
    MainActivity activity;
    VideoView vd;
    LineChart audioLevelLineChart;

    private String TAG = "ALStreamHandler: ";
    private Graphing graphing;
    private int valsPerSec = 5;
    private int valCounter = 7;


    public AudioLevelStreamHandler(String ip, MainActivity activity, LineChart audioLevelLineChart)
    {
        this.ip = ip;
        this.activity = activity;
        socket = new Socket();
        this.audioLevelLineChart = audioLevelLineChart;
        graphing = new Graphing();
    }

    public void closeSocket()
    {
        try
        {
            socket.close();
        }
        catch(IOException e)
        {
            Log.e(TAG, "Error occurred when closing socket");
        }
    }

    BufferedReader is;
    InputStream in;
    @Override
    public void run()
    {
        try
        {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 1111)), 10000);
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Log.i(TAG, "Connected to server...");

            String line = null;
            int counter = 0;
            int audioVal;
            long timestamp;

            if(audioLevelLineChart.getData().getEntryCount()!=0)
            {
                activity.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        audioLevelLineChart.clearValues();
                    }
                });
            }
            audioLevelLineChart = graphing.updateYAxisLabels(audioLevelLineChart, 2, 0, 32767, true);

            List<Integer> audioVals = new ArrayList<Integer>();
            List<Float> timestamps = new ArrayList<Float>();


            while(true && !socket.isClosed())
            {
                line = is.readLine();
                String[] vals = line.split(",");
                audioVal = Integer.parseInt(vals[0]);
                timestamp = Long.parseLong(vals[1]);

                if(audioVal > 0)
                {
                    audioVals.add(audioVal);
                    timestamps.add((float)timestamp/1000000000);
                }
                counter++;
                if(counter == valCounter)
                {
                    audioLevelLineChart = graphing.updateSingleSeriesGraph(audioLevelLineChart, 0, audioVals, timestamps);
                    activity.updateAudioLevel(audioLevelLineChart);
                    counter = 0;
                    audioVals.clear();
                    timestamps.clear();
                }
            }
        } catch (IOException e)
        {
            Log.e(TAG, e.toString());
        }
    }
}