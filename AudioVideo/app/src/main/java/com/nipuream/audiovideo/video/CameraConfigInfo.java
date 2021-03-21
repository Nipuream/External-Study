package com.nipuream.audiovideo.video;

public class CameraConfigInfo {

    private int cameraFacingId;
    private int degress;
    private int textureWidth;
    private int textureHeight;

    public CameraConfigInfo(int cameraFacingId, int degress, int textureWidth, int textureHeight) {
        this.cameraFacingId = cameraFacingId;
        this.degress = degress;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public int getCameraFacingId() {
        return cameraFacingId;
    }

    public int getDegress() {
        return degress;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }
}
