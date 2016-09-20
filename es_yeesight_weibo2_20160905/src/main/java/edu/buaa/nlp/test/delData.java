package edu.buaa.nlp.test;

import edu.buaa.nlp.es.client.ESClient;
import edu.buaa.nlp.es.constant.Configuration;
import edu.buaa.wordsegment.PreProcessor;
import org.elasticsearch.action.delete.DeleteResponse;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class delData {

	
	public static boolean delWeibo(String myId)
	{
	    try {
			DeleteResponse response = ESClient.getClient().prepareDelete(Configuration.SOCIALITY_INDEX_NAME,
					Configuration.SOCIALITY_INDEX_TYPE_WEIBO, myId)   
			        .execute()   
			        .actionGet();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    return true;
	}
	
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if((args==null) || (args.length<2))
		{
			System.out.println("Please input: delData filename");
		}

		List<String> lstIds = new ArrayList<String>();
		System.out.println("输入文件名:" + args[1]);

		String content = PreProcessor.readFile(args[1]);
		String[] ids = content.split("[\r\n]+");
		for(String id : ids)
		{
			if(id.trim().isEmpty()) continue;
			if(id.trim().matches("[0-9a-zA-Z]+"))
			{
				lstIds.add(id.trim());
			}
		}
		System.out.println(lstIds.size());

		for(String id : lstIds)
		{
			System.out.println(id);
		    try {
				DeleteResponse response = ESClient.getClient().prepareDelete(Configuration.INDEX_NAME,
						Configuration.INDEX_TYPE_ARTICLE, id)
				        .execute()
				        .actionGet();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    //System.out.println(response);
		}
		System.out.println("OK");
	}

	
	
	
}
