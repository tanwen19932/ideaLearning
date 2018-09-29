package hdfs.ipc;

import hdfs.proto.MkdirsRequestProto;
import hdfs.proto.MkdirsResponseProto;

/**
 * 这里是对 Client 和 protobuf 的定义的一个 adapter
 * @author TW
 * @date TW on 2017/5/3.
 */
public class ClientNamenodeProtocolTranslatorPB implements ClientProtocol {

    ClientNamenodeProtocolPB rpcProxy;
    public ClientNamenodeProtocolTranslatorPB(ClientNamenodeProtocolPB rpcProxy){
        this.rpcProxy = rpcProxy;
    }
    public boolean mkdirs(String src, boolean isAuth, boolean createParent) {
        MkdirsRequestProto req = new MkdirsRequestProto();
        return rpcProxy.mkdirs(null, req).getResult();
    }
}
