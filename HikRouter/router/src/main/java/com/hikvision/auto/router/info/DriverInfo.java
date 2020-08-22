package com.hikvision.auto.router.info;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 驾驶员信息
 */
@Entity(nameInDb = "driverInfo")
public class DriverInfo {

    @Id(autoincrement = true)
    private Long id;

    /**
     * 驾驶员唯一标识
     */
    @Unique
    private String driverIndexCode;

    /**
     * 驾驶员姓名
     */
    private String name;

    /**
     * 性别，0-男 1-女
     */
    private int sex;

    /**
     * 证件类型，0-运营资格证 1-身份证
     * 默认为运营资格证号
     */
    private int cardType;

    /**
     * 证件ID，运营资格证号或身份证号，采用UTF-8编码
     */
    private String cardID;

    /**
     * 驾驶员图片下载中心
     */
    private String photoUrl;

    /**
     * 当前驾驶员最新变更时间
     */
    private String lastTime;

    /**
     * 驾驶员信息版本
     */
    private int driverVersion;

    /**
     * 网络请求地址
     */
    private String netUrl;

    public String getNetUrl() {
        return this.netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }

    public int getDriverVersion() {
        return this.driverVersion;
    }

    public void setDriverVersion(int driverVersion) {
        this.driverVersion = driverVersion;
    }

    public String getLastTime() {
        return this.lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCardID() {
        return this.cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }

    public int getCardType() {
        return this.cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public int getSex() {
        return this.sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDriverIndexCode() {
        return this.driverIndexCode;
    }

    public void setDriverIndexCode(String driverIndexCode) {
        this.driverIndexCode = driverIndexCode;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 226968352)
    public DriverInfo(Long id, String driverIndexCode, String name, int sex,
            int cardType, String cardID, String photoUrl, String lastTime,
            int driverVersion, String netUrl) {
        this.id = id;
        this.driverIndexCode = driverIndexCode;
        this.name = name;
        this.sex = sex;
        this.cardType = cardType;
        this.cardID = cardID;
        this.photoUrl = photoUrl;
        this.lastTime = lastTime;
        this.driverVersion = driverVersion;
        this.netUrl = netUrl;
    }

    public DriverInfo(String driverIndexCode, String name, int sex, int cardType, String cardID, String photoUrl, String lastTime, int driverVersion, String netUrl) {
        this.driverIndexCode = driverIndexCode;
        this.name = name;
        this.sex = sex;
        this.cardType = cardType;
        this.cardID = cardID;
        this.photoUrl = photoUrl;
        this.lastTime = lastTime;
        this.driverVersion = driverVersion;
        this.netUrl = netUrl;
    }

    @Generated(hash = 2077275369)
    public DriverInfo() {
    }


}
