package filter;

import news.News;
import news.filter.NewsFilterChain;
import org.junit.Test;

/**
 * @author TW
 * @date TW on 2016/9/8.
 */
public class NewsFilterChainTest {

    @Test
    public void testTryFilter()
            throws Exception {
        News news = new News();
        news.setId("1");
        news.setMediaTname("新闻");
        news.setTitleSrc("大新闻");
        news.setPubdate("2015-06-11 12:00:00.0");
        news.setTextSrc("超级大新闻！！");
        news.setMediaNameSrc("Sina");
        news.setLanguageCode("en");
        news.setCountryNameZh("意大利");
        news.setMediaLevel(1);
         //isAllNull(news.getMediaNameZh(), news.getMediaNameEn(),news.getMediaNameSrc());

        System.out.println("是否过滤："+ NewsFilterChain.tryFilter(news)+"\n"+news);
    }
}