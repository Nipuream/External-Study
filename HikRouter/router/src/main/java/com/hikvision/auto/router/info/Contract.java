package com.hikvision.auto.router.info;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "contract")
public class Contract {

    /**
     * 标志
     * 1:呼入 2:呼出 3:呼入/呼出
     */
    private int flag;

    /**
     * 电话号码，最长20bytes
     */
    @Id
    public String phoneNumber;

    /**
     * 联系人
     */
    public String contract;

    public String getContract() {
        return this.contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Generated(hash = 1775124491)
    public Contract(int flag, String phoneNumber, String contract) {
        this.flag = flag;
        this.phoneNumber = phoneNumber;
        this.contract = contract;
    }

    @Generated(hash = 1343858295)
    public Contract() {
    }


}
