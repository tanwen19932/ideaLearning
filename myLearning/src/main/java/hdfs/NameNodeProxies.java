package hdfs;

import hdfs.ipc.ClientNamenodeProtocolTranslatorPB;
import hdfs.ipc.ClientProtocol;
//import hdfs.ipc.RPC;

import java.net.InetSocketAddress;
import java.net.URI;

/**
 * @author TW
 * @date TW on 2017/5/4.
 */
public class NameNodeProxies {


    public static class ProxyAndInfo<PROXYTYPE> {
        private final PROXYTYPE proxy;
        private final String dtService;
        private final InetSocketAddress address;

        public ProxyAndInfo(PROXYTYPE proxy, String dtService,
                            InetSocketAddress address) {
            this.proxy = proxy;
            this.dtService = dtService;
            this.address = address;
        }

        public PROXYTYPE getProxy() {
            return proxy;
        }

        public String getDelegationTokenService() {
            return dtService;
        }

        public InetSocketAddress getAddress() {
            return address;
        }
    }

    /**
     * 主要是为了解决 HA 的 NameNode 对于 DFSClient 来说是透明的
     */
    public static <T> ProxyAndInfo<T> createProxy(String conf, URI namenodeUri, Class<T> xface) {
        return createNonHAProxy(conf, getAddr(namenodeUri), xface);
    }

    public static <T> ProxyAndInfo<T> createNonHAProxy(String conf, InetSocketAddress addr, Class<T> xface) {
        T proxy = null;
        if (xface == ClientProtocol.class) {
            proxy = (T) createNNProxyWithClientProtocol(addr, conf, "", true, true);
        }
        return new ProxyAndInfo<T>(proxy, "", addr);
    }

    public static ClientProtocol createNNProxyWithClientProtocol(InetSocketAddress ip, String conf, String ugi, boolean withRetries, boolean fallbackToSimpleAuth) {
        //RPC.



        return new ClientNamenodeProtocolTranslatorPB(null);
    }


    public static InetSocketAddress getAddr(URI uri) {
        return new InetSocketAddress(uri.getHost(), 9000);
    }
}
