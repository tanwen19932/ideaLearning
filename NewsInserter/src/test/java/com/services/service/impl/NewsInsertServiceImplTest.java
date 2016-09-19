package com.services.service.impl;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import tw.utils.HttpUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author TW
 * @date TW on 2016/9/8.
 */
public class NewsInsertServiceImplTest {
    @Test
    public void testAPI()
            throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        long beging = System.currentTimeMillis();
        Future future = null;
        for (int i = 0; i < 30; i++) {
            future= executorService.submit(new Runnable() {
                @Override
                public void run() {
                    JSONArray jArray = new JSONArray();
                    String jo1 = "{\"titleSrc\":\"Hakuna Matata biancogold: https://instagramcom/twhx/ O shit\",\"pubdate\":\"2016-07-21 15:31:00\",\"textSrc\":\"biancogold:“https://instagramcom/twhx/”O shit Anthony Van Engelen switch flip envy from his curtains part in Vans Propeller Back the champ and check out AVEs signature apparel line and new colorways of his seamless shoe, the AV Rapidweld Pro, at vanscom/AV and locate a shop near you with our Store LocatorAVE hops on the Vans Pro Skate Tour today in Philly as well \",\"url\":\"http://psychokiller00.tumblr.com/post/147762784140\",\"comeFrom\":\"Cision\",\"mediaType\":1,\"mediaTname\":\"新闻\",\"mediaNameSrc\":\"とよた\",\"mediaNameZh\":\"丰田汽车\",\"mediaNameEn\":\"TOYOTA USA\",\"countryNameZh\":\"美国\",\"countryNameEn\":\"United States\",\"districtNameZh\":\"米苏拉\",\"districtNameEn\":\"Missoula\",\"languageCode\":\"en\",\"languageTname\":\"英语\",\"author\":\"JaoH\",\"view\":0,\"transFromM\":\"\",\"comeFromDb\":\"3522@article_0601\",\"created\":\"2016-07-21 15:31:00\",\"docLength\":426,\"isHome\":false,\"isPicture\":false,\"isOriginal\":false}";
                    for (int j = 0; j < 1000; j++) {
                        jArray.put(new JSONObject(jo1));
                    }
                    // System.out.println(jo1);
                    System.out.println(Thread.currentThread().getName() + HttpUtil.doPost(
                            "http://192.168.9.126:8080/webservice/insertNewsForPost/dfa95dbe9d657116c5613d6b6c05abcd",
                            //"http://192.168.9.126:8080/webservice/insertNewsTest/dfa95dbe9d657116c5613d6b6c05abcd",
                            jArray));
                    System.out.println(Thread.currentThread().getName() + " 耗时 》 "
                            + (System.currentTimeMillis() - beging) / 1000 + " s");
                }
            });
        }
        future.get();
    }
}