package com.pt.cam.Model;

import java.io.File;

public class ImgVid {
    File file;
    boolean isVideo=false;

    public ImgVid(File file, boolean isVideo) {
        this.file = file;
        this.isVideo = isVideo;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }
}
