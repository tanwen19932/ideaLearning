package edu.buaa.nlp.es.main;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import edu.buaa.nlp.es.client.ESClient;
import edu.buaa.nlp.es.constant.Configuration;
import edu.buaa.nlp.es.news.Mapper;

public class MappingConfig {

	private static Logger logger=Logger.getLogger(MappingConfig.class);

	private static void mapping() throws IOException{
		Client client=ESClient.getClient();
/*        final IndicesExistsResponse res = client.admin().indices().prepareExists(Mapper.INDEX_NAME).execute().actionGet();
        if (res.isExists()) {
            final DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(Mapper.INDEX_NAME);
            delIdx.execute().actionGet();
        }*/

		final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(Configuration.INDEX_NAME);
        final XContentBuilder mappingBuilder = XContentFactory.jsonBuilder().startObject().startObject(Configuration.INDEX_TYPE_ARTICLE)
                .startObject("_id").field("path", "uuid").field("store", "true").field("index", "not_analyzed").endObject()
                .startObject("properties")
                .startObject("uuid").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()
                .startObject("titleSrc").field("type", "string").field("store", "true").field("boost", 5).endObject()
                .startObject("titleEn").field("type", "string").field("store", "true").field("boost", 3).endObject()
                .startObject("titleZh").field("type", "string").field("store", "true").field("boost", 3).endObject()
                .startObject("abstractSrc").field("type", "string").field("store", "true").endObject()
                .startObject("abstractEn").field("type", "string").field("store", "true").endObject()
                .startObject("abstractZh").field("type", "string").field("store", "true").endObject()
                .startObject("textSrc").field("type", "string").field("boost", 4).endObject()
                .startObject("textEn").field("type", "string").field("boost", 4).endObject()

                .startObject("mediaType").field("type", "integer").field("store", "true").endObject()
                .startObject("mediaTname").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()

                .startObject("industryId").field("type", "integer").field("store", "true").endObject()
                .startObject("industryName").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()

                .startObject("regionId").field("type", "integer").field("store", "true").endObject()
                .startObject("regionName").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()

                .startObject("languageType").field("type", "integer").field("store", "true").endObject()
                .startObject("languageTname").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()
                .startObject("languageCode").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()

                .startObject("url").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()
                .startObject("view").field("type", "integer").field("store", "true").endObject()
                .startObject("reply").field("type", "integer").field("store", "true").endObject()
                .startObject("author").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()
                .startObject("subname").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()

                .startObject("isOriginal").field("type", "integer").field("store", "true").endObject()
                .startObject("original").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()

                .startObject("categoryId").field("type", "integer").field("store", "true").endObject()
                .startObject("categoryName").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()

                .startObject("keywordSrc").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()
                .startObject("keywordEn").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()

                .startObject("sentimentId").field("type", "integer").field("store", "true").endObject()
                .startObject("sentimentName").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()

                .startObject("levelId").field("type", "integer").field("store", "true").endObject()
                .startObject("levelName").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()

                .startObject("countryId").field("type", "integer").field("store", "true").endObject()
                .startObject("countryName").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()

                .startObject("mediaId").field("type", "integer").field("store", "true").endObject()
                .startObject("mediaName").field("type", "string").field("store", "true").field("index", "not_analyzed").endObject()

                .startObject("pubdate").field("type", "date").field("store", "true").field("format", "yyyy-MM-dd HH:mm:ss").field("index", "no").endObject()
                .startObject("snatchTime").field("type", "date").field("store", "true").field("format", "yyyy-MM-dd HH:mm:ss").field("index", "no").endObject()
                .startObject("createTime").field("type", "date").field("store", "true").field("format", "yyyy-MM-dd HH:mm:ss").field("index", "no").endObject()
                .startObject("updateTime").field("type", "date").field("store", "true").field("format", "yyyy-MM-dd HH:mm:ss").field("index", "no").endObject()
                .endObject()
                .endObject().endObject();

		System.out.println(mappingBuilder.string());
		createIndexRequestBuilder.addMapping(Configuration.INDEX_TYPE_ARTICLE, mappingBuilder);

		// MAPPING DONE
		createIndexRequestBuilder.execute().actionGet();
		client.close();
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		try {
			mapping();
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
