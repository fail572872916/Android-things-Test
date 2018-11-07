package com.example.linkaruduino;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private UartDevice mUartDevice;


    private static final String UART_DEVICE_NAME = "UART0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mUartDevice = PeripheralManager.getInstance().openUartDevice(UART_DEVICE_NAME);
            configureUartFrame(mUartDevice);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mUartDevice.write(UART_DEVICE_NAME.getBytes(), UART_DEVICE_NAME.getBytes().length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "run: " + UART_DEVICE_NAME);
                    mHandler.postDelayed(this, 2000);

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        try {
            mUartDevice.registerUartDeviceCallback(mUartDeviceCallback);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUartDevice.unregisterUartDeviceCallback(mUartDeviceCallback);
    }
    private UartDeviceCallback mUartDeviceCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uartDevice) {
            try {
                readUartBuffer(uartDevice);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    };
    public void configureUartFrame(UartDevice uart) throws IOException {
        // Configure the UART port
        uart.setBaudrate(9600);
        uart.setDataSize(8);
        uart.setParity(UartDevice.PARITY_NONE);
        uart.setStopBits(1);
    }
    public void readUartBuffer(UartDevice uart) throws IOException {
        // Maximum amount of data to read at one time
        final int maxCount = 4096;
        byte[] buffer = new byte[maxCount];

        int count;
        while ((count = uart.read(buffer, buffer.length)) > 0) {
            Log.d(TAG, "Read " + count + " bytes from peripheral");
        }
    }
    public void setFlowControlEnabled(UartDevice uart, boolean enable) throws IOException {
        if (enable) {
            // Enable hardware flow control
            uart.setHardwareFlowControl(UartDevice.HW_FLOW_CONTROL_AUTO_RTSCTS);
        } else {
            // Disable flow control
            uart.setHardwareFlowControl(UartDevice.HW_FLOW_CONTROL_NONE);
        }
    }
}
