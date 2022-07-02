package com.example.mainproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.multidex.mainproject.R;
import com.example.mainproject.domain.Message;
import com.example.mainproject.rest.AppApiVolley;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new MyThread().run();
    }
    class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            new AppApiVolley(MainActivity.this).fillPerson();
            new AppApiVolley(MainActivity.this).fillOrganization();
            new AppApiVolley(MainActivity.this).fillChats();
            new AppApiVolley(MainActivity.this).fillMsg();

        }
    }

}