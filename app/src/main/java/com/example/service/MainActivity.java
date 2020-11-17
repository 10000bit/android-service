package com.example.service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import com.example.service.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;
    HandlerThread handlerThread;
    Handler handler;
    int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);

        mainBinding.buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handlerThread != null && handlerThread.isAlive())
                    return;
                // thread start
                handlerThread = new HandlerThread("My Handler Thread");
                handlerThread.start();
                handler = new Handler(handlerThread.getLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if (msg.what == 0) {
                            // ui update
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pb.setProgress(value);
                                    value+=10;
                                }
                            });
                            Message m = Message.obtain();
                            m.what = 0;
                            sendMessageDelayed(m, 2000); // 2초마다 처리
                        } else if (msg.what == -1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    value = 0;
                                    pb.setProgress(value);
                                }
                            });
                            getLooper().quit();

                        }
                    }
                };

                Message m = Message.obtain();
                m.what = 0;
                handler.sendMessage(m);
            }
        });

        mainBinding.buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handlerThread != null && handlerThread.isAlive()) {
                    Message m = Message.obtain();
                    m.what = -1;
                    handler.sendMessage(m);
                }
            }
        });
    }
}
