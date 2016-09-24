package edu.buaa.nlp.es.util;

import edu.buaa.nlp.es.client.ESClient;
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryAction;
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryRequestBuilder;
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryResponse;
import org.elasticsearch.client.Client;

import java.net.UnknownHostException;

/**
 * @author TW
 * @date TW on 2016/9/24.
 */
public class ValidateQuery {

    public static boolean check(String index, String types, String query) {

        Client client = null;
        try {
            client = ESClient.getClient();
            ValidateQueryRequestBuilder validateQueryRequestBuilder = new ValidateQueryRequestBuilder(client, ValidateQueryAction.INSTANCE);
            ValidateQueryResponse response = validateQueryRequestBuilder
                    .setIndices(index)
                    .setTypes(types)
                    .setSource(query.getBytes())
                    .execute().actionGet();
            return response.isValid();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
