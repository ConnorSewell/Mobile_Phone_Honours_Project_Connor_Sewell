package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectionManager cm = new ConnectionManager(this);
    }

}




