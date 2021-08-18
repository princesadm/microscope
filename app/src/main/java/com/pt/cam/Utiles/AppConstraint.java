package com.pt.cam.Utiles;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class AppConstraint {

    //API URLS

    public static final String DOWNLOAD_FOLDER_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Cam" + File.separator + "downloadImage" + File.separator;
    public static final String IMG_FOLDER_PATH = Environment.getExternalStorageDirectory() + File.separator + "Cam" + File.separator + "downloadImage" + File.separator;

}
