package com.test.sobot.sobot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ValueStatic {
    private static final Logger logger = LoggerFactory.getLogger(ValueStatic.class);


    //读取Excel路径
    static String redfilePath2;
    //机器人信息
    static String sysNum;
    //初始化用户信息url
    static String initUrl;
    //问答接口
    static String robotsendUrl;



    @Value("${myConfig.redfilePath2}")
    public void setRedfilePath2(String filePath2) {
        logger.info("ValueStatic filePath2:" + filePath2);
        redfilePath2 = filePath2;
    }

    @Value("${myConfig.sysNum}")
    public void setSysNum(String Num) {
        logger.info("ValueStatic Num:" + Num);
        sysNum = Num;
    }

    @Value("${myConfig.initUrl}")
    public void setInitUrl(String initurl) {
        logger.info("ValueStatic initurl:" + initurl);
        initUrl = initurl;
    }

    @Value("${myConfig.robotsendUrl}")
    public void setRobotsendUrl(String sendUrl) {
        logger.info("ValueStatic sendUrl:" + sendUrl);
        robotsendUrl = sendUrl;
    }


}