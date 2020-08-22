package com.hikvision.auto.router.info;

public class FileModel {

    /**
     * 文件类型
     */
    private int fileType;
    /**
     * 文件路径
     */
    private String name;
    /**
     * 缩略图路径
     */
    private String thumbnail;
    /**
     * GPS文件路径
     */
    private String gps;
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * 时长
     */
    private int duration;
    /**
     * 文件大小
     */
    private int fileSize;
    /**
     * 加锁状态
     */
    private int locked;
    /**
     * 通道号
     */
    private int channel;
    /**
     * 经度
     */
    private long startLon;
    /**
     * 纬度
     */
    private long startLat;
    /**
     * 录像类型
     */
    private int type;

    public FileModel(int fileType, String name, String thumbnail, String gps, long startTime, long endTime, int duration, int fileSize, int locked, int channel, long startLon, long startLat, int type) {
        this.fileType = fileType;
        this.name = name;
        this.thumbnail = thumbnail;
        this.gps = gps;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.fileSize = fileSize;
        this.locked = locked;
        this.channel = channel;
        this.startLon = startLon;
        this.startLat = startLat;
        this.type = type;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public long getStartLon() {
        return startLon;
    }

    public void setStartLon(long startLon) {
        this.startLon = startLon;
    }

    public long getStartLat() {
        return startLat;
    }

    public void setStartLat(long startLat) {
        this.startLat = startLat;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "fileType=" + fileType +
                ", name='" + name + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", gps='" + gps + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                ", fileSize=" + fileSize +
                ", locked=" + locked +
                ", channel=" + channel +
                ", startLon=" + startLon +
                ", startLat=" + startLat +
                ", type=" + type +
                '}';
    }
}
