package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.util.Log;
import android.widget.VideoView;

import com.github.mikephil.charting.charts.LineChart;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Connor on 28/03/2017.
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
    private int valCounter = 10;

    public AudioLevelStreamHandler(String ip, MainActivity activity, LineChart audioLevelLineChart)
    {
        this.ip = ip;
        this.activity = activity;
        socket = new Socket();
        this.audioLevelLineChart = audioLevelLineChart;
        graphing = new Graphing();
    }

    public void setValsPerSec(int valsPerSec)
    {

    }

    BufferedReader is;
    @Override
    public void run()
    {
        try
        {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 1111)), 10000);
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = null;
            Log.i(TAG, "Connected to server...");

            int audioVal = 0; long timestamp = 0;
            float audioValAccumulator = 0; long timeStampAccumulator = 0;

            int counter = 0;
            int valsReceived = 0;

            audioLevelLineChart = graphing.updateYAxisLabels(audioLevelLineChart, 2, 0, 32767, true);

            while(true)
            {
                line = is.readLine();
                String[] vals = line.split(",");
                audioVal = Integer.parseInt(vals[0]);
                timestamp = Long.parseLong(vals[1]);
                if(audioVal > 0) {
                    audioValAccumulator += audioVal;
                    timeStampAccumulator += timestamp;
                    valsReceived++;
                }
                counter++;

                if(counter == valCounter)
                {
                    float averagedAudioVal = audioValAccumulator/valsReceived;
                    float averagedTimeStamp = (timeStampAccumulator/valsReceived)/1000000000;
                    audioLevelLineChart = graphing.updateSingleSeriesGraph(audioLevelLineChart, 0, averagedAudioVal, averagedTimeStamp);
                    activity.updateAudioLevel(audioLevelLineChart);
                    //System.out.println("Averaged was: " + averagedTimeStamp);
                    audioValAccumulator = 0;
                    timeStampAccumulator = 0;
                    counter = 0;
                    valsReceived = 0;
                }
            }
        } catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
}