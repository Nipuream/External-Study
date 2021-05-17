package com.hikvision.auto.router.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 数据类型转换/转义
 * Created by xufulong5 on 2017/3/23.
 */
public class ParseUtil {
    /**
     * 将字符串转换成二进制
     * @param str 字符串
     * @return 字节数组
     */
    public static byte[] stringToByte(String str){
        try {
            return (str+"\0").getBytes("GBK");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将整数转换成二进制字节（先低字节后高字节）
     * @param iSource 原始数据
     * @param iArrayLen 数据长度
     * @return byte[]
     */
    public static byte[] int2BytesL(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
        }
        return bLocalArr;
    }


    /**
     * 整型数据转换成字节数组,高位在前
     * 与intToByteArray不同的是改方法不会新建数组临时变量，直接通过传入数组作为容器
     *
     * @param data 整型数据
     * @return 字节数组
     */
    public static void intToByteArrayByCopy(int data, byte[] toWhere, int offset) {
        toWhere[offset] = (byte) ((data >> 24) & 0xff);
        toWhere[offset + 1] = (byte) ((data >> 16) & 0xff);
        toWhere[offset + 2] = (byte) ((data >> 8) & 0xff);
        toWhere[offset + 3] = (byte) (data & 0xff);
    }

    /**
     * 将整数转换成二进制字节（先高字节后低字节）
     * @param iSource 原始数据
     * @param iArrayLen 数据长度
     * @return byte[]
     */
    public static byte[] int2BytesH(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte) (iSource >> (8 * (iArrayLen - i - 1)) & 0xFF);
        }
        return bLocalArr;
    }

    /**
     * 获取指定长度字节数
     * @param src 原始数据
     * @param startIndex 开始位置
     * @param length 长度
     * @return 字节数组
     */
    public static byte[] byteTobyte(byte[] src, int startIndex, int length)
    {
        byte[] des = new byte[length];
        int i = 0;
        for (int j = startIndex; i < length; ++j) {
            des[i] = src[j];
            ++i;
        }
        return des;
    }

    /**将二进制转换成16进制
     * @param buf 字节
     * @return String
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (byte aBuf : buf) {
            String hex = Integer.toHexString(aBuf & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**将16进制转换为二进制
     * @param hexStr 十六进制字符串
     * @return 字节数组
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * Bit转Byte
     */
    public static byte BitToByte(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }

    /**
     * 为保证数据传输的透明，需对信息字段中出现的标志位进行转义处理，定义如下
     *  7EH 《————》 7DH+02H；
     *  7DH 《————》 7DH+01H；
     * @param str 字符串
     * @param type 类型
     * @return 转义后的数组
     */
    public static byte[] tropeYXByte(String str, int type){

        byte[] result;
        if(type == 1){
            str = replaceStr(str,"7d", "7d01");
            str = replaceStr(str,"7e", "7d02");
        }
		result = ParseUtil.parseHexStr2Byte(str);

        return result;
    }

    /**
     * 转义操作
     * @param str 字符串
     * @param thq thq
     * @param thh thh
     * @return String
     */
    private static String replaceStr(String str, String thq, String thh){

        StringBuilder des = new StringBuilder();
        String s = "";
        str = str+" ";
        for(int i=0;i< str.length();i++){
            if(i%2==0){
                if(s.equals(thq)){
                    s = thh;
                }
                des.append(s);
                s = "";
                s = s+str.charAt(i);
            }else{
                s = s+str.charAt(i);
            }
        }
        return des.toString();
    }

    /**
     * 4个字符转2个字符
     * @param data 原始数据
     * @param thq thq
     * @param thh thh
     * @return String
     */
    private static String replaceFourToTwo(String data, String thq, String thh){
        StringBuffer dataBuffer = new StringBuffer();
        for(int i=0;i< data.length();i+=2){
            String dStr = data.substring(i, i+2);
            dataBuffer.append(dStr);
            String desc = dataBuffer.toString();
            if(desc.endsWith(thq)){
                dataBuffer = new StringBuffer();
                desc = desc.substring(0, desc.length()-4);
                dataBuffer.append(desc);
                dataBuffer.append("##");
            }
        }
        return dataBuffer.toString().replaceAll("##", thh);
    }

    /**
     * 转义
     * @param bytes 字节数组
     * @return byte[]
     */
    static byte[] yxReversal(byte[] bytes){
        String data = parseByte2HexStr(bytes).toUpperCase();
        if(data.contains("7D02")){
            data = replaceFourToTwo(data, "7D02", "7E");
        }

        if(data.contains("7D01")){
            data = replaceFourToTwo(data, "7D01", "7D");
        }
        return parseHexStr2Byte(data);
    }

    /**
     * 高低位反相排序
     * @param bytes 字节数组
     * @return byte[]
     */
    public static byte[] sortToByte(byte[] bytes){
        byte[] des = new byte[bytes.length];
        int i = 0;
        for (int j = bytes.length-1; j >=0; j--) {
            des[i] = bytes[j];
            i++;
        }
        return des;
    }

    /**
     * 异或运算和
     * @param bytes 字节数组
     * @return byte[]
     */
    public static byte[] byteOrbyte(byte[] bytes){
        byte[] orByte = new byte[1];
        byte value = bytes[0];
        for (int i = 1; i < bytes.length; i++) {
            value = (byte) (value^bytes[i]);
        }
        orByte[0] = value;
        return orByte;
    }

    /**
     * 将二进制转换成字符串
     * @param src 原始数组
     * @param startIndex 开始位置
     * @param length 数组长度
     * @return String
     */
    public static String byteToString(byte[] src, int startIndex, int length){
        byte[] des = new byte[length];
        int i = 0;
        for (int j = startIndex; i < length; ++j) {
            des[i] = src[j];
            ++i;
        }
        String str = null;
        try {
            str= new String(des,"GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str != null ? str.trim() : "";
    }

    /**
     * 将二进制转换成字符串
     * @param src 原始字节
     * @return String
     */
    static String singleByteToString(byte src){
        byte[] des = new byte[1];
        des[0] = src;
        String str = null;
        try {
            str= new String(des,"gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str != null ? str.trim() : "";
    }

    /**
     * 字节转换成位
     * @param bytes 字节数组
     * @return String
     */
    public static String byteTobit(byte[] bytes){
        String str = "";
        for (byte aByte : bytes) {
            for (int i = 7; i >= 0; --i) {
                str += (aByte & (1 << i)) == 0 ? '0' : '1';
            }
        }
        return str;
    }

    /**
     * BCD码转为10进制串(阿拉伯数据)
     * @param bytes 字节数组
     * @return 转换后的字符串
     */
    public static String bcd2Str(byte[] bytes){
        StringBuilder temp=new StringBuilder(bytes.length*2);

        for (byte aByte : bytes) {
            temp.append((byte) ((aByte & 0xf0) >>> 4));
            temp.append((byte) (aByte & 0x0f));
        }
        return temp.toString();
    }

    public static String bcd2Str(char[] bytes){
        StringBuilder temp=new StringBuilder(bytes.length*2);

        for (char aByte : bytes) {
            temp.append((byte) ((aByte & 0xf0) >>> 4));
            temp.append((byte) (aByte & 0x0f));
        }
        return temp.toString();
    }

    public static String bcd2Str(byte[] bytes , int offset , int count){
        StringBuilder temp=new StringBuilder(count*2);

        for (int i = offset ; i < offset + count ; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString();
    }

    /**
     * 10进制串转为BCD码
     * @param asc 字符串
     * @return BCD码
     */
    public static byte[] str2Bcd(String asc) {
        if (StringUtil.isEmpty(asc)) {
            return null;
        }
        asc = asc.replaceAll("\\.", "");
        int len = asc.length();
        int mod = len % 2;

        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        byte abt[];
        if (len >= 2) {
            len = len / 2;
        }

        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length()/2; p++) {
            if ( (abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ( (abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ( (abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ( (abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            }else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * 10进制串转为BCD码
     * @param asc 字符串
     * @return BCD码
     */
    public static byte[] str2Bcd(String asc,int length) {
        if (StringUtil.isEmpty(asc)) {
            return null;
        }
        asc = asc.replaceAll("\\.", "");
        int len = asc.length();
        int bcdLength = length *2;
        if(len < bcdLength ){
            for(int i = 0; i < bcdLength-len;i++)
                asc = "0" + asc;
        }

        if (len > bcdLength){
            asc = asc.substring(len-bcdLength);
        }

        len = asc.length();

        byte abt[];
        if (len >= 2) {
            len = len / 2;
        }

        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length()/2; p++) {
            if ( (abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ( (abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ( (abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ( (abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            }else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * 十六进制转换字符串
     */
    public static String hexStr2Str(String hexStr) {

        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * byte——>String
     * @param src 字节数组
     * @return String
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String bytesToHexStringLog(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv).append("-");
        }
        return stringBuilder.toString();
    }

    public static String charsToHexString(char[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (char aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String buildId(long time) {
        String ret = "";
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int s = c.get(Calendar.SECOND);
        ret += String.format("%6s", Integer.toBinaryString(year - 2010));
        ret += String.format("%4s", Integer.toBinaryString(month));
        ret += String.format("%5s", Integer.toBinaryString(day));
        ret += String.format("%5s", Integer.toBinaryString(hour));
        ret += String.format("%6s", Integer.toBinaryString(min));
        ret += String.format("%6s", Integer.toBinaryString(s));
        ret = ret.replaceAll(" ", "0");
        int x = Integer.parseInt(ret, 2);
        return Integer.toHexString(x).toUpperCase();
    }

    /**
     * 字节数组转成int类型
     * @param b 字节数组
     * @param offset 数组下标偏移
     * @return int数值
     * @throws Exception
     */
    public static int byteArrayToInt(byte[] b, int offset) {
        int value= 0, len = 4;
        if (b.length < 4) {
            len = b.length;
        }
        for (int i = 0; i < len; i++) {
            int shift= (len - 1 - i) * 8;
            value +=(b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    public static int charArrayToInt(char[] b, int offset){
        int value= 0;
        for (int i = 0; i < 4; i++) {
            int shift= (4 - 1 - i) * 8;
            value +=(b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    /**
     * 将16位的short转换成byte数组
     *
     * @param s
     *            short
     * @return byte[] 长度为2
     * */
    public static byte[] shortToByteArray(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * 将byte[2]转换成short
     * @param b 字节数组
     * @return short
     */
    public static short byte2Short(byte[] b){
        if(b == null){
            return 0;
        }
        return (short) (((b[0] & 0xff) << 8) | (b[1] & 0xff));
    }

    public static String conver2HexStr(byte [] b)
    {
        StringBuilder result = new StringBuilder();
        for (byte aB : b) {
            result.append(Long.toString(aB & 0xff, 2)).append(",");
        }
        return result.toString();
    }

    /**
     * 字节数组转换成int数组
     * @param byteArray 字节数组
     * @return int类型数组
     */
    static int[] byteArrayToIntArray(byte[] byteArray){
        if(byteArray.length % 4 != 0)
            return new int[0];
        int[] intArray = new int[byteArray.length / 4];
        int j = 0;
        for (int i=0; i<byteArray.length; i+=4){
            intArray[j++] = (byteArray[i] << 24)  + (byteArray[i+1] << 16) + (byteArray[i+2] << 8) + byteArray[i+3];
        }
        return intArray;
    }

    /**
     * 整型数据转换成字节数组,高位在前
     * @param data 整型数据
     * @return 字节数组
     */
    public static byte[] intToByteArray(int data){
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((data >> 24) & 0xff);
        bytes[1] = (byte) ((data >> 16) & 0xff);
        bytes[2] = (byte) ((data >> 8) & 0xff);
        bytes[3] = (byte) (data & 0xff);
        return bytes;
    }

    /**
     * 整型数据转换成2个字节,高位在前
     * @param data 整型数据
     * @return 字节数组
     */
    public static byte[] intTo2Bytes(int data){
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((data >> 8) & 0xff);
        bytes[1] = (byte) (data & 0xff);
        return bytes;
    }

    public static void initToByteAndCopy(int value , byte[] datas , int offset ,int count){
        for(int i = 0 ; i < count ; i++){
            datas[offset + i] = (byte) ((value >> (8 * (count - i - 1))) & 0xff);
        }
    }

	public static void intTo2BytesAndCopy(int data , byte[] src , int offset){
		src[offset] = (byte) ((data >> 8) & 0xff);
		src[offset + 1] = (byte) (data & 0xff);
	}

	public static void intToByteArrayAndCopy(int data , byte[] src , int offset){
		src[offset] = (byte) ((data >> 24) & 0xff);
		src[offset + 1] = (byte) ((data >> 16) & 0xff);
		src[offset + 2] = (byte) ((data >> 8) & 0xff);
		src[offset + 3] = (byte) (data & 0xff);
	}
    /**
     * 十六进制编码转化为字符串
     * @param hexStr 十六进制字符串
     * @return String
     */
    public static String toStringHex(String hexStr) {
        byte[] baKeyword = new byte[hexStr.length()/2];
        for(int i = 0; i < baKeyword.length; i++)
        {
            try {
                baKeyword[i] = (byte)(0xff & Integer.parseInt(hexStr.substring(i*2, i*2+2),16));
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        try {
            hexStr = new String(baKeyword, "GBK");
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        return hexStr;
    }

	public static int findIndex(String hexStr){
		int index = 0 ;
		while(index < (hexStr.length()-1)){
			if("0".equals(hexStr.charAt(index)+"") && "0".equals(hexStr.charAt(index+1)+"")){
				System.out.println("index=" + index);
				return index;
			}else {
				index += 2;
			}
		}
		return index;
	}

    /**
     * 将byte[4]后两个字节转换成short
     * @param b 字节数组
     * @return short
     */
    public static short byteToShort(byte[] b){
        if(b == null){
            return 0;
        }

        return (short) (((b[2] & 0xff) << 8) | (b[3] & 0xff));
    }
    /**
     * ASCII字节数组转成字符串
     * @param ascii ascii数组
     * @return 字符串
     */
    public static String asciiToStr(byte[] ascii){
        String hex = bytesToHexString(ascii);
        if(StringUtil.isEmpty(hex)){
            return "";
        }
        return hexStr2Str(hex);
    }

    /**
     * 字符串转成ASCII字节数组
     * @param str 字符串
     * @return ascii数组
     */
    public static byte[] strToAscii(String str){
        if(StringUtil.isEmpty(str)){
            return null;
        }
        char[] charArray = str.toCharArray();
        byte[] asciiArray = new byte[charArray.length];
        int i=0;
        for(char c:charArray){
            asciiArray[i++] = (byte)c;
        }
        return asciiArray;
    }

    public static String ascii2String(byte[] ASCIIs) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ASCIIs.length; i++) {
            sb.append((char) ascii2Char(ASCIIs[i]));
        }
        return sb.toString();
    }

    private static char ascii2Char(int ASCII) {
        return (char) ASCII;
    }

    public static void intTo1BytesAndCopy(int data , byte[] toWhere , int offset){
        toWhere[offset] = (byte) (data & 0xff);
    }

    public static int byteToInt(byte[] data , int offset , int count){
        int value= 0;
        for (int i = 0; i < count; i++) {
            int shift= (count - 1 - i) * 8;
            value |=(data[i + offset] & 0xff) << shift;
        }
        return value;
    }

    private static ByteBuffer buffer = ByteBuffer.allocate(8);
    //byte 数组与 long 的相互转换
    public static byte[] longToBytes(long x) {
        buffer.clear();
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.clear();
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }


	/**
	 * 时间归一化
	 * @param time 注意入参格式为YYYYMMDDhhmmss
	 * @return 归一化为32位int型表示
	 */
	public static int timeNormalize(String time){
        if (time.equalsIgnoreCase("0")) {
            return 0;
        }
        //HikLog.infoLog("qjy", "timeNormalize");
		int year = Integer.valueOf(time.substring(0, 4));//6bit
		year -= 2010;//0-63表示2010-2073
		int month = Integer.valueOf(time.substring(4, 6));//4bit
		int day = Integer.valueOf(time.substring(6, 8));//5bit
		int hour = Integer.valueOf(time.substring(8, 10));//5bit
		int minute = Integer.valueOf(time.substring(10, 12));//6bit
		int second = Integer.valueOf(time.substring(12, 14));//6bit

		return (year<<26) | (month<<22) |  (day<<17) | (hour<<12) |  minute<<6 | second;
	}

    /**
     * 反归一化时间，输出时间格式为YYYYMMDDhhmmss
     * @param timeNormalize 归一化过的时间int32
     * @return 反归一化时间
     */
    public static String reverseTimeNormalize(int timeNormalize) {
        String year = String.valueOf((timeNormalize >> 26) + 2010);
        String month = String.valueOf((timeNormalize >> 22) & 0x0F);
        String date = String.valueOf((timeNormalize >> 17) & 0x1F);
        String hour = String.valueOf((timeNormalize >> 12) & 0x1F);
        String minute = String.valueOf((timeNormalize >> 6) & 0x3F);
        String second = String.valueOf(timeNormalize & 0x3F);
        return year+(month.length()==1?("0"+month):month)+(date.length()==1?("0"+date):date)
                +(hour.length()==1?("0"+hour):hour)+(minute.length()==1?("0"+minute):minute)+(second.length()==1?("0"+second):second);
    }


    public static void main(String[] arg){
        int time = timeNormalize("20180126064650");
        System.out.println(time);
        String timeS = reverseTimeNormalize(time);
        System.out.println(timeS);
    }

	/**
	 * 时间拆分为数组
	 * @param normalizeTime 归一化时间
	 * @return 拆分为年月日时分秒数组
	 */
	private static int[] timeSplit(int normalizeTime){
		String time = Integer.toBinaryString(normalizeTime);
		int length = time.length();
		String year = time.substring(0,length-26);
		String month = time.substring(length-26, length-22);
		String day = time.substring(length-22, length-17);
		String hour = time.substring(length-17, length-12);
		String minute = time.substring(length-12, length-6);
		String second = time.substring(length-6);
		int[] mTime = new int[6];
		mTime[0] = binaryToDecimal(year)+2010;
		mTime[1] = binaryToDecimal(month);
		mTime[2] = binaryToDecimal(day);
		mTime[3] = binaryToDecimal(hour);
		mTime[4] = binaryToDecimal(minute);
		mTime[5] = binaryToDecimal(second);
		return mTime;
	}

	/**
	 * 二进制字符串转十进制
	 * @param binary 二进制字符串
	 * @return 十进制数值
	 */
	public static int binaryToDecimal(String binary) {
		int max = binary.length();
		int result = 0;
		for (int i = max; i > 0; i--) {
			char c = binary.charAt(i - 1);
			int decimal = c - '0';
			result += Math.pow(2, max - i) * decimal;
		}
		return result;
	}

    public static byte[] doubleToBytes(double d) {
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }

    public static void doubleToBytesAndCopy(double d , byte[] src , int offset , int count) {
        long value = Double.doubleToRawLongBits(d);
        for (int i = 0; i < count; i++) {
            src[offset + i] = (byte) ((value >> 8 * i) & 0xff);
        }
    }

    /**
     * 将字符串转给固定长度len的字节数组
     * @param originStr 输入字串(ASCii编码)
     * @param len 输出字节数组长度
     * @return
     */
    public static byte[] getFixedLenBytes(String originStr, int len) {
        if (len <= 0) {
            return null;
        }
        byte[] certNumBytes = new byte[len];
        int length;
        if (StringUtil.isEmpty(originStr)) {
            length = 0;
        }else if (originStr.length() < len) {
            length = originStr.length();
        }else {
            length = len;
        }
        if (length > 0) {
            byte[] asciiBytes = asciiToBytes(originStr);
            System.arraycopy(asciiBytes, 0, certNumBytes, 0, length);
        }
        for (int i = length; i < len; i++) {
            certNumBytes[i] = 0x00;
        }
        return certNumBytes;
    }

    public static byte[] getFixedLenBytes(byte[] originBytes, int len) {
        if (len <= 0) {
            return null;
        }
        byte[] outputBytes = new byte[len];
        int length;
        if (originBytes == null) {
            length = 0;
        }else if (originBytes.length < len) {
            length = originBytes.length;
        }else {
            length = len;
        }
        if (length > 0) {
            System.arraycopy(originBytes, 0, outputBytes, 0, length);
        }
        for (int i = length; i < len; i++) {
            outputBytes[i] = 0x00;
        }
        return outputBytes;
    }

    public static byte[] asciiToBytes(String value)
    {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        char[] chars = value.toCharArray();
        byte[] ret = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            ret[i] = (byte) chars[i];
        }
        return ret;
    }

    public static byte[] concat(byte[] a, byte[] b) {
        if (a == null || b == null) {
            return null;
        }
        byte[] val = new byte[a.length + b.length];
        System.arraycopy(a, 0, val, 0, a.length);
        System.arraycopy(b, 0, val, a.length, b.length);
        return val;
    }

    public static int CalCrc (byte[] data , int count)
    {
        int crc = 0;
        for (int i = 0; i < count; i++) {
            crc = crc ^ ((data[i] & 0xFF) << 8);
            for (int j = 0; j < 8; j++) {
                if(((crc & 0xFFFF) & 0x8000) != 0) {
                    crc = crc<<1^0x1021;
                }else {
                    crc = crc<<1;
                }
            }
        }
        return(crc & 0xFFFF);
    }

    public static int CalCrc (int crc,byte[] data , int count)
    {
        for (int i = 0; i < count; i++) {
            crc = crc ^ ((data[i] & 0xFF) << 8);
            for (int j = 0; j < 8; j++) {
                if(((crc & 0xFFFF) & 0x8000) != 0) {
                    crc = crc<<1^0x1021;
                }else {
                    crc = crc<<1;
                }
            }
        }
        return(crc & 0xFFFF);
    }

    public static long dateToLong(String strTime, String formatType){
        if (StringUtil.isEmpty(strTime)) {
            return -1;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date;
        try {
            date = formatter.parse(strTime);
        }catch (ParseException e) {
            return -1;
        }
        return date.getTime();
    }


    public static byte[] fillAfter(byte[] src, int length){
        byte[] after = new byte[length];
        System.arraycopy(src,0,after,0,src==null?0:src.length);
        return after;
    }

}
