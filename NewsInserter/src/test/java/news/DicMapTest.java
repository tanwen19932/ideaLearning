package news;

import edu.buaa.nlp.socialHttp.SocialDao;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

/**
 * @author TW
 * @date TW on 2016/8/23.
 */
public class DicMapTest {

    @Test
    public void testGetCountryEn()
            throws Exception {

    }


    @Test
    public void testGetCountryEnByAbbr()
            throws Exception {

    }

    @Test
    public void testGetCountryZhByAbbr()
            throws Exception {

    }

    @Test
    public void testGetLanguageEn()
            throws Exception {
        System.out.println(DicMap.getLanguageEn("英语"));
        String json = "{\"Count\":1,\"Socials\":[{\"id\":\"1234567\",\"time\":147000,\"text\":\"This is a text\",\"isOri\":1,\"userId\":\"\",\"name\":\"David Li\",\"province\":\"Beijing\",\"city\":\"Beijing\",\"gender\":-1,\"verified\":1,\"verifiedReason\":\"famous actor\",\"userType\":\"其他\",\"flwCnt\":0,\"frdCnt\":0,\"staCnt\":0,\"userAvatar\":\"http://xxx.com/yyy.jpeg\",\"rpsCnt\":0,\"cmtCnt\":0,\"atdCnt\":0,\"title\":\"This is a title\",\"country\":\"\",\"sourceType\":\"weibo\",\"comeFrom\":\"Goonie\",\"url\":\"http://www.weibo.com/123456/asdf\"}]}";
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("Socials");
        for (Object temp : jsonArray) {
            JSONObject tempJson = (JSONObject) temp;
            System.out.println("TTTTTTTTTTTTTT" + tempJson);
            System.out.println(tempJson.getLong("time"));
        }
        System.out.println(SocialDao.Insert(json));
    }

    @Test
    public void testGetLanguageZh()
            throws Exception {

    }

    @Test
    public void testGetCityEn()
            throws Exception {

    }

    @Test
    public void testGetCityZh()
            throws Exception {

    }

    @Test
    public void testGetMediaType()
            throws Exception {

    }

    @Test
    public void testGetMediaTName()
            throws Exception {

    }

    @Test
    public void testGetMediaTName1()
            throws Exception {

    }

    @Test
    public void testMain()
            throws Exception {

    }
}