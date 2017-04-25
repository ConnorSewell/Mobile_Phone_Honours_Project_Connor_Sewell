package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.rtp.RtpStream;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.in;


/**
 * Created by Connor on 18/02/2017.
 * Using: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ For all network related code (sockets). Accessed: 10/02/2017 @ 03:00
 * */

public class VideoStreamHandler implements Runnable
{
    //DatagramSocket socket = null;
    RtpStream stream;
    Socket socket;
    String ip;
    MainActivity activity;

    private String TAG = "Connection Manager: ";

    public VideoStreamHandler(String ip, MainActivity activity)
    {
        this.ip = ip;
        this.activity = activity;
        try
        {
            socket = new Socket();
            socket.setTcpNoDelay(true);
            //socket = new DatagramSocket(8888);
            //socket.setReceiveBufferSize(20000);
        }
        catch(Exception e)
        {
        }
    }

    public void closeSocket()
    {
        try
        {
            socket.close();
        }
        catch(Exception e)
        {
            Log.e(TAG, "Error occurred when closing socket");
        }
    }


    InputStream is;
    DataInputStream dis;
    ImageView iv;

    byte[] imageIn = new byte[200000];

    Boolean inUse = false;

    @Override
    public void run() {
        //http://stackoverflow.com/questions/2878867/how-to-send-an-array-of-bytes-over-a-tcp-connection-java-programming
        //Method of sending byte stream through socket taken from above
        //Accessed: 08/03/2017 @ 21:00

        InetAddress ipAddress = null;

        try
        {
            //byte[] emptyMessage = new byte[1];
            //ipAddress = InetAddress.getByName(ip);
            //DatagramPacket sendPacket = new DatagramPacket(emptyMessage, emptyMessage.length, ipAddress, 8888);
            //socket.send(sendPacket);
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, 8888)), 10000);
            socket.setSoTimeout(5000);
            socket.setPerformancePreferences(0, 1, 0);
            socket.setTcpNoDelay(true);
            is = socket.getInputStream();
            dis = new DataInputStream(new BufferedInputStream(is));
        } catch (Exception e) {
            System.out.println("Could not convert...");
        }

        while(true && !socket.isClosed())
        {
            try
            {
                int len = dis.readInt();
                //System.out.println("Length: " + len);
                byte[] imageData = new byte[len];
                if (len > 0)
                {
                    dis.readFully(imageData);
                    if(!inUse) {
                        inUse = true;
                        setImage(imageData, imageData.length);
                    }
                }

                //DatagramPacket packet = new DatagramPacket(imageIn, imageIn.length);
                //socket.receive(packet);
                //byte[] values = packet.getData();
                //setImage(values, values.length);
                //System.out.println("Got in");
                //for(int i = 0; i < receive.length; i++)
                //{
                //    System.out.println(receive[i]);
                //}
            }
            catch(Exception e)
            {
                System.out.println("Failed here");
            }
        }

    }




       // try
      //  {
         //   socket.bind(null);
         //   socket.setTcpNoDelay(true);
         //   socket.connect((new InetSocketAddress(ip, 8888)), 10000);
         //   is = socket.getInputStream();
         //   is.read();
         //   dis = new DataInputStream(is);
          //  while(true)
          //  {
          //          int len = is.read();
          //          byte[] imageData = new byte[len];
          //          if (len > 0)
          //          {
          //              is.read(imageData, 0, len);
                        //dis.readFully(imageData);
          //              setImage(imageData);
          //          }
          //  }
        //} catch (IOException e)
       // {
       //     Log.e(TAG, e.toString());
            //activity.notifyConnectionError();
            //closeSocket();
        //}
    //}

    public void setImage(final byte[] imageBytes, final int length)
    {
        activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                activity.setImage(imageBytes, length);
                inUse = false;
            }
        });
    }
}
