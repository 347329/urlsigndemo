package com.example.urlsign.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.urlsign.demo.utils.UrlSignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("")
public class SignController {
    private static Logger LOGGER= LoggerFactory.getLogger(SignController.class);

    @Value("${urlsign.key}")
    private String urlSignKey ;

    @Value("${urlsign.testurl}")
    private String testurl ;

    @ResponseBody
    @RequestMapping("generatesignurl")
    public Object getSignUrl() throws UnsupportedEncodingException {
        Map<String, String> params =new HashMap<>();

        params.put("platformNo", "1");
        params.put("timestamp",String.valueOf(new Date().getTime()));
        params.put("orderNo", UUID.randomUUID().toString());
        params.put("openId","1000011");
        //流水号
        params.put("transId","100010103836");
        String urlParameter=UrlSignUtils.getUrlStr(params,urlSignKey);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("success",true);
        jsonObject.put("url",testurl+"?"+ java.net.URLEncoder.encode(urlParameter,"utf-8"));
        return jsonObject;
    }

    @ResponseBody
    @RequestMapping("checkSign")
    public Object testurl(HttpServletRequest request) throws UnsupportedEncodingException {
        String queryString=request.getQueryString();
        LOGGER.info("queryString:{}",queryString);
        String urldecode= URLDecoder.decode(queryString,"utf-8");
        String[] parameters=urldecode.split("&");
        Map<String,String> queryMap=new HashMap<String,String>();
        for (int i=0;i<parameters.length;i++){
            String[] parameter=parameters[i].split("=");
            queryMap.put(parameter[0],parameter[1]);
        }

        String sign=queryMap.get("sign");
        JSONObject jsonObject=new JSONObject();
        String signature=UrlSignUtils.uniSign(queryMap, urlSignKey);

        if ((signature+"").equals(sign)){
            jsonObject.put("success",true);
            jsonObject.put("msg","校验成功");
        }else {
            jsonObject.put("success",false);
            jsonObject.put("msg","校验失败");
        }
        return  jsonObject;
    }

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    public String getUrlSignKey() {
        return urlSignKey;
    }

    public void setUrlSignKey(String urlSignKey) {
        this.urlSignKey = urlSignKey;
    }
}
