package com;

import org.apache.commons.httpclient.HttpClient;
import org.json.JSONArray;

import tw.utils.HttpUtil;

public class TwTest0 {

    public static void main(String[] args) {
        long beging = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            JSONArray jArray = new JSONArray();
            String jo1 = "{\"titleSrc\":\"Hakuna Matata biancogold: https://instagramcom/twhx/ O shit\",\"pubdate\":\"2016-07-21 15:31:00\",\"textSrc\":\"biancogold:“https://instagramcom/twhx/”O shit Anthony Van Engelen switch flip envy from his curtains part in Vans Propeller Back the champ and check out AVEs signature apparel line and new colorways of his seamless shoe, the AV Rapidweld Pro, at vanscom/AV and locate a shop near you with our Store LocatorAVE hops on the Vans Pro Skate Tour today in Philly as well \",\"url\":\"http://psychokiller00.tumblr.com/post/147762784140\",\"comeFrom\":\"Cision\",\"mediaType\":1,\"mediaTname\":\"新闻\",\"mediaNameSrc\":\"とよた\",\"mediaNameZh\":\"丰田汽车\",\"mediaNameEn\":\"TOYOTA USA\",\"countryNameZh\":\"美国\",\"countryNameEn\":\"United States\",\"districtNameZh\":\"米苏拉\",\"districtNameEn\":\"Missoula\",\"languageCode\":\"en\",\"languageTname\":\"英语\",\"author\":\"JaoH\",\"view\":0,\"transFromM\":\"\",\"comeFromDb\":\"3522@article_0601\",\"created\":\"2016-07-21 15:31:00\",\"docLength\":426,\"isHome\":false,\"isPicture\":false,\"isOriginal\":false}";
            for (int j = 0; j < 1000; j++) {
                jArray.put(jo1);
            }
            System.out.println(" 耗时 》 " + (beging - System.nanoTime()) / 1000 + " s" + HttpUtil.doPost("http://localhost:8080/ROOT/webservice/insertNewsForPost", jArray));
        }

    }

}
