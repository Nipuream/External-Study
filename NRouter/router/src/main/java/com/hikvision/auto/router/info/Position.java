package com.hikvision.auto.router.info;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "position")
public class Position {

    @Id(autoincrement = true)
    private Long id;

    /**
     * 定位精度
     */
    private float accuracy;
    /**
     * 经度
     */
    private double longitude;
    /**
     * 纬度
     */
    private double latitude;
    /**
     * 时间
     */
    private long time;
    /**
     * 海拔
     */
    private double altitude;
    /**
     * 速度
     */
    private float speed;
    /**
     * 方位
     */
    private float bearing;
    /**
     * 搜到的星数
     */
    private int satellites;
    public int getSatellites() {
        return this.satellites;
    }
    public void setSatellites(int satellites) {
        this.satellites = satellites;
    }
    public float getBearing() {
        return this.bearing;
    }
    public void setBearing(float bearing) {
        this.bearing = bearing;
    }
    public float getSpeed() {
        return this.speed;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public double getAltitude() {
        return this.altitude;
    }
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public float getAccuracy() {
        return this.accuracy;
    }
    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 1085055371)
    public Position(Long id, float accuracy, double longitude, double latitude,
            long time, double altitude, float speed, float bearing, int satellites) {
        this.id = id;
        this.accuracy = accuracy;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
        this.altitude = altitude;
        this.speed = speed;
        this.bearing = bearing;
        this.satellites = satellites;
    }

    public Position(float accuracy, double longitude, double latitude, long time, double altitude, float speed, float bearing, int satellites) {
        this.accuracy = accuracy;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
        this.altitude = altitude;
        this.speed = speed;
        this.bearing = bearing;
        this.satellites = satellites;
    }

    @Generated(hash = 958937587)
    public Position() {
    }

}
