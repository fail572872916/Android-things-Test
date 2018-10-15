package com.example.fail5.ledtest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

public class MainActivity extends Activity {

        private static final String TAG = MainActivity.class.getSimpleName();
        //LED闪烁时间间隔，单位毫秒
        private int interval_between_blinks_ms = 1000;

        //控制LED灯闪烁频率控件
        private SeekBar mSeekbar;
        //当前LED等闪烁频率
        private TextView mSeekbarValue;
        private Handler mHandler = new Handler();
        //Gpio接口对象
        private Gpio mLedGpio;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.i(TAG, "Starting BlinkActivity");
            setContentView(R.layout.activity_main);
            mSeekbarValue = (TextView) findViewById(R.id.seekBar_value);
            mSeekbar = (SeekBar) findViewById(R.id.seekBar);
            mSeekbar.setProgress(interval_between_blinks_ms);
            mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    mSeekbarValue.setText("LED between time:" + i + "ms");
                    //通过SeekBar控件改变LED等闪烁频率
                    interval_between_blinks_ms = i;
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });

            // 使用指定引脚名称，PeripheralManagerService来打开一个连接到GPIO端口的LED连接
            PeripheralManager service = PeripheralManager.getInstance();
            try {
                String pinName = BoardDefaults.getGPIOForLED();
                mLedGpio = service.openGpio(pinName);
                //设置引脚为输出信号
                mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                Log.i(TAG, "Start blinking LED GPIO pin");

                //Post一个Runnable对象，在指定的时间间隔持续的改变GPIO接口的状态，使得LED等闪烁
                mHandler.post(mBlinkRunnable);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            //从Handler中移除blink Runnable对象
            mHandler.removeCallbacks(mBlinkRunnable);
            Log.i(TAG, "Closing LED GPIO pin");
            try {
                //页面销毁，当应用程序不在需要GPIO连接的时候，关闭Gpio资源
                mLedGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            } finally {
                mLedGpio = null;
            }
        }

        private Runnable mBlinkRunnable = new Runnable() {
            @Override
            public void run() {
                // 如果GPIO引脚已经关闭，则退出Runnable
                if (mLedGpio == null) {
                    return;
                }
                try {
                    //使用setValue()方法传递getValue()相反的值来改变LED的状态；
                    mLedGpio.setValue(!mLedGpio.getValue());
                    Log.d(TAG, "State set to " + mLedGpio.getValue());
                    mHandler.postDelayed(mBlinkRunnable, interval_between_blinks_ms);
                } catch (IOException e) {
                    Log.e(TAG, "Error on PeripheralIO API", e);
                }
            }
        };
    }