/*
 * Copyright 2016 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel2106.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidhiddencamera.HiddenCameraFragment;

public class MainActivity extends AppCompatActivity {

    private HiddenCameraFragment mHiddenCameraFragment;
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//                             WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//                             WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
//                             WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
//                             WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("wake");

        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_using_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHiddenCameraFragment != null) {    //Remove fragment from container if present
                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(mHiddenCameraFragment)
                            .commit();
                    mHiddenCameraFragment = null;
                }

                startService(new Intent(MainActivity.this, DemoCamService.class));
            }
        });

        findViewById(R.id.btn_using_service2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, DemoCamService.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mHiddenCameraFragment != null) {    //Remove fragment from container if present
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(mHiddenCameraFragment)
                    .commit();
            mHiddenCameraFragment = null;
        }else { //Kill the activity
            super.onBackPressed();
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Waker", "wake");
            try {
                PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock fullWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                                                                               PowerManager.FULL_WAKE_LOCK |
                                                                               PowerManager.ACQUIRE_CAUSES_WAKEUP),
                                                                               "waker:");
                fullWakeLock.acquire(); // turn on
                fullWakeLock.release();
            } catch (Exception e) {
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
}
