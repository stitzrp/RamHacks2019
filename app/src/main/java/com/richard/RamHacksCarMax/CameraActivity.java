package com.richard.RamHacksCarMax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.TextView;

import java.util.Arrays;

public class CameraActivity extends AppCompatActivity {
    TextureView textureView;
    CameraDevice cameraDevice;
    String cameraID;
    Size imageDimensions;
    CaptureRequest.Builder captureRequestBuilder;
    CameraCaptureSession cameraSession;
    Handler backgroundHandler;
    HandlerThread handlerThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        textureView = (TextureView) findViewById(R.id.texture);

        textureView.setSurfaceTextureListener(surfaceTextureListener);
    }


    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            try {
                openCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    private void openCamera() throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraID = cameraManager.getCameraIdList()[0];

        CameraCharacteristics cc = cameraManager.getCameraCharacteristics(cameraID);
        StreamConfigurationMap map = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        imageDimensions = map.getOutputSizes(SurfaceTexture.class)[0];

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        cameraManager.openCamera(cameraID, stateCallback, null);
    }

    CameraDevice.StateCallback stateCallback= new CameraDevice.StateCallback(){

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            cameraDevice = cameraDevice;
            try {
                startCameraPreview();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice=null;
        }
    };
    private void startCameraPreview() throws CameraAccessException {
        SurfaceTexture texture = textureView.getSurfaceTexture();
        texture.setDefaultBufferSize(imageDimensions.getWidth(), imageDimensions.getHeight());

        Surface surface = new Surface(texture);
        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        captureRequestBuilder.addTarget((surface));

        cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                if(cameraDevice==null){
                    return;
                }

                cameraSession=cameraCaptureSession;

                try {
                    updatePreview();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

            }
        },null );
    }

    private void updatePreview() throws CameraAccessException {
        if(cameraDevice==null)
        {
            return;
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        cameraSession.setRepeatingRequest(captureRequestBuilder.build(),null,backgroundHandler);
    }

    protected void onResume(){
        super.onResume();
        startBackroundThread();

        if(textureView.isAvailable()){
            try {
                openCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        else
        {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }

    }

    private void startBackroundThread() {
        handlerThread= new HandlerThread("Camera Background");
        handlerThread.start();
        backgroundHandler= new Handler(handlerThread.getLooper());
    }

    protected void onPause(){
        try {
            stopBackroundThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    private void stopBackroundThread() throws InterruptedException {
    handlerThread.quitSafely();
    handlerThread.join();

    backgroundHandler=null;
    handlerThread=null;

    }

}
