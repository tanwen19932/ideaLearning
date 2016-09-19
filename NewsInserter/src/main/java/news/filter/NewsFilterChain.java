package news.filter;

import filter.Filter;
import filter.FilterChain;
import news.DicMap;
import news.News;
import tw.utils.DateUtil;

import java.util.LinkedList;

import static tw.utils.StringUtil.*;

public class NewsFilterChain
        extends FilterChain<News>
        implements Filter<News> {
    static NewsFilterChain newsFilterChain = new NewsFilterChain();

    private NewsFilterChain() {
        filters = new LinkedList<>();
        filters.add(new NewsMediaSrcFilter());
        filters.add(new NewsTypeFilter());
        filters.add(new NewsContentAndTimeFilter());
    }

    @Override
    public boolean filter(News param) {
        for (Filter<News> filter : filters) {
            if (filter.filter(param)) {
                System.out.println("异常将被过滤+ " + filter.getClass().getSimpleName());
                return true;
            }
        }
        return false;
    }

    public static boolean tryFilter(News news) {
        return newsFilterChain.filter(news);
    }
}

class NewsMediaSrcFilter
        implements Filter<News> {
    @Override
    public boolean filter(News news) {
        if (isNull(news.getMediaNameZh())) {
            if (!isNull(news.getMediaNameSrc())) {
                news.setMediaNameZh(news.getMediaNameSrc());
            } else if (!isNull(news.getMediaNameEn())) {
                news.setMediaNameZh(news.getMediaNameEn());
                news.setMediaNameSrc(news.getMediaNameEn());
            }
        }
        if (isNull(news.getMediaNameSrc())) {
            news.setMediaNameSrc(news.getMediaNameZh());
        }
        return isAllNull(news.getMediaNameZh(), news.getMediaNameEn(),news.getMediaNameSrc());
    }

}


class NewsTypeFilter
        implements Filter<News> {
    @Override
    public boolean filter(News news) {
        if (!isNull(news.getMediaTname())) {
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
        return isOneNull(news.getMediaTname(), news.getCountryNameEn(), news.getCountryNameZh()
                , news.getLanguageCode(), news.getLanguageTname());
    }


}

class NewsContentAndTimeFilter
        implements Filter<News> {
    @Override
    public boolean filter(News news) {
        news.setPubdate(DateUtil.tryParse(news.getPubdate()));
        if (news.getDocLength() == 0) {
            news.setDocLength(news.getTitleSrc().length() + news.getTextSrc().length());
        }
        if (isNull(news.getCreated())) {
            news.setCreated(news.getPubdate());
        }
        if (isNull(news.getUpdated())) {
            news.setUpdated(news.getPubdate());
        }
        return isOneNull(news.getTitleSrc(), news.getTextSrc(), news.getPubdate());
    }
}