package com.pt.cam.Utiles;

import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.pt.cam.R;


import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;



public class AppUtils {

    private static AlertDialog dialog;
    public static String imageFilePath = "", videoFilePath = "";


    public static boolean isNetworkAvailable(Context activity) {

        if (activity != null) {
            ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                removeSimpleProgressDialog();
                return false;
            }
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info == null) {
                removeSimpleProgressDialog();
                return false;
            }
            for (NetworkInfo state : info) {
                if (state.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
            removeSimpleProgressDialog();
            return false;
        } else {
            return false;
        }
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void openKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void showSimpleProgressDialog(Context context) {
        //showSimpleLoadingDialog(context);
    }

    public static LinearLayoutManager setSmoothScroll(Context context, LinearLayoutManager layoutManager, int position) {
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(context) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        smoothScroller.setTargetPosition(position);
        layoutManager.startSmoothScroll(smoothScroller);

        return layoutManager;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("HardwareIds")
    public static String getDeviceID(Context context) {

        // creating a hex string
        String identifier = "";

        String pseudoId = "35" +
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10;

        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String btId = "";

        if (bluetoothAdapter != null) {
            btId = bluetoothAdapter.getAddress();
        }

        String longId = pseudoId + androidId + btId;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(longId.getBytes(), 0, longId.length());

            // get md5 bytes
            byte md5Bytes[] = messageDigest.digest();


            for (byte md5Byte : md5Bytes) {
                int b = (0xFF & md5Byte);

                // if it is a single digit, make sure it have 0 in front (proper padding)
                if (b <= 0xF) {
                    identifier += "0";
                }

                // add number to string
                identifier += Integer.toHexString(b);
            }

            // hex string to uppercase
            identifier = identifier.toUpperCase();

        } catch (Exception e) {
            L.e(e.getMessage());
            e.printStackTrace();
        }

        L.e("Device identifier " + identifier);

        return identifier;
    }


    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        AppUtils.imageFilePath = image.getAbsolutePath();
        return image;
    }

    public static String comrpess_50(String ref_path, File file) {
        File imgfile = new File(ref_path);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap myBitmap = BitmapFactory.decodeFile(imgfile.getAbsolutePath(), options);
            // Bitmap compressed_bitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / 5, myBitmap.getHeight() / 5, true);
            Bitmap compressed_bitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, false);

            Log.i("file=", file + "");
            Log.i("file size=", Integer.parseInt(String.valueOf(file.length() / 1024)) + "");
            Log.i("file size conpress=", Integer.parseInt(String.valueOf(file.length() / 1024)) / 20 + "");

            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + AppConstraint.DOWNLOAD_FOLDER_PATH;
            File saveFile = new File(imagesDir + file.getName());
            FileOutputStream out = new FileOutputStream(saveFile);
            compressed_bitmap.compress(Bitmap.CompressFormat.JPEG, 15, out);
            out.flush();
            out.close();
//            imgfile.delete();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    public static void deleteFile(File fileOrDirectory) {
        try {

            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    child.delete();

        } catch (Exception e) {
            e.getMessage();
        }

    }

    public static String compressImage(Context context, String imageUri, String ref_path) {

        String filePath = getRealPathFromURI(context, imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
//        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(imageUri);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 0) {
                matrix.postRotate(0);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename(ref_path);
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public static String getFilename(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public static String getRealPathFromURI(Context context, String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    /*public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
*/

    public static void saveImage(Context context, Bitmap bitmap, @NonNull String name) {
        AppUtils.showSimpleProgressDialog(context);
        boolean saved;
        OutputStream fos;
        File saveFile = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis() + ".png");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
//                contentValues.put(MediaStore.MediaColumns.ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM" + AppConstraint.DOWNLOAD_FOLDER_PATH);
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(imageUri);
                saveFile = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM).toString() + AppConstraint.DOWNLOAD_FOLDER_PATH + name + ".png");
//                comrpess_50(saveFile.getAbsolutePath(),saveFile);
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM).toString() + AppConstraint.DOWNLOAD_FOLDER_PATH;
                File file = new File(imagesDir);

                if (!file.exists()) {
                    file.mkdir();
                }

                File image = new File(imagesDir, System.currentTimeMillis() + ".png");
                fos = new FileOutputStream(image);
                saveFile = image;
//                comrpess_50(file.getAbsolutePath(),file);
            }


            saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

//            comrpess_50(saveFile.getAbsolutePath(), saveFile);
            compressImage(context, saveFile.getAbsolutePath(), Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + AppConstraint.DOWNLOAD_FOLDER_PATH);

        } catch (IOException e) {
            e.printStackTrace();
            AppUtils.removeSimpleProgressDialog();
        }
        AppUtils.removeSimpleProgressDialog();

    }

    public static void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(AppConstraint.DOWNLOAD_FOLDER_PATH);

        if (!direct.exists()) {
            File wallpaperDirectory = new File(AppConstraint.DOWNLOAD_FOLDER_PATH);
            wallpaperDirectory.mkdirs();
        }

        File file = new File(AppConstraint.DOWNLOAD_FOLDER_PATH, fileName);
//        if (file.exists()) {
//            file.delete();
//        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void MakeDirectory(Context context) {

        if (SDK_INT >= Build.VERSION_CODES.R) {
            ContextWrapper cw = new ContextWrapper(context);
            File file = cw.getExternalFilesDir(Environment.DIRECTORY_DCIM + AppConstraint.DOWNLOAD_FOLDER_PATH);
            if (!file.exists()) {

                ContextWrapper c = new ContextWrapper(context);
                File myDir = c.getExternalFilesDir(Environment.DIRECTORY_DCIM + AppConstraint.DOWNLOAD_FOLDER_PATH);
                File dmyDir = c.getExternalFilesDir(AppConstraint.DOWNLOAD_FOLDER_PATH);
                myDir.mkdirs();
                dmyDir.mkdirs();

                Log.i("directory===>", "making successfuly");

            } else {

                Log.i("Directory is", "Exist");

            }
        } else {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + AppConstraint.DOWNLOAD_FOLDER_PATH);
            if (!file.exists()) {

                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + AppConstraint.DOWNLOAD_FOLDER_PATH);
                File dmyDir = new File(AppConstraint.DOWNLOAD_FOLDER_PATH);
                myDir.mkdirs();
                dmyDir.mkdirs();

                Log.i("directory===>", "making successfuly");

            } else {

                Log.i("Directory is", "Exist");

            }
        }


    }

    public static File createVideoFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "VIDEO_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File image = File.createTempFile(imageFileName, ".mp4", storageDir);
        AppUtils.videoFilePath = image.getAbsolutePath();
        return image;
    }



    public static File saveImage(ImageView iv) {
        BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        FileOutputStream outStream = null;

        File file = null;
        // Write to SD Card
        try {
        // File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(AppConstraint.DOWNLOAD_FOLDER_PATH);
            dir.mkdirs();

            String fileName = String.format("%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);

            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();

            Log.d("TAG", "onPictureTaken - wrote to " + outFile.getAbsolutePath());

            file = outFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File saveImage(Bitmap bitmap,Context context) {
        File file = null;
        try {
            file = new File(AppConstraint.DOWNLOAD_FOLDER_PATH + System.currentTimeMillis() + ".png");
            file.createNewFile();

//Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            L.e("" + e.getMessage());
            e.printStackTrace();
            return file; // it will return null
        }

    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public static Uri getLocalBitmapUri(ImageView imageView,Context context) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "shareImage_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    // Method when launching drawable within Glide
    public Uri getBitmapFromDrawable(Bitmap bmp,Context context){

        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "shareImage_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();

            // wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
            bmpUri = FileProvider.getUriForFile(context, "com.pt.levench.fileprovider", file);  // use this version for API >= 24

            // **Note:** For API < 24, you may use bmpUri = Uri.fromFile(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    public static void launchPlayStore(Context context, String packageName) {
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + packageName));
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }



    public static Bitmap convertBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public static File bitmapToFile(Bitmap bitmap) { // File name like "image.png"
        //create a file to write bitmap data

        File file = null;
        try {
            file = new File(AppConstraint.DOWNLOAD_FOLDER_PATH + System.currentTimeMillis() + ".png");
            file.createNewFile();

//Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            L.e("" + e.getMessage());
            e.printStackTrace();
            return file; // it will return null
        }
    }

    public static String getPath(Uri uri, Context context) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }


    public static void loadImage(Context context, ImageView imageView, String url) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Glide.with(context).clear(imageView);
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(true)
                    //.centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT)).load(url).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imageView);
        } else {
            Glide.with(context).clear(imageView);
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    //.centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT)).load(url).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imageView);
        }
    }

    public static void loadImage(Context context, ImageView imageView, Uri url) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Glide.with(context).clear(imageView);
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(true)
                    //.centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT)).load(url).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imageView);
        } else {
            Glide.with(context).clear(imageView);
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    //.centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT)).load(url).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imageView);
        }
    }

    public static void loadImage(Context context, ShapeableImageView imageView, Uri url) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Glide.with(context).clear(imageView);
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(true)
                    //.centerCrop()
                    .dontAnimate()
                    .transform(new BitmapTransformation() {
                        @Override
                        protected Bitmap transform(@NonNull @NotNull BitmapPool pool, @NonNull @NotNull Bitmap toTransform, int outWidth, int outHeight) {
                            return rotateBitmap(toTransform, 90);
                        }

                        @Override
                        public void updateDiskCacheKey(@NonNull @NotNull MessageDigest messageDigest) {

                        }
                    })
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT)).load(url).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imageView);
        } else {
            Glide.with(context).clear(imageView);
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    //.centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT)).load(url).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imageView);
        }
    }

    public static void loadImage(Context context, ImageView imageView, Bitmap url) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Glide.with(context).clear(imageView);
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(true)
                    //.centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT)).load(url).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imageView);
        } else {
            Glide.with(context).clear(imageView);
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    //.centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT)).load(url).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imageView);
        }
    }

    public static void loadImage(Context context, ShapeableImageView imageView, Bitmap url) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Glide.with(context).clear(imageView);
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(true)
                    //.centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT)).load(url).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imageView);
        } else {
            Glide.with(context).clear(imageView);
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    //.centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT)).load(url).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imageView);
        }
    }

    public static void loadImage(Context context, ShapeableImageView imageView, String url, int default_image) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Glide.with(context).clear(imageView);
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(true)
                    //.centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT)).load(url).placeholder(default_image).error(default_image).into(imageView);
        } else {
            Glide.with(context).clear(imageView);
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    //.centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT)).load(url).placeholder(default_image).error(default_image).into(imageView);
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public class MyTransformation extends BitmapTransformation {
        private final int mOrientation;

        public MyTransformation(int orientation) {
            super();
            mOrientation = orientation;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            int exifOrientationDegrees = getExifOrientationDegrees(mOrientation);
            return TransformationUtils.rotateImageExif(pool, toTransform, exifOrientationDegrees);
        }

        private int getExifOrientationDegrees(int orientation) {
            int exifInt;
            switch (orientation) {
                case 90:
                    exifInt = ExifInterface.ORIENTATION_ROTATE_90;
                    break;
                // other cases
                default:
                    exifInt = ExifInterface.ORIENTATION_NORMAL;
                    break;
            }
            return exifInt;
        }

        @Override
        public void updateDiskCacheKey(@NonNull @NotNull MessageDigest messageDigest) {

        }
    }


    //new Image Rotation

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }
    

}
