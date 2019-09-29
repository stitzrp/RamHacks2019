package com.richard.RamHacksCarMax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.net.Uri;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

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
    private IntentIntegrator qrScan;
    String stockNum=""; //the stock number


    static {
        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Camera Permission must be turned on", Toast.LENGTH_SHORT).show();
            }
        }
    }



//Javier

    //method launches the check saleable activity and passes number as parameter
    private void checkSalable( String num ){


        // create and launch intent
        final Intent saleCheck = new Intent(CameraActivity.this,getSaleable.class);

        Bundle bundle = new Bundle();
        bundle.putString("num", num);
        saleCheck.putExtra("bundle", bundle);
        startActivity(saleCheck);
    }
//end Javier


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: aisdjfisadjis");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        textureView = (TextureView) findViewById(R.id.texture);
        textureView.setSurfaceTextureListener(surfaceTextureListener);
        button = findViewById(R.id.button_capture);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                    takePicture2();


                    //TODO uncomment the line below once the stocknumber is found from the QR code


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void takePicture2() {

        IntentIntegrator qrScan;
        //Javier


        qrScan = new IntentIntegrator(this);
        qrScan.initiateScan();
        Log.d(TAG, "takePicture2: WE SCANNED");        

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            Log.d(TAG, "onActivityResult: GOT AN EMPTY STRING");
            //if qrcode has nothing in it
            if (result.getContents() == null) {

                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());

                    stockNum=obj.toString();
                    checkSalable(stockNum);

                    // Toast.makeText(this, obj.toString(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                   // Toast.makeText(this, result.getContents()+"___________", Toast.LENGTH_LONG).show();
                    stockNum=result.getContents();
                    checkSalable(stockNum);


                    checkSalable(stockNum);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /////////////////////////////////////




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

