package com.hikvision.auto.router.utils;


/**
 * Created by songyao on 2016/5/21.
 */
public class StringUtil {

    public static boolean isEmpty(String text){
        boolean isEmpty = false;
        if(text == null || "".equals(text.trim()))
            isEmpty = true;
        return isEmpty;
    }

    public static int str2Int(String str) {
        if (isEmpty(str)) {
            return 0;
        }
        int ret;
        try {
            ret = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            ret = 0;
        }
        return ret;
    }

    /**
     * @param bytes 字节数组
     * @desc bcd码转字符串，一个字节转两个数字
     * @author niechaoqun
     * @time 2016/12/12 15:26
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((bytes[i] & 0xf0) >> 4);
            temp.append((bytes[i] & 0x0f));
        }
        return temp.toString();
    }

    /**
     * @param
     * @desc byte[]的16进制字符串表示
     * @author niechaoqun
     * @time 2016/12/16 11:10
     */
    public static String getHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length);
        String hex;
        for (int i = 0; i < bytes.length; i++) {
            hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static int indexOfArray(String[] str, String value){
        if (str != null && str.length != 0 && value != null) {
            for(int i = 0; i < str.length; i++){
                if(str[i].equals(value)){
                    return i;
                }
            }
        }
        return 1;
    }


}
