package com.example.gpio2steepr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity implements View.OnClickListener {

    PeripheralManager peripheralManager;
    private Gpio gpio2;
    private Gpio gpio1;

    Button btAdd, btReduce, btDirection, btWhirling;
    SeekBar seekBar;
    private TextView tv_pwm;
    int mProgress = 0;

    boolean isWorking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        peripheralManager = PeripheralManager.getInstance();
        try {
            gpio1 = peripheralManager.openGpio(Constants.GPIO1);
            gpio2 = peripheralManager.openGpio(Constants.GPIO2);
            gpio1.setActiveType(Gpio.ACTIVE_LOW);
            gpio1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            // 高电平有效
            gpio2.setActiveType(Gpio.ACTIVE_HIGH);
            // 将引脚初始化为高电平输出
            gpio2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initView();
    }

    private void initView() {

        btAdd = findViewById(R.id.bt_add);
        btReduce = findViewById(R.id.bt_reduce);
        btDirection = findViewById(R.id.bt_direction);
        btWhirling = findViewById(R.id.bt_whirling);
        seekBar = (SeekBar) findViewById(R.id.seekBar_pwm);
        tv_pwm = (TextView) findViewById(R.id.tv_pwm);
        btAdd.setOnClickListener(this);
        btReduce.setOnClickListener(this);
        btDirection.setOnClickListener(this);
        btWhirling.setOnClickListener(this);

        mProgress = seekBar.getProgress();
        Log.d("MainActivity", "mProgress:" + mProgress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgress = progress;
                seekBar.setProgress(progress);
                tv_pwm.setText(tv_pwm.getText() + ":" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv_pwm.setText(tv_pwm.getText() + ":" + seekBar.getProgress());

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add:
                seekBar.setProgress(mProgress + 1);
                break;
            case R.id.bt_reduce:
                seekBar.setProgress(mProgress - 1);
                break;
            case R.id.bt_direction:

                //没运行的时候再设置
                if (!isWorking) {
                    try {
                        //得到它的状态然后取反
                        gpio2.setValue(!gpio2.getValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.bt_whirling:
                //没运行的时候再设置
                if (!isWorking) {
                    Rotate();
                }
                break;
            default:
                break;

        }

    }

    public void Rotate() {
//倒计时脉冲
        for (int i = 1; i <= mProgress; i++) {
            try {
                isWorking = true;
                // sendData(B_Rotation);
                gpio1.setValue(false);
                Thread.sleep(5);
                gpio1.setValue(true);
                Thread.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (i == mProgress) {
                isWorking = false;
            }
        }
    }
}
