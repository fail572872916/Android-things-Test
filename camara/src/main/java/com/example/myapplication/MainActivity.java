package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

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
public class MainActivity extends Activity {
    public static String wwwRoot = Environment.getExternalStorageDirectory() + "/cameracar-www/";
    private DoorbellCamera mCamera;
    private static final String TAG = "MainActivity";
    /**
     * Driver for the doorbell button;
     */
    private ButtonInputDriver mButtonInputDriver;

    ImageView mImShow;
    /**
     * A {@link Handler} for running Camera tasks in the background.
     */
    private Handler mCameraHandler;

    /**
     * An additional thread for running Camera tasks that shouldn't block the UI.
     */
    private HandlerThread mCameraThread;

    /**
     * A {@link Handler} for running Cloud tasks in the background.
     */


        Bitmap mBitmap ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Doorbell Activity created.");
        mImShow = findViewById(R.id.im_image);
        // We need permission to access the camera
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // A problem occurred auto-granting the permission
            Log.e(TAG, "No permission");
            return;
        }


        initCamera();

        initPIO();

    }

    private void initPIO() {
        try {
            mButtonInputDriver = new ButtonInputDriver(
                    BoardDefaults.getGPIOForButton(),
                    Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_ENTER);
            mButtonInputDriver.register();
        } catch (IOException e) {
            mButtonInputDriver = null;
            Log.w(TAG, "Could not open GPIO pins", e);
        }
    }

    public void initCamera() {

        // We need permission to access the camera

        if (checkSelfPermission(Manifest.permission.CAMERA)

                != PackageManager.PERMISSION_GRANTED) {

            // A problem occurred auto-granting the permission

            Log.d(TAG, "PhotoCamera No permission");


            return;

        }


        //imageView = (ImageView)findViewById(R.id.imageView);


        DoorbellCamera.dumpFormatInfo(this);

        Log.d(TAG, "PhotoCamera inited");


        // Creates new handlers and associated threads for camera and networking operations.

        mCameraThread = new HandlerThread("CameraBackground");

        mCameraThread.start();

        mCameraHandler = new Handler(mCameraThread.getLooper());


        // Camera code is complicated, so we've shoved it all in this closet class for you.

        mCamera = DoorbellCamera.getInstance();

        mCamera.initializeCamera(this, mCameraHandler, mOnImageAvailableListener);



        /*imageView.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                Log.i("takePicture", "click image to take picture");

                mCamera.takePicture();

            }

        });*/

    }
    // 构建Runnable对象，并在runnable中更新UI
    Runnable   udpUIRunnable=new  Runnable(){
        @Override
        public void run() {

            mImShow.setImageBitmap(mBitmap); //更新UI
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.shutDown();

        mCameraThread.quitSafely();

        try {
            mButtonInputDriver.close();
        } catch (IOException e) {
            Log.e(TAG, "button driver error", e);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Doorbell rang!
            Log.d(TAG, "button pressed");
            mCamera.takePicture();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Listener for new camera images.
     */
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireLatestImage();
                    // get image bytes
                    ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
                    final byte[] imageBytes = new byte[imageBuf.remaining()];
                    imageBuf.get(imageBytes);
                    image.close();

                    onPictureTaken(imageBytes);
                }
            };


    /**
     * Handle image processing in Firebase and Cloud Vision.
     */

    private void onPictureTaken(final byte[] imageBytes) {

        Log.d(TAG, "PhotoCamera onPictureTaken");

        if (imageBytes != null) {

            String imageStr = Base64.encodeToString(imageBytes, Base64.NO_WRAP | Base64.URL_SAFE);

            Log.d(TAG, "imageBase64:" + imageStr);


            final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            mBitmap=bitmap;
            if (bitmap != null) {

                File f1 = new File(wwwRoot);
                File file = new File(wwwRoot + "pic.jpg");//将要保存图片的路径
                if (!f1.exists()) {
                    f1.mkdir();
                }
                try {

                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                    bos.flush();

                    bos.close();

                    handler.sendMessage(handler.obtainMessage(22, bitmap));
                } catch (IOException e) {

                    e.printStackTrace();

                }


            }

        }

    }

    /**
     * 接收解析后传过来的数据
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bitmap model = (Bitmap) msg.obj;
            showPictures(model);
        }
    };

    private void showPictures(Bitmap model) {
        mImShow.setImageBitmap(model);
    }
}