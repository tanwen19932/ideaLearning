package com.services.service.impl;

import com.services.service.InserterService;
import news.*;
import news.mediaSrc.util.MediaSrcUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import tw.utils.ReflectUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static tw.utils.StringUtil.isAllNull;
import static tw.utils.StringUtil.isOneNull;

//@WebService(endpointInterface = "news.api.hbase.InserterService")
//@SOAPBinding(style = SOAPBinding.Style.RPC)
// @WebService
public class NewsInsertServiceImpl implements InserterService {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(NewsInsertServiceImpl.class);
    private static AtomicInteger todayCountRequest = new AtomicInteger();
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(30);
    private static final String ERROR_STR = "{ \"Exception\" :\"EXCEPTION\"}";
    static NewsDao newsDao = new NewsDao();
    static NewsDaoTest newsDaoTest = new NewsDaoTest();
    private static NewsInsertServiceImpl instance = null;


    private NewsInsertServiceImpl() {
    }

    public static NewsInsertServiceImpl getInstance() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    private static synchronized void init() {
        if (instance == null) {
            instance = new NewsInsertServiceImpl();
        }
    }


    public String insert(String newsJsonStr) {
        return insert(newsJsonStr, false);
    }

    public String insertTest(String newsJsonStr) {
        return insert(newsJsonStr, true);
    }

    public String insert(String newsJsonStr, boolean isTest) {
        Future<String> result = threadPool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                JSONObject result = new JSONObject();
                int count = todayCountRequest.incrementAndGet();
                LOG.info(" 处理请求 " + count);
                Map<ERROR, Integer> countMap = new HashMap<>();
                try {
                    JSONArray newsArray = new JSONArray(newsJsonStr);
                    for (Object jo : newsArray) {
                        JSONObject newsJO = (JSONObject) jo;
                        add(countMap, insert(newsJO, isTest));
                    }
                } catch (JSONException e) {
                    try {
                        JSONObject newsJO = new JSONObject(newsJsonStr);
                        add(countMap, insert(newsJO, isTest));
                    } catch (Exception e2) {
                        LOG.debug(e2.getMessage());
                        add(countMap, ERROR.WrongFormat);
                    }
                }
                for (Entry<ERROR, Integer> entry : countMap.entrySet()) {
                    result.put(entry.getKey().name(), entry.getValue());
                }
                LOG.info(" 结束请求 " + count);

                return result.toString();
            }
        });
        try {
            return result.get();
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR_STR.replaceAll("EXCEPTION", e.getClass().getName());
        }
    }

    private ERROR insert(JSONObject newsJO, boolean isTest) {
        News news = new News();
        String isTestStr = "是否测试 " + isTest;
        Field[] fields = news.getClass().getDeclaredFields();
        for (Field field : fields) {
            String value = field.getName();
            if (newsJO.has(value)) {
                ReflectUtil.invokeObjSetMethod(news, field, newsJO.get(value));
            }
        }
        if (isOneNull(news.getTitleSrc(), news.getPubdate(), news.getTextSrc(), news.getComeFrom(), news.getUrl())) {
            LOG.info(isTestStr + " 处理失败 " + newsJO.toString());
            return ERROR.NeedMoreParams;
        } else
            fixNews(news);
        if (isOneNull(news.getTitleSrc(), news.getPubdate(), news.getTextSrc(), news.getComeFrom(), news.getUrl(),
                String.valueOf(news.getMediaType()), news.getMediaTname())) {
            LOG.info(isTestStr + " 处理失败 " + newsJO.toString());
            return ERROR.NeedMoreParams;
        }
        if (isAllNull(news.getMediaNameSrc(), news.getMediaNameZh(), news.getMediaNameEn())) {
            return ERROR.MediaNameWrong;
        }
        if (isTest) {
            newsDaoTest.Insert(news);
        } else {
            newsDao.Insert(news);
        }
        LOG.info(isTestStr + " 处理成功 title :" + newsJO.getString(NewsMap.TITLE_SRC) + "--- URL: " + newsJO.getString(NewsMap.URL));
        return ERROR.Success;
    }


    private void fixNews(News news) {
        if (isAllNull(news.getMediaNameSrc(), news.getMediaNameZh(), news.getMediaNameEn())) {
            MediaSrcUtil.fixNews(news);
        } else {
            MediaSrcUtil.fixNewsMediaName(news);
        }
        if (news.getMediaTname() != null) {
            news.setMediaType(DicMap.getMediaType(news.getMediaTname()));
        } else {
            news.setMediaTname(DicMap.getMediaTName(news.getMediaType()));
        }
        // 国家
        if (news.getCountryNameEn() == null && news.getCountryNameZh() != null) {
            news.setCountryNameEn(DicMap.getCountryEn(news.getCountryNameZh()));
        } else if (news.getCountryNameEn() != null && news.getCountryNameZh() == null) {
            news.setCountryNameZh(DicMap.getCountryZh(news.getCountryNameEn()));
        }
        // 语言
        if (news.getLanguageCode() == null && news.getLanguageTname() != null) {
            news.setLanguageCode(DicMap.getLanguageEn(news.getLanguageTname()));
        } else if (news.getLanguageCode() != null && news.getLanguageTname() == null) {
            news.setLanguageTname(DicMap.getLanguageZh(news.getLanguageCode()));
        }
    }

    private void add(Map<ERROR, Integer> countMap, ERROR e) {
        if (countMap.containsKey(e)) {
            countMap.put(e, 1 + countMap.get(e));
        } else {
            countMap.put(e, 1);
        }
    }

    enum ERROR {
        Success, NeedMoreParams, MediaNameWrong, WrongFormat;
    }

}
