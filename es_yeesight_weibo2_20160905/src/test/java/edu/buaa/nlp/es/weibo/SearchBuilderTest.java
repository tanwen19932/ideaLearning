package edu.buaa.nlp.es.weibo;

import org.junit.Test;

/**
 * @author TW
 * @date TW on 2016/9/19.
 */
public class SearchBuilderTest {

    @Test
    public void testCrossSearch()
            throws Exception {
        SearchBuilder sb = new SearchBuilder();
        sb.crossSearch("{\"keyword\":\"\\\"Google\\\"\",\"resultType\":\"front\",\"pageNo\":1,\"pageSize\":500,\"highlight\":false,\"fieldName\":\"_score\",\"order\":\"desc\",\"type\":\"weibo\"}");
    }
}