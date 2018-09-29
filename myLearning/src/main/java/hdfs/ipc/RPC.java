//package hdfs.ipc;
//
//import org.apache.commons.configuration.Configuration;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.hadoop.io.Writable;
//import org.apache.hadoop.ipc.ProtocolInfo;
//import org.apache.hadoop.ipc.RpcServerException;
//import org.apache.hadoop.ipc.VersionedProtocol;
//import org.apache.hadoop.ipc.WritableRpcEngine;
//import org.apache.hadoop.util.ReflectionUtils;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// *
// * RPC 包括了所有的 Client Server 交互信息
// * 以及定义各个代理方法
// * @author TW
// * @date TW on 2017/5/4.
// */
//public class RPC {
//    final static int RPC_SERVICE_CLASS_DEFAULT = 0;
//  public enum RpcKind {
//    RPC_BUILTIN ((short) 1),         // Used for built in calls by tests
//    RPC_WRITABLE ((short) 2),        // Use WritableRpcEngine
//    RPC_PROTOCOL_BUFFER ((short) 3); // Use ProtobufRpcEngine
//    final static short MAX_INDEX = RPC_PROTOCOL_BUFFER.value; // used for array size
//    public final short value; //TODO make it private
//
//    RpcKind(short val) {
//      this.value = val;
//    }
//  }
//
//  interface RpcInvoker {
//    /**
//     * Process a client call on the server side
//     * @param server the server within whose context this rpc call is made
//     * @param protocol - the protocol name (the class of the client proxy
//     *      used to make calls to the rpc server.
//     * @param rpcRequest  - deserialized
//     * @param receiveTime time at which the call received (for metrics)
//     * @return the call's return
//     * @throws IOException
//     **/
//    public Writable call(Server server, String protocol,
//                         Writable rpcRequest, long receiveTime) throws Exception ;
//  }
//
//  static final Log LOG = LogFactory.getLog(RPC.class);
//
//
//
//}
