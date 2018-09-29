package hdfs;

import hdfs.ipc.ClientProtocol;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author TW
 * @date TW on 2017/5/4.
 */
public class DFSClient {
    ClientProtocol namenode;

    public DFSClient() {
        try {
            URI nnUri = new URI("");
            NameNodeProxies.ProxyAndInfo<ClientProtocol> proxyInfo = NameNodeProxies.createProxy(
                    "", nnUri,ClientProtocol.class);
            namenode = proxyInfo.getProxy();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public boolean mkdirs(String path, boolean isAuth, boolean createParent) {
        return namenode.mkdirs(path, isAuth, createParent);
    }
}
