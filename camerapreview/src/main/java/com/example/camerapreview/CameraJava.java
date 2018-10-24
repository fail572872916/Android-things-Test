package com.example.camerapreview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by hyunwook on 2018-07-10.
 */

public class CameraJava {

    private Context context;
    private String cameraId;
    private CameraDevice cameraDevice;
    private Size imageDimension;

    private ImageReader imageReader;
    private TextureView textureView;

    private CaptureRequest captureRequest;
    private CaptureRequest.Builder captureRequestBuilder;
    private CameraCaptureSession cameraCaptureSession;

    private Handler mBackgroundHandler;

    //private ShutterCallback mShutterCallback;

    static final String TAG = CameraJava.class.getSimpleName();
    public static final CameraJava Instance= new CameraJava();

    public void openCamera(Context context, TextureView textureView, Handler backgroundHandler) {
        this.context = context;
        this.textureView = textureView;
        this.mBackgroundHandler = backgroundHandler;

        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        try {
            this.cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            imageDimension = map.getOutputSizes(SurfaceTexture.class)[2];

            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //close
    public void closeCamera() {
        cameraDevice.close();
        cameraDevice = null;

        imageReader.close();
        imageReader = null;
    }

    /**
     * CameraDevice.StateCallback Interface.
     */

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice p0) {
            cameraDevice = p0;
            createCameraPreview();
        }



        @Override
        public void onDisconnected(@NonNull CameraDevice p0) {
            cameraDevice.close();

        }

        @Override
        public void onError(@NonNull CameraDevice p0, int p1) {
            cameraDevice.close();
            cameraDevice = null;

        }
    };

    private void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();

            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());

            Surface surface = new Surface(texture);

            //카메라 미리보기에 요청
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            //CaptureRequestBuild 취득
            cameraDevice.createCaptureSession(new ArrayList<>(Collections.singleton(surface)), new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigureFailed(CameraCaptureSession p0) {
                    Toast.makeText(context, "Configuration change", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onConfigured(CameraCaptureSession p0) {
                    if (cameraDevice == null) {
                        return;
                    }

                    cameraCaptureSession = p0;
                    updatePreview();
                }

            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void updatePreview() {
        if (cameraDevice == null) {
            Log.d(TAG, "updatePreview error..");
        }

        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


}
