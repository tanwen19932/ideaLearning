package com.services.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.services.service.impl.NewsInsertServiceImpl;
import edu.buaa.nlp.socialHttp.SocialDao;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.services.service.impl.PlatFormServiceImpl;

@Controller
@RequestMapping("/webservice")
public class YqAppServiceController {
    private String stipulatecode = "dfa95dbe9d657116c5613d6b6c05abcd";// 规定值
    protected static Logger log = LoggerFactory.getLogger(YqAppServiceController.class);
    @Resource(name = "platFormService")
    public PlatFormServiceImpl platFormService;

    private static void writeLog(long s, long e, String method, String param) {
        log.info("_" + method + ",耗时" + (e - s) + "毫秒\n参数：" + param.substring(0, param.length() > 100 ? 100 : param.length()));
    }

    @RequestMapping(value = "/insertNewsTest/{key}", method = RequestMethod.POST)
    public void insertNewsTest(@PathVariable String key, HttpServletRequest request, HttpServletResponse response) {
        JSONObject object = getJsonResult(key,request, new IResulter() {
            @Override
            public String handle(String params) {
                return NewsInsertServiceImpl.getInstance().insertTest(params);
            }
        });
        doWriteDate(object, request, response);
    }

    @RequestMapping(value = "/insertNewsForPost/{key}", method = RequestMethod.POST)
    public void insertNewsForPost(@PathVariable String key, HttpServletRequest request, HttpServletResponse response) {
        JSONObject object = getJsonResult(key, request, new IResulter() {
            @Override
            public String handle(String params) {
                return NewsInsertServiceImpl.getInstance().insert(params);
            }
        });
        doWriteDate(object, request, response);
    }



    @RequestMapping(value = "/insertSocialForPost/{key}", method = RequestMethod.POST)
    public void insertSocialForPost(@PathVariable String key, HttpServletRequest request, HttpServletResponse response) {
        // false : true;
        JSONObject object = getJsonResult(key ,request, new IResulter() {
            @Override
            public String handle(String params) {
                return String.valueOf(SocialDao.Insert(params));
            }
        });
        doWriteDate(object, request, response);
    }

    private JSONObject getJsonResult( String key,HttpServletRequest request , IResulter resulter){
        JSONObject object = null;
        if (!PassAuthentication(key)) {
            object = new JSONObject("{'returncode':'9','returnmsg':'没有权限'}");
        } else {
            long s = System.currentTimeMillis();
            String param = "";
            try {
                BufferedReader bf = new BufferedReader(new InputStreamReader(request.getInputStream()));
                String line;
                while ((line = bf.readLine()) != null) {
                    param += line;
                }
                String result = resulter.handle(param);
                log.info(result);
                param = URLDecoder.decode(param, "UTF-8");
                object = new JSONObject();
                object.put("returncode", "1");
                object.put("returnmsg", "正常");
                object.put("returndata", new JSONObject(result));
//				System.out.println("输出结果：" + obj.toString());
            } catch (Exception e) {
                e.printStackTrace();
                object = new JSONObject();
                object.put("returncode", "0");
                object.put("returnmsg", "失败：" + e.getMessage());
                object.put("returndata", new String[]{});
            }
            long e = System.currentTimeMillis();
            writeLog(s, e, "insertNewsForPost", param);
        }
        return object;
    }

    public void doWriteDate(JSONObject object, HttpServletRequest request, HttpServletResponse response) {
        try {
            String jsoncallback = request.getParameter("callback");// 跨域请求，回调方法。
            boolean callback = request.getParameter("callback") == null ? false// 是否跨域请求.
                    : true;
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json; charset=utf-8");

            PrintWriter out = response.getWriter();
            // 如果是跨域情况，则要加该回调方法，解决跨域无法获取数据的问题。
            if (callback) {
                out.print("(" + jsoncallback + "(" + object.toString() + "))");// 解决跨域请求无法获取数据的问题。
            } else {
                out.print(object.toString());
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * @Title: PassAuthentication @Description: 认证 @param @param
     * authenticationcode @param @return 设定文件 @return boolean 返回类型 @throws
     */
    public boolean PassAuthentication(String authenticationcode) {
        if (!tw.utils.StringUtil.isNull(authenticationcode) && authenticationcode.equals(stipulatecode)) {
            return true;
        } else {
            return false;
        }

    }
}
