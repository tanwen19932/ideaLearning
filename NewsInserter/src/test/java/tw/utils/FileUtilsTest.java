package tw.utils;

import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author TW
 * @date TW on 2016/8/30.
 */
public class FileUtilsTest {
    static Boolean lock ;
    @Test
    public void testFileAppendJson()
            throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        Future future=null ;
        long begin = System.nanoTime();
        for(int i =0; i<3 ; i++){
            future = executorService.submit(new Runnable() {
               @Override
               public void run() {
                   int i = 0;
                   while(i<10000){
                       JSONObject jsonObject = new JSONObject("{\n" +
                               "    \"Count\":1,\n" +
                               "    \"Socials\":[\n" +
                               "        {\n" +
                               "            \"id\":\"1234567\",\n" +
                               "            \"time\":1470000000000,\n" +
                               "            \"text\":\"This is a text\",\n" +
                               "            \"isOri\":1,\n" +
                               "            \"userId\":\"\",\n" +
                               "            \"name\":\"David Li\",\n" +
                               "            \"province\":\"Beijing\",\n" +
                               "            \"city\":\"Beijing\",\n" +
                               "            \"gender\":-1,\n" +
                               "            \"verified\":1,\n" +
                               "            \"verifiedReason\":\"famous actor\",\n" +
                               "            \"userType\":\"其他\",\n" +
                               "            \"flwCnt\":0,\n" +
                               "            \"frdCnt\":0,\n" +
                               "            \"staCnt\":0,\n" +
                               "            \"userAvatar\":\"http://xxx.com/yyy.jpeg\",\n" +
                               "            \"rpsCnt\":0,\n" +
                               "            \"cmtCnt\":0,\n" +
                               "            \"atdCnt\":0,\n" +
                               "            \"title\":\"This is a title\",\n" +
                               "            \"country\":\"\",\n" +
                               "            \"sourceType\":\"weibo\",\n" +
                               "            \"comeFrom\":\"Goonie\",\n" +
                               "            \"url\":\"http://www.weibo.com/123456/asdf\"\n" +
                               "        }\n" +
                               "    ]\n" +
                               "}");
                       try {
                           FileUtil.fileAppendJson("tw.txt/",jsonObject);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       i++;
                   }
               }
           });
        }
        future.get();
        System.out.println( (System.nanoTime()-begin)/1000/1000/1000);
    }
}