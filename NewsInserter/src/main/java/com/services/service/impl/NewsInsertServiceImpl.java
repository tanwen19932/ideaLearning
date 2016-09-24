package com.services.service.impl;

import com.services.service.InserterService;
import news.DicMap;
import news.News;
import news.NewsDao;
import news.filter.NewsFilterChain;
import news.mediaSrc.util.MediaSrcUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import tw.utils.FileUtil;
import tw.utils.JsonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import static tw.utils.StringUtil.isAllNull;
import static tw.utils.StringUtil.isOneNull;

//@WebService(endpointInterface = "news.api.hbase.InserterService")
//@SOAPBinding(style = SOAPBinding.Style.RPC)
// @WebService
public class NewsInsertServiceImpl
        implements InserterService {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(NewsInsertServiceImpl.class);
    private static AtomicInteger todayCountRequest = new AtomicInteger();
    //private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    private static final String ERROR_STR = "{ \"Exception\" :\"EXCEPTION\"}";
    static NewsDao newsDao = new NewsDao();
    //static NewsDaoTest newsDaoTest = new NewsDaoTest();
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
        //Future<String> result = threadPool.submit(new Callable<String>() {
        //    @Override
        //    public String call()
        //            throws Exception {
                JSONObject result = new JSONObject();
                int count = todayCountRequest.incrementAndGet();
                LOG.info(" 处理请求 " + count);
                Map<ERROR, Integer> countMap = new HashMap<>();
                try {
                    JSONArray newsArray = new JSONArray(newsJsonStr);
                    for (Object jo : newsArray) {
                        JSONObject newsJO = new JSONObject(jo) ;
                        add(countMap, insert(newsJO, isTest));
                    }
                } catch (Exception e) {
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
        //    }
        //});
        //try {
        //    return result.get();
        //} catch (Exception e) {
        //    e.printStackTrace();
        //    return ERROR_STR.replaceAll("EXCEPTION", e.getClass().getName());
        //}
    }

    private ERROR insert(JSONObject newsJO, boolean isTest)
            throws IOException {
        News news = News.getFromJSONObject(newsJO);
        //String isTestStr = "是否测试 " + isTest;
        NewsFilterChain.tryFilter(news);
        ERROR result = checkError(news);
        if(result==ERROR.Success){
            if (isTest) {
                FileUtil.fileAppendJson(news.getComeFrom() + "Test/", JsonUtil.getJsonObj(news));
            } else {
                //FileUtil.fileAppendJson(news.getComeFrom() + "/", JsonUtil.getJsonObj(news));
                newsDao.Insert(news);
            }
        }
        //LOG.info(news.getId() + "  " + isTestStr + " 处理成功 title :" + newsJO.getString(NewsMap.TITLE_SRC) + "--- URL: " + newsJO.getString(NewsMap.URL));
        return result;
    }

    public ERROR checkError(News news) {
        if (isOneNull(news.getTitleSrc(), news.getPubdate(), news.getTextSrc(), news.getComeFrom(), news.getUrl(), news.getMediaTname())) {
            //LOG.info(isTestStr + " 处理失败 " + newsJO.toString());
            return ERROR.NeedMoreParams;
        }
        if (isAllNull(news.getMediaNameSrc(), news.getMediaNameZh(), news.getMediaNameEn())) {
            return ERROR.MediaNameWrong;
        }
        else return ERROR.Success;
    }

    private void fixNews(News news) {
        if (isAllNull(news.getMediaNameSrc(), news.getMediaNameZh(), news.getMediaNameEn())) {
            //MediaSrcUtil.fixNews(news);

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
        Success, NeedMoreParams, MediaNameWrong, WrongFormat
    }

}
