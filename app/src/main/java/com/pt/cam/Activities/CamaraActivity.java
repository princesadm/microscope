package com.pt.cam.Activities;

import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.pt.cam.Adapters.ModeAdapter;
import com.pt.cam.Model.Modes;
import com.pt.cam.R;
import com.pt.cam.Utiles.AppConstraint;
import com.pt.cam.databinding.ActivityCamaraBinding;
import com.shahryar.airbar.AirBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CamaraActivity extends AppCompatActivity {

    private ActivityCamaraBinding binding;
    private ArrayList<Modes> arrayList_modes;
    private ModeAdapter modeAdapter;

    private static final String TAG = CamaraActivity.class.getSimpleName();
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    private Uri imgUri = null;
    private String flashMode;
    private Camera camera;
    private final Executor executor = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCamaraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(CamaraActivity.this, R.color.purple_500));
        arrayList_modes = new ArrayList<>();
        arrayList_modes.add(new Modes("Obaervation", true));
        arrayList_modes.add(new Modes("Size Distribution", false));
        arrayList_modes.add(new Modes("Microworld", false));
        arrayList_modes.add(new Modes("Malaria", false));

        modeAdapter = new ModeAdapter(CamaraActivity.this, arrayList_modes);
        binding.rvData.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvData.setAdapter(modeAdapter);


//        binding.abBright.setMin(0);
        if (allPermissionsGranted()) {
            startCamera(); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);

            if (SDK_INT >= Build.VERSION_CODES.Q) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        }

        binding.llZoomInOut.setOnClickListener(v -> {
            if (binding.abZoom.getVisibility() == View.GONE) {
                binding.abZoom.setVisibility(View.VISIBLE);
                binding.tvZoomDigit.setVisibility(View.VISIBLE);
            } else {
                binding.abZoom.setVisibility(View.GONE);
                binding.tvZoomDigit.setVisibility(View.GONE);
            }
        });

        binding.llBright.setOnClickListener(v -> {
            if (binding.abBright.getVisibility() == View.GONE) {
                binding.abBright.setVisibility(View.VISIBLE);
                binding.tvBright.setVisibility(View.VISIBLE);
            } else {
                binding.abBright.setVisibility(View.GONE);
                binding.tvBright.setVisibility(View.GONE);
            }
        });

        binding.abZoom.setOnProgressChangedListener(new AirBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(@NonNull AirBar airBar, double v, double v1) {
                camera.getCameraControl().setLinearZoom((float) ((float) v / airBar.getMax()));
                binding.tvZoomDigit.setText(((int) v)+"");

                if (Integer.parseInt(String.valueOf((int)v)) >= 100 && Integer.parseInt(String.valueOf((int)v)) <=200) {
                    Toast.makeText(CamaraActivity.this, "Zoom Led 1 -->" + (int)v, Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(String.valueOf((int)v)) > 200) {
                    Toast.makeText(CamaraActivity.this, "zoom Led  2-->" + (int)v, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterProgressChanged(@NonNull AirBar airBar, double v, double v1) {
                binding.abZoom.setVisibility(View.GONE);
                binding.tvZoomDigit.setVisibility(View.GONE);
            }
        });

        binding.abBright.setOnProgressChangedListener(new AirBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(@NonNull AirBar airBar, double v, double v1) {
                binding.tvBright.setText(((int) v)+"");
//                camera.getCameraControl().setExposureCompensationIndex(Integer.parseInt(String.valueOf(airBar.getMax())));

                if (Integer.parseInt(String.valueOf((int)v)) >= 150 && Integer.parseInt(String.valueOf((int)v)) <= 300) {
                    Toast.makeText(CamaraActivity.this, "bright Led 1 -->" + (int)v, Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(String.valueOf((int)v)) > 300) {
                    Toast.makeText(CamaraActivity.this, "bright Led  2-->" + (int)v, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterProgressChanged(@NonNull AirBar airBar, double v, double v1) {
                binding.abBright.setVisibility(View.GONE);
                binding.tvBright.setVisibility(View.GONE);
            }
        });

        binding.llGallery.setOnClickListener(v -> {
            startActivity(new Intent(CamaraActivity.this, GallaryScreen.class));
        });

        binding.ivVideo.setTag(R.drawable.ic_baseline_videocam_24);
        binding.llVideo.setOnClickListener(v -> {
            if (binding.ivVideo.getTag().equals(R.drawable.ic_baseline_videocam_24)) {
                binding.ivCapture.setImageResource(R.drawable.ic_take_video);
                binding.ivVideo.setImageResource(R.drawable.ic_outline_camera_rear);
                binding.ivVideo.setTag(R.drawable.ic_outline_camera_rear);
            } else {
                binding.ivCapture.setImageResource(R.drawable.ic_take_picture);
                binding.ivVideo.setImageResource(R.drawable.ic_baseline_videocam_24);
                binding.ivVideo.setTag(R.drawable.ic_baseline_videocam_24);
            }
        });

        changeFlashIcon(R.drawable.ic_flash_on);
        binding.llFlash.setOnClickListener(v -> {
            if (binding.ivFlash.getTag().equals(R.drawable.ic_flash_on)) {
                changeFlashIcon(R.drawable.ic_flash_auto);
            } else if (binding.ivFlash.getTag().equals(R.drawable.ic_flash_auto)) {
                changeFlashIcon(R.drawable.ic_flash_off);
            } else if (binding.ivFlash.getTag().equals(R.drawable.ic_flash_off)) {
                changeFlashIcon(R.drawable.ic_flash_on);
            }
        });

        binding.viewFinder.setOnClickListener(v -> {
            int[] coordinates = new int[2];
            binding.viewFinder.getLocationOnScreen(coordinates);

// MotionEvent parameters
            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis();
            int action = MotionEvent.ACTION_DOWN;
            int x = coordinates[0];
            int y = coordinates[1];
            int metaState = 0;

// dispatch the event
            MotionEvent event = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
            tapToFocus(camera, binding.viewFinder, event);
        });


    }

    public void changeFlashIcon(int icon) {
        binding.ivFlash.setImageResource(icon);
        binding.ivFlash.setTag(icon);
    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

       /* //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // Query if extension is available (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }*/

        final ImageCapture imageCapture = builder
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());

        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture);
        camera.getCameraControl().setZoomRatio(0.1f);


        binding.llCapture.setOnClickListener(v -> {
            File folder = new File(AppConstraint.IMG_FOLDER_PATH);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            File file = new File(AppConstraint.IMG_FOLDER_PATH, System.currentTimeMillis() + ".jpg");
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
            imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(CamaraActivity.this, "Image Saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onError(@NonNull ImageCaptureException error) {
                    error.printStackTrace();
                }
            });
        });


    }

    public static void tapToFocus(Camera camera, PreviewView previewView, MotionEvent motionEvent) {
        if (camera != null && previewView != null && motionEvent.getAction() == 1) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            camera.getCameraControl().startFocusAndMetering(new FocusMeteringAction.Builder(new SurfaceOrientedMeteringPointFactory((float) previewView.getWidth(), (float) previewView.getHeight()).createPoint(x, y), FocusMeteringAction.FLAG_AF).setAutoCancelDuration(2, TimeUnit.SECONDS).build());
//            AnimUtils.moveImgFocus(imageView, x, y);
//            AnimUtils.animateImgFocus(imageView);
        }
    }


    public String getBatchDirectoryName() {

        String app_folder_path = "";
        app_folder_path = AppConstraint.IMG_FOLDER_PATH;
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {
            dir.mkdirs();
        }

        return app_folder_path;
    }

   /* private void updateTransform ( ) {
        Matrix mx = new Matrix();
        float w = binding.viewFinder.getMeasuredWidth();
        float h = binding.viewFinder.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int) binding.viewFinder.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        //mx.postRotate((float)rotationDgr, cX, cY);
        binding.viewFinder.setTransform(mx);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Toast.makeText(CamaraActivity.this, "click", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void setZoomInOutAndTapToFocuse() {
        ScaleGestureDetector.SimpleOnScaleGestureListener listener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {

                return true;

            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return super.onScaleBegin(detector);
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);
            }
        };

    }
}