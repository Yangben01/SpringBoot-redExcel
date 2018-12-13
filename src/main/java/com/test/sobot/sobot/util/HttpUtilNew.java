package com.test.sobot.sobot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

/**
 * http请求 post get方法
 * 
 */
public class HttpUtilNew {
  private static Logger logger = LoggerFactory.getLogger(HttpUtilNew.class);

  /**
   * POST请求，Map形式数据
   * @param url 请求地址
   * @param param 请求数据
   */
  public static String sendPost(String url, Map<String, Object> param) {
    return sendPost(url, param, "");
  }

  public static String sendPost(String url, Map<String, Object> param, String token) {
    Long start = System.currentTimeMillis();
    logger.info("sendPost_start: " + url + ",param:" + param);

    StringBuffer buffer = new StringBuffer();
    if (param != null && !param.isEmpty()) {
      for (Map.Entry<String, Object> entry : param.entrySet()) {
        try {
          if (entry.getValue() != null) {
            buffer.append(entry.getKey()).append("=")
              .append(URLEncoder.encode(entry.getValue().toString(), "utf-8")).append("&");
          }
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }

      }
      buffer.deleteCharAt(buffer.length() - 1);
    }

    PrintWriter out = null;
    BufferedReader in = null;
    String result = "";

    try {

      URL realUrl = new URL(url);
      // 打开和URL之间的连接
      URLConnection conn = realUrl.openConnection();
      // 设置通用的请求属性
      conn.setRequestProperty("accept", "*/*");
      conn.setRequestProperty("connection", "Keep-Alive");
      conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
      if (!"".equals(token)) {
        conn.setRequestProperty("temp-id", token);
      }
      conn.setConnectTimeout(30000);
      conn.setReadTimeout(30000);
      // 发送POST请求必须设置如下两行
      conn.setDoOutput(true);
      conn.setDoInput(true);
      // 获取URLConnection对象对应的输出流
      out = new PrintWriter(conn.getOutputStream());
      // 发送请求参数
      out.print(buffer);
      // flush输出流的缓冲
      out.flush();
      // 定义BufferedReader输入流来读取URL的响应
      in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
      String line;
      while ((line = in.readLine()) != null) {
        result += line;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    // 使用finally块来关闭输出流、输入流
    finally {
      logger.info("sendPost_end:" + url + ",param:" + param + ",result:" + result + ",t:"
        + (System.currentTimeMillis() - start) + ":ms");
      try {
        if (out != null) {
          out.close();
        }
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
        logger.error("sendPost_error:" + url + ",param:" + param + ",result:" + result + ",t:"
          + (System.currentTimeMillis() - start) + ":ms");
      }

    }
    return result;
  }

  /**
   * 
   */
  public static String sendGet(String url, Map<String, String> parameters) {
    String result = "";
    BufferedReader in = null;// 读取响应输入流
    StringBuffer sb = new StringBuffer();// 存储参数
    String params = "";// 编码之后的参数
    try {
      // 编码请求参数
      if (parameters.size() == 1) {
        for (String name : parameters.keySet()) {
          sb.append(name).append("=").append(URLEncoder.encode(parameters.get(name), "UTF-8"));
        }
        params = sb.toString();
      } else {
        for (String name : parameters.keySet()) {
          sb.append(name).append("=").append(URLEncoder.encode(parameters.get(name), "UTF-8"))
            .append("&");
        }
        String temp_params = sb.toString();
        params = temp_params.substring(0, temp_params.length() - 1);
      }
      String full_url = url + "?" + params;
      System.out.println(full_url);
      // 创建URL对象
      URL connURL = new URL(full_url);
      // 打开URL连接(建立了一个与服务器的tcp连接,并没有实际发送http请求！)
      URLConnection urlConnection = connURL.openConnection();
      urlConnection.setConnectTimeout(5000);
      java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) urlConnection;
      // 设置通用请求属性(如果已存在具有该关键字的属性，则用新值改写其值。)
      httpConn.setRequestProperty("Accept", "*/*");
      httpConn.setRequestProperty("Connection", "Keep-Alive");
      httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
      // 建立实际的连接(远程对象变为可用。远程对象的头字段和内容变为可访问)
      httpConn.connect();

      // 定义BufferedReader输入流来读取URL的响应,并设置编码方式
      in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
      String line;
      // 读取返回的内容
      while ((line = in.readLine()) != null) {
        result += line;
      }
    } catch (Exception e) {
      System.out.println("发送GET请求异常----" + e);
      e.printStackTrace();
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        System.out.println("发送GET请求异常----" + ex);
        ex.printStackTrace();
      }
    }
    return result;
  }


}
