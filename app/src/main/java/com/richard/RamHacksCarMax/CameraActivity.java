package com.richard.RamHacksCarMax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    TextureView textureView;
    Button button;
    private static final SparseIntArray ORIENTATION = new SparseIntArray();
    CameraDevice cameraDevice;
    String cameraID;
    CaptureRequest captureRequest;
    CaptureRequest.Builder captureRequestBuilder;
    CameraCaptureSession cameraCaptureSession;
    private Size imageDimensions;
    private ImageReader imageReader;
    private File file;
    final String TAG = "";
    Handler mBackgroundHandler;
    HandlerThread mBackroundThread;


    static {
        ORIENTATION.append(Surface.ROTATION_0,90);
        ORIENTATION.append(Surface.ROTATION_90,0);
        ORIENTATION.append(Surface.ROTATION_180,270);
        ORIENTATION.append(Surface.ROTATION_270,180);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
      if(requestCode==101)
      {
          if(grantResults[0]== PackageManager.PERMISSION_DENIED){
              Toast.makeText(this, "Camera Permission must be turned on", Toast.LENGTH_SHORT).show();
          }
      }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: aisdjfisadjis");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        textureView = (TextureView) findViewById(R.id.texture);
        textureView.setSurfaceTextureListener(surfaceTextureListener);
        button= findViewById(R.id.button_capture);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    takePicture();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void takePicture() throws CameraAccessException {
        if(cameraDevice==null){
            return;
        }

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
        Size[] jpegSizes = null;
        jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);

        int width = 640;
        int height = 480;

        if(jpegSizes!= null && jpegSizes.length>0)
        {
            width = jpegSizes[0].getWidth();
            height = jpegSizes[0].getHeight();

        }

       // ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG,1);
          ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.RAW_SENSOR, 1);

        final List<Surface> outputSurfaces = new ArrayList<>(2);
        outputSurfaces.add(reader.getSurface());

        outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

        final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        captureBuilder.addTarget(reader.getSurface());
        captureBuilder.set(CaptureRequest.CONTROL_MODE,CameraMetadata.CONTROL_MODE_AUTO);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATION.get(rotation));

        Long taLong = System.currentTimeMillis()/1000;
        String ts = taLong.toString();

        file = new File(Environment.getExternalStorageDirectory() + "/" + ts +".jpg");
        Log.d(TAG, "takePicture: file location " + Environment.getExternalStorageDirectory().getPath());
        final ImageReader reader2 = reader;
        ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader imageReader) {
                Image image =null;

                image= reader2.acquireLatestImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes= new byte[buffer.capacity()];
                buffer.get(bytes);
                try {
                    save(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally
                {
                    if(image != null){
                        image.close();
                    }
                }
            }
        };
        reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
        final CameraCaptureSession.CaptureCallback captureListener =  new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(CameraCaptureSession session,CaptureRequest request,TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                Toast.makeText(getApplicationContext(),"Saved", Toast.LENGTH_LONG).show();

                try {
                    createCameraPreview();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        };
        cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {

            @Override
            public void onConfigured(CameraCaptureSession session) {
                try {
                    session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(CameraCaptureSession session) {
                Log.d(TAG, "onConfigureFailed: METHOD CALLED");
            }
        }, mBackgroundHandler);
    }

    private void save(byte[] bytes) throws IOException {
        OutputStream outputStream = null;
        outputStream = new FileOutputStream(file);
        outputStream.write(bytes);
        outputStream.close();
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

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice=camera;
            try {
                createCameraPreview();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int i) {
            cameraDevice.close();
            cameraDevice=null;
        }
    };

    private void createCameraPreview() throws CameraAccessException {
        SurfaceTexture texture = textureView.getSurfaceTexture();
        texture.setDefaultBufferSize(imageDimensions.getWidth(), imageDimensions.getHeight());
        Surface surface = new Surface(texture);
        captureRequestBuilder= cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        captureRequestBuilder.addTarget(surface);
        cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession session) {
                if(cameraDevice==null)
                {
                    return;
                }
                cameraCaptureSession =session;
                try {
                    updatePreview();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(CameraCaptureSession session) {
                Toast.makeText(getApplicationContext(),"config changed", Toast.LENGTH_LONG);
            }
        }, null);
    }
    private void openCamera() throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraID = cameraManager.getCameraIdList()[0];

        CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        imageDimensions = map.getOutputSizes(SurfaceTexture.class)[0];

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=  PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);

            return;
        }
        cameraManager.openCamera(cameraID, stateCallback, null);
    }


    /*private void startCameraPreview() throws CameraAccessException {
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

                CameraActivity.this.cameraCaptureSession =cameraCaptureSession;

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
*/
    private void updatePreview() throws CameraAccessException {
        if(cameraDevice==null)
        {
            return;
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),null, mBackgroundHandler);
    }

    @Override
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
        mBackroundThread = new HandlerThread("Camera Background");
        mBackroundThread.start();
        mBackgroundHandler = new Handler(mBackroundThread.getLooper());
    }

    @Override
    protected void onPause(){
        try {
            stopBackroundThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    private void stopBackroundThread() throws InterruptedException {
    mBackroundThread.quitSafely();
    mBackroundThread.join();

    mBackgroundHandler =null;
    mBackroundThread =null;
    }
}

