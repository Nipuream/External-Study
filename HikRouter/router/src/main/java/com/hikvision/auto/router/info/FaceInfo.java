package com.hikvision.auto.router.info;

import java.util.Objects;

/**
 * 人脸信息
 */
public class FaceInfo {

    /**
     * 人脸ID
     */
    private int faceID;
    /**
     * 人脸对应的姓名
     */
    private String name;
    /**
     * 人脸对应的身份证号码
     */
    private String identityID;
    /**
     * 人脸对应的设备图片地址
     */
    private String faceUrl;
    /**
     * 人脸图片版本，用于比对是否需要更新图片
     */
    private String faceVersion;
    /**
     * 人脸组id，当存在人员分组时需要传入
     */
    private String groupID;

    /**
     * 设置模式，0新增人脸，1更新人脸，2删除人脸
     */
    private int mod;

    public FaceInfo(int faceID, String name, String identityID, String faceUrl, String faceVersion, String groupID, int mod) {
        this.faceID = faceID;
        this.name = name;
        this.identityID = identityID;
        this.faceUrl = faceUrl;
        this.faceVersion = faceVersion;
        this.groupID = groupID;
        this.mod = mod;
    }

    public int getFaceID() {
        return faceID;
    }

    public void setFaceID(int faceID) {
        this.faceID = faceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentityID() {
        return identityID;
    }

    public void setIdentityID(String identityID) {
        this.identityID = identityID;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public String getFaceVersion() {
        return faceVersion;
    }

    public void setFaceVersion(String faceVersion) {
        this.faceVersion = faceVersion;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public int getMod() {
        return mod;
    }

    public void setMod(int mod) {
        this.mod = mod;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FaceInfo faceInfo = (FaceInfo) o;
        return faceID == faceInfo.faceID &&
                mod == faceInfo.mod &&
                Objects.equals(name, faceInfo.name) &&
                Objects.equals(identityID, faceInfo.identityID) &&
                Objects.equals(faceUrl, faceInfo.faceUrl) &&
                Objects.equals(faceVersion, faceInfo.faceVersion) &&
                Objects.equals(groupID, faceInfo.groupID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(faceID, name, identityID, faceUrl, faceVersion, groupID, mod);
    }

    @Override
    public String toString() {
        return "FaceInfo{" +
                "faceID=" + faceID +
                ", name='" + name + '\'' +
                ", identityID='" + identityID + '\'' +
                ", faceUrl='" + faceUrl + '\'' +
                ", faceVersion='" + faceVersion + '\'' +
                ", groupID='" + groupID + '\'' +
                ", mod=" + mod +
                '}';
    }
}
