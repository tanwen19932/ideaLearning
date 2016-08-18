//package com.services.controller;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.URLDecoder;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import com.services.service.impl.PlatFormServiceImpl;
//
//@Controller
//@RequestMapping("/webservice")
//public class YqAppServiceControllerBAK {
//    private String stipulatecode = "dfa95dbe9d657116c5613d6b6c05abcd";// 规定值
//    protected static Logger log = LoggerFactory.getLogger(YqAppServiceControllerBAK.class);
//    @Resource(name = "platFormService")
//    public PlatFormServiceImpl platFormService;
//
//    private static void writeLog(long s, long e, String method, String param) {
//        log.info("_" + method + ",耗时" + (e - s) + "毫秒\n参数：" + param.substring(0, param.length() > 100 ? 100 : param.length()));
//    }
//
//    @RequestMapping(value = "/insertNewsForPost/{key}", method = RequestMethod.POST)
//    public void insertNewsForPost(@PathVariable String key, HttpServletRequest request, HttpServletResponse response) {
//        // String jsoncallback = request.getParameter("jsoncallback");
//        // boolean callback = request.getParameter("jsoncallback") == null ?
//        // false : true;
//        JSONObject object = null;
//        if (!PassAuthentication(key)) {
//            object = new JSONObject("{'returncode':'9','returnmsg':'没有权限'}");
//        } else {
//            long s = System.currentTimeMillis();
//            String param = "";
//            try {
//                BufferedReader bf = new BufferedReader(new InputStreamReader(request.getInputStream()));
//                String line;
//                while ((line = bf.readLine()) != null) {
//                    param += line;
//                }
//                param = URLDecoder.decode(param, "UTF-8");
//                object = new JSONObject();
//                JSONObject obj = platFormService.insertNewsForPost(param);
//                // String obj =
//                // NewsInsertServiceImpl.getInstance().insert(param);
//                object.put("returncode", "1");
//                object.put("returnmsg", "正常");
//                object.put("returndata", obj);
//                System.out.println("输出结果：" + obj.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//                object = new JSONObject();
//                object.put("returncode", "0");
//                object.put("returnmsg", "失败：" + e.getMessage());
//                object.put("returndata", new String[]{});
//            }
//            long e = System.currentTimeMillis();
//            writeLog(s, e, "insertNewsForPost", param);
//        }
//        doWriteDate(object, request, response);
//    }
//
//    @RequestMapping(value = "/insertSocialForPost/{key}", method = RequestMethod.POST)
//    public void insertSocialForPost(@PathVariable String key, HttpServletRequest request, HttpServletResponse response) {
//        // String jsoncallback = request.getParameter("jsoncallback");
//        // boolean callback = request.getParameter("jsoncallback") == null ?
//        // false : true;
//        JSONObject object = null;
//        if (!PassAuthentication(key)) {
//            object = new JSONObject("{'returncode':'9','returnmsg':'没有权限'}");
//        } else {
//            long s = System.currentTimeMillis();
//            String param = "";
//            try {
//                BufferedReader bf = new BufferedReader(new InputStreamReader(request.getInputStream()));
//                String line;
//                while ((line = bf.readLine()) != null) {
//                    param += line;
//                }
//                param = URLDecoder.decode(param, "UTF-8");
//                object = new JSONObject();
//                int result = 1;
////				int result = Social.SocialDao.Insert(param);
//                object.put("returncode", "1");
//                object.put("returnmsg", "正常");
//                object.put("returndata", result);
////				System.out.println("输出结果：" + obj.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//                object = new JSONObject();
//                object.put("returncode", "0");
//                object.put("returnmsg", "失败：" + e.getMessage());
//                object.put("returndata", new String[]{});
//            }
//            long e = System.currentTimeMillis();
//            writeLog(s, e, "insertNewsForPost", param);
//        }
//        doWriteDate(object, request, response);
//    }
//
//    /**
//     * 返回json公共方法
//     *
//     * @param @param jsoncallback 回调方法
//     * @param @param callback 是否回调
//     * @param @param object 返回对象
//     * @param @param response 设定文件
//     * @return void 返回类型
//     * @Title: doWriteDate
//     * @Description: 返回json公共方法
//     */
//
//    public void doWriteDate(JSONObject object, HttpServletRequest request, HttpServletResponse response) {
//        try {
//            String jsoncallback = request.getParameter("callback");// 跨域请求，回调方法。
//            boolean callback = request.getParameter("callback") == null ? false// 是否跨域请求.
//                    : true;
//            response.setCharacterEncoding("utf-8");
//            response.setContentType("text/json; charset=utf-8");
//
//            PrintWriter out = response.getWriter();
//            // 如果是跨域情况，则要加该回调方法，解决跨域无法获取数据的问题。
//            if (callback) {
//                out.print("(" + jsoncallback + "(" + object.toString() + "))");// 解决跨域请求无法获取数据的问题。
//            } else {
//                out.print(object.toString());
//            }
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * @Title: PassAuthentication @Description: 认证 @param @param
//     * authenticationcode @param @return 设定文件 @return boolean 返回类型 @throws
//     */
//    public boolean PassAuthentication(String authenticationcode) {
//        if (!tw.utils.StringUtil.isNull(authenticationcode) && authenticationcode.equals(stipulatecode)) {
//            return true;
//        } else {
//            return false;
//        }
//
//    }
//}
