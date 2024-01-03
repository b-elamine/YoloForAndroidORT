package com.example.yoloonnx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    TextView modelName;
    Button importCamera, importLib, predictBtn;
    ImageButton cancel;
    private Bitmap capturedBitmap;
    ArrayList<String> models = new ArrayList<>();

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        modelName = findViewById(R.id.title);
        importCamera = findViewById(R.id.btnCamera);
        importLib = findViewById(R.id.btnLib);
        cancel = findViewById(R.id.btnCancel);
        previewView = findViewById(R.id.previewView);
        predictBtn =  findViewById(R.id.predict);

        if (!hasCameraPermission()) {
            requestCameraPermission();
        } else {
            startCamera();
            importCamera.setOnClickListener(v -> takePhoto());
            cancel.setOnClickListener(v -> deletePic());
        }
    }

    private void deletePic() {
        ImageView imageView = findViewById(R.id.ImageView);

        imageView.setVisibility(View.GONE);
        previewView.setVisibility(View.VISIBLE);
        predictBtn.setVisibility(View.VISIBLE);
        importCamera.setEnabled(true);
        cancel.setVisibility(View.GONE);
    }

    private void takePhoto() {
        if (imageCapture == null) {
            return;
        }

        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        // Image captured successfully
                        displayCapturedImage(image);
                        image.close();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        // Image capture failed, handle error
                    }
                });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageCapture.Builder builder = new ImageCapture.Builder();
                int aspectRatio = AspectRatio.RATIO_4_3; // Choose the aspect ratio you need

                builder.setTargetAspectRatio(aspectRatio);

                imageCapture = builder.build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (Exception e) {
                Log.e("CameraX", "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
                importCamera.setOnClickListener(v -> takePhoto());
            } else {
                // Permission denied, handle this scenario gracefully
            }
        }
    }

    private void displayCapturedImage(ImageProxy image) {
        // Handle captured image here (for example, display in ImageView)
        ImageView imageView = findViewById(R.id.ImageView); // Replace with your ImageView ID
        imageView.setVisibility(View.VISIBLE); // Show the ImageView
        cancel.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.GONE);
        importCamera.setEnabled(false);

        // Convert ImageProxy to a Bitmap and display it in the ImageView
        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];
        ByteBuffer buffer = planeProxy.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        // Create a source for the ImageDecoder
        ImageDecoder.Source source = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P ) {
            source = ImageDecoder.createSource(ByteBuffer.wrap(bytes));
        }

        // Decode the image, handling rotation automatically
        try {
            Bitmap bitmap = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(source);
            }
            imageView.setImageBitmap(bitmap);
            capturedBitmap = bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        image.close();
    }
}
