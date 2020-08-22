package com.hikvision.auto.router.base.protocol;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 协议的解析工作
 * 不确定的点？
 * 协议的组包工作是否放在业务层module（如果放在这边，还要封装一层数据格式）
 */
public interface IProtocolProcessor {

    @IntDef({
            PROTOCOL.PRO_BASE, PROTOCOL.QING_DAO
    })
    @Retention(RetentionPolicy.CLASS)
    public @interface PROTOCOL {
        int PRO_BASE = 0;
        int QING_DAO = 1;
    }

    @IntDef({
            PROTOCOL_MSG_ID.POSITION_QUERY,
            PROTOCOL_MSG_ID.POSITION_QUERY_RESPONSE,
            PROTOCOL_MSG_ID.POSITION_TRACK_CONTRIL,
            PROTOCOL_MSG_ID.POSITION_TRACK_RESPONSE,
            PROTOCOL_MSG_ID.STORED_IMAGE_RETRIEVAL,
            PROTOCOL_MSG_ID.STORED_IMAGE_RETRIEVAL_RESPONSE
    })
    @Retention(RetentionPolicy.CLASS)
    public @interface PROTOCOL_MSG_ID {
        int POSITION_QUERY = 0x8201; //位置信息查询
        int POSITION_QUERY_RESPONSE = 0x0201; //位置信息查询应答
        int POSITION_TRACK_CONTRIL = 0x8202; //位置跟踪控制
        int POSITION_TRACK_RESPONSE = 0x0202; //位置跟踪信息汇报
        int STORED_IMAGE_RETRIEVAL = 0x8802; //存储图像检索
        int STORED_IMAGE_RETRIEVAL_RESPONSE = 0x0802; //存储图像检索应答
    }

    /**
     * 首先选择地级市协议
     * @param protocol
     */
    void attachParser(@PROTOCOL int protocol);


    /**
     * 当前解析器是哪套协议
     * @return
     */
    int protocolIs();


    /**
     * 协议解析
     * @param serial
     * @param msgId
     * @param body
     */
    void protocolParse(int serial, @PROTOCOL_MSG_ID int msgId, byte[] body);


    /**
     * 协议组包
     * @param msgId
     * @param json
     */
    void packagePack(@PROTOCOL_MSG_ID int msgId, String json, Class<?> clz);

}
