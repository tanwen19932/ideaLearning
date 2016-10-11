package edu.buaa.nlp.es.client;

import edu.buaa.nlp.es.constant.Configuration;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;



public class ESClient {
	
	
	public static Client getClient() throws UnknownHostException {
		Settings settings = Settings.settingsBuilder()
				.put("cluster.name", Configuration.CLUSTER_NAME)
				.build();
		Client client =TransportClient.builder().settings(settings).build()
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(Configuration.INDEX_SERVER_ADDRESS), 9300));
		return client;
	}

	public static Client getClientTotal() throws UnknownHostException {
		Settings settings = Settings.settingsBuilder()
				.put("cluster.name", Configuration.TOTAL_CLUSTER_NAME)
				.build();
		Client client =TransportClient.builder().settings(settings).build()
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(Configuration.TOTAL_INDEX_SERVER_ADDRESS), 9300));
		return client;
	}
	
	public static Client getClient(String clusterName,String serverAddress) throws UnknownHostException {
		//		Settings settings = ImmutableSettings.settingsBuilder().put("client.transport.sniff", true).build();
		//		Client client = new TransportClient().addTransportAddress(new InetSocketTransportAddress(Mapper.INDEX_SERVER_ADDRESS, 9300));
		Settings settings = Settings.settingsBuilder()
				.put("cluster.name",clusterName)
				.build();
		Client client =TransportClient.builder().settings(settings).build()
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serverAddress), 9300));
		return client;
	}
	
	public static void main(String[] args) {
		Client c = null;
		try {
			c = getClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(c);
	}
}
