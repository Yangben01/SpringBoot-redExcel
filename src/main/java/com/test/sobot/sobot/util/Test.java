package com.test.sobot.sobot.util;


import com.alibaba.fastjson.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Component
public class Test implements CommandLineRunner {

    private static       String question;



    @Override
    public void run(String... args) throws Exception {

        File dir = null;
        try {
            dir = new File(ValueStatic.redfilePath2);
            ExcelRed excelHelper = new ExcelRed();
            //dir文件，0代表是第一行为保存到数据库或者实体类的表头，一般为英文的字符串，2代表是第二种模板，
            JSONArray jsonArray = excelHelper.readExcle(dir, 1, 2);
            JSONObject jobs =null;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject job = jsonArray.getJSONObject(i);
                question = job.getString("question");
                if (question != null && !question.equals("")) {
                    Map param = new HashMap<String, Object>();
                    param.put("ack", "1");
                    param.put("sysNum", ValueStatic.sysNum);
                    param.put("source", "0");
                    param.put("tranFlag", "0");
                    param.put("isReComment", "1");
                    String user = HttpUtilNew.sendGet(ValueStatic.initUrl, param);
                    if (user != null && !user.equals("")) {
                        Map maps = new HashMap<String, Object>();
                        com.alibaba.fastjson.JSONObject object = JSON.parseObject(user);
                        maps.put("requestText", question);
                        maps.put("question", question);
                        maps.put("sysNum", ValueStatic.sysNum);
                        maps.put("uid", object.get("uid"));
                        maps.put("cid", object.get("cid"));
                        String answer = HttpUtilNew.sendGet(ValueStatic.robotsendUrl, maps);

                        System.out.println("答案" + answer);
                        com.alibaba.fastjson.JSONObject jsons = JSON.parseObject(answer);
                        Object stripe = jsons.get("stripe");
                        if (stripe != null && !stripe.equals("")) {
                            String sugguestions=jsons.getString("sugguestions");
                            System.out.println("sugguestions=="+sugguestions);
                            JSONArray json = JSONArray.fromObject(sugguestions);
                            if(json.size()>0) {
                                for (int l = 0; l < json.size(); l++) {
                                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                                    jobs = json.getJSONObject(l);

                                }
                                String questions=jobs.get("question").toString();
                                String docId=jobs.get("docId").toString();
                                System.out.println("智能学习问题:"+questions);
                                System.out.println("词条Id:"+docId);
                                if (questions != null && !questions.equals("")) {
                                    maps.put("requestText", docId);
                                    maps.put("question", questions);
                                    maps.put("sysNum", ValueStatic.sysNum);
                                    maps.put("uid", object.get("uid"));
                                    maps.put("cid", object.get("cid"));
                                    maps.put("questionFlag", "1");
                                    String answers = HttpUtilNew.sendGet(ValueStatic.robotsendUrl, maps);
                                    System.out.println("智能学习答案"+answers);
                                }

                            }

                        }


                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
