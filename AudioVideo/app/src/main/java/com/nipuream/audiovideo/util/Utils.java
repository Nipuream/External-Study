package com.nipuream.audiovideo.util;

import android.hardware.Camera;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Utils {

    private static final String TAG = "Utils";

    public static boolean isSupportPreviewSize(List<Camera.Size> supportSize, int previewWidth, int previewHeight){

        for(Camera.Size size : supportSize){
            if(size.width == previewWidth && size.height == previewHeight){

                return true;
            }
        }
        return false;
    }


    /**
     * 重置采样率
     */
    public static void Resample(int sampleRate, String decodeFileUrl) {
        String newDecodeFileUrl = decodeFileUrl + "new";

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(decodeFileUrl));
            FileOutputStream fileOutputStream = new FileOutputStream(new File(newDecodeFileUrl));

            new SSRC(fileInputStream, fileOutputStream, sampleRate, Constants.ExportSampleRate,
                    Constants.ExportByteNumber, Constants.ExportByteNumber, 1, Integer.MAX_VALUE, 0, 0, true);

            fileInputStream.close();
            fileOutputStream.close();

            FileFunction.renameFile(newDecodeFileUrl, decodeFileUrl);
        } catch (IOException e) {
            Log.e(TAG, "关闭bufferedOutputStream异常" + e);
        }
    }

    /**
     * 重置采样点字节数
     */
    public static byte[] convertByteNumber(int sourceByteNumber, int outputByteNumber,
                                           byte[] sourceByteArray) {
        if (sourceByteNumber == outputByteNumber) {
            return sourceByteArray;
        }

        int sourceByteArrayLength = sourceByteArray.length;

        byte[] byteArray;

        switch (sourceByteNumber) {
            case 1:
                switch (outputByteNumber) {
                    case 2:
                        byteArray = new byte[sourceByteArrayLength * 2];

                        byte resultByte[];

                        for (int index = 0; index < sourceByteArrayLength; index += 1) {
                            resultByte = CommonFunction.GetBytes((short) (sourceByteArray[index] * 256),
                                    Constants.isBigEnding);

                            byteArray[2 * index] = resultByte[0];
                            byteArray[2 * index + 1] = resultByte[1];
                        }

                        return byteArray;
                    default:
                        break;
                }
                break;
            case 2:
                switch (outputByteNumber) {
                    case 1:
                        int outputByteArrayLength = sourceByteArrayLength / 2;

                        byteArray = new byte[outputByteArrayLength];

                        for (int index = 0; index < outputByteArrayLength; index += 1) {
                            byteArray[index] = (byte) (CommonFunction.GetShort(sourceByteArray[2 * index],
                                    sourceByteArray[2 * index + 1], Constants.isBigEnding) / 256);
                        }

                        return byteArray;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        return sourceByteArray;
    }

    /**
     * 重置声道数
     */
    public static byte[] convertChannelNumber(int sourceChannelCount, int outputChannelCount,
                                              int byteNumber, byte[] sourceByteArray) {
        if (sourceChannelCount == outputChannelCount) {
            return sourceByteArray;
        }

        switch (byteNumber) {
            case 1:
            case 2:
                break;
            default:
                return sourceByteArray;
        }

        int sourceByteArrayLength = sourceByteArray.length;

        byte[] byteArray;

        switch (sourceChannelCount) {
            case 1:
                switch (outputChannelCount) {
                    case 2:
                        byteArray = new byte[sourceByteArrayLength * 2];

                        byte firstByte;
                        byte secondByte;

                        switch (byteNumber) {
                            case 1:
                                for (int index = 0; index < sourceByteArrayLength; index += 1) {
                                    firstByte = sourceByteArray[index];

                                    byteArray[2 * index] = firstByte;
                                    byteArray[2 * index + 1] = firstByte;
                                }
                                break;
                            case 2:
                                for (int index = 0; index < sourceByteArrayLength; index += 2) {
                                    firstByte = sourceByteArray[index];
                                    secondByte = sourceByteArray[index + 1];

                                    byteArray[2 * index] = firstByte;
                                    byteArray[2 * index + 1] = secondByte;
                                    byteArray[2 * index + 2] = firstByte;
                                    byteArray[2 * index + 3] = secondByte;
                                }
                                break;
                            default:
                                break;
                        }

                        return byteArray;
                    default:
                        break;
                }
                break;
            case 2:
                switch (outputChannelCount) {
                    case 1:
                        int outputByteArrayLength = sourceByteArrayLength / 2;

                        byteArray = new byte[outputByteArrayLength];

                        switch (byteNumber) {
                            case 1:
                                for (int index = 0; index < outputByteArrayLength; index += 2) {
                                    short averageNumber =
                                            (short) ((short) sourceByteArray[2 * index] + (short) sourceByteArray[2
                                                    * index + 1]);
                                    byteArray[index] = (byte) (averageNumber >> 1);
                                }
                                break;
                            case 2:
                                for (int index = 0; index < outputByteArrayLength; index += 2) {
                                    byte resultByte[] =
                                            CommonFunction.AverageShortByteArray(sourceByteArray[2 * index],
                                                    sourceByteArray[2 * index + 1], sourceByteArray[2 * index + 2],
                                                    sourceByteArray[2 * index + 3], Constants.isBigEnding);

                                    byteArray[index] = resultByte[0];
                                    byteArray[index + 1] = resultByte[1];
                                }
                                break;
                            default:
                                break;
                        }

                        return byteArray;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        return sourceByteArray;
    }


}
