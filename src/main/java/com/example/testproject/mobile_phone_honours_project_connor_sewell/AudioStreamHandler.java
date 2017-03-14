package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Connor on 14/03/2017.
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

        audioTrack = new  AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000, 2, AudioFormat.ENCODING_PCM_8BIT, 1280, AudioTrack.MODE_STREAM);
        audioTrack.setPlaybackRate(8000);

        if (audioTrack.STATE_INITIALIZED == 1)
        {
            audioTrack.play();
        }
        else if(audioTrack.STATE_INITIALIZED == 0)
        {
            Log.e(TAG, "NOT INITIALISED");
        }
    }

    DataInputStream dis;
    InputStream is;

    @Override
    public void run()
    {
        //http://stackoverflow.com/questions/2878867/how-to-send-an-array-of-bytes-over-a-tcp-connection-java-programming
        //Method of sending byte stream through socket taken from above
        //Accessed: 08/03/2017 @ 21:00
        try
        {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 3333)), 10000);
            is = socket.getInputStream();
            dis = new DataInputStream(is);
            while(true)
            {
                int len = dis.readInt();
                byte[] audioData = new byte[1280];
                if (len > 0)
                {
                    dis.readFully(audioData);
                    playAudio(audioData);
                    Log.e("Play? ", "...");
                }
            }
        } catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void playAudio(byte[] audioData)
    {
        audioTrack.write(audioData, 0, 1280);
    }
}
