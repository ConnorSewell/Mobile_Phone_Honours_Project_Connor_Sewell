package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Connor on 14/03/2017.
 * This class is NOT used, but is left
 * in case of future use. It allows the gathering, and playing of incoming audio streams.
 *
 *  Using: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ For all network related code. Accessed: 10/02/2017 @ 03:00
 */

public class AudioStreamHandler implements Runnable
{
    Socket socket;
    String ip;
    MainActivity ma;

    private String TAG = "AudioStreamHandler: ";

    //http://stackoverflow.com/questions/29695269/android-audiorecord-audiotrack-playing-recording-from-buffer
    //^ Used for audio track related code. Accessed: 14/03/2017 @ 20:43
    private AudioTrack audioTrack;

    public AudioStreamHandler(String ip, MainActivity activity)
    {
        this.ip = ip;
        this.ma = activity;
        socket = new Socket();
    }

    DataInputStream dis;
    InputStream is;

    @Override
    public void run()
    {
        //http://stackoverflow.com/questions/2878867/how-to-send-an-array-of-bytes-over-a-tcp-connection-java-programming
        //Method of reading byte array
        //Accessed: 08/03/2017 @ 21:00
        try
        {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 3333)), 10000);
            is = socket.getInputStream();
            dis = new DataInputStream(is);

            audioTrack = new  AudioTrack(AudioManager.STREAM_MUSIC, 44100, 2, AudioFormat.ENCODING_PCM_16BIT, 7680, AudioTrack.MODE_STREAM);
            audioTrack.setPlaybackRate(44100);


            if (audioTrack.STATE_INITIALIZED == 1)
            {
                audioTrack.play();
            }
            else if(audioTrack.STATE_INITIALIZED == 0)
            {
                Log.e(TAG, "NOT INITIALISED");
            }

            while(true)
            {
                int len = dis.readInt();
                byte[] audioData = new byte[7104];
                if (len > 0)
                {
                    dis.readFully(audioData);
                    audioTrack.write(audioData, 0, 7104);
                }
            }
        } catch (IOException e)
        {
            Log.e(TAG, e.toString());
        }
    }

}
