package hdfs.ipc;

import hdfs.proto.MkdirsRequestProto;
import hdfs.proto.MkdirsResponseProto;

/**
 * 这里是简单的通过 protobuf 生成的类的一个子类
 * 用子类的原因是支持注解保证安全性等.
 * @author TW
 * @date TW on 2017/5/3.
 */
public interface ClientNamenodeProtocolPB {
    public MkdirsResponseProto mkdirs(Object what, MkdirsRequestProto request);
}
