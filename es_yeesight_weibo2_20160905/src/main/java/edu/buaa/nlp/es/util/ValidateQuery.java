package edu.buaa.nlp.es.util;

import edu.buaa.nlp.es.client.ESClient;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryAction;
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryRequestBuilder;
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * @author TW
 * @date TW on 2016/9/24.
 */
public class ValidateQuery {
    private static final Logger LOG = Logger.getLogger(ValidateQuery.class);
    public static boolean check(String index, String types, String queryStr) {

        Client client = null;
        try {
            client = ESClient.getClient();
            QueryBuilder query = QueryBuilders.existsQuery(queryStr);
            ValidateQueryRequestBuilder validateQueryRequestBuilder = new ValidateQueryRequestBuilder(client, ValidateQueryAction.INSTANCE);
           if(index!=null) validateQueryRequestBuilder.setIndices(index);
            if(types!=null) validateQueryRequestBuilder.setTypes(types);
            ValidateQueryResponse response = validateQueryRequestBuilder
                    .setQuery(query)
                    .execute().actionGet();

            System.out.println();
            LOG.info("检测query的有效性："+response.isValid() );
            return response.isValid();
        } catch (Throwable e) {
            LOG.error("检测query的有效性出错！返回False" );
            e.printStackTrace();
            return false;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public static boolean check(String queryStr) {
        return check("news201609","article",queryStr);
    }
}
