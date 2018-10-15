package com.example.ledbutton;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
    /**
     * 连接bc6引脚和ged即可
     */
    private static final String DEVICE_RPI3 = "BCM6";
    private Gpio mGpio;
    private Button bt_open, bt_close;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_open = findViewById(R.id.bt_open);
        bt_close = findViewById(R.id.bt_close);
        PeripheralManager peripheralManager = PeripheralManager.getInstance();
        try {
            mGpio = peripheralManager.openGpio(DEVICE_RPI3);
            //设置引脚为输出信号
            mGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bt_open.setOnClickListener(this);
        bt_close.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //从Handler中移除blink Runnable对象
        Log.i(TAG, "Closing LED GPIO pin");
        try {
            //页面销毁，当应用程序不在需要GPIO连接的时候，关闭Gpio资源
            mGpio.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            mGpio = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_open:

                setValue(true);
                break;
            case R.id.bt_close:

                setValue(false);
                break;

        }
    }

    private void setValue(boolean b) {
        if (mGpio == null) {
            return;
        }
        try {
            mGpio.setValue(!mGpio.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
