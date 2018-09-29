//package protobuf;
//
//import com.google.protobuf.InvalidProtocolBufferException;
//
///**
// * @author TW
// * @date TW on 2017/5/2.
// */
//public class ProtobufTest {
//    public static void main(String[] args){
//        UserInfoOuterClass.UserInfo info =UserInfoOuterClass.UserInfo.newBuilder()
//                .setReceiverId(1).setSenderId(2).setSenderName("张三")
//                .setReceiverName("李四")
//                .build();
//        //得到 bytes 序列化
//        byte[] myinfo = info.toByteArray();
//        System.out.println(myinfo.length);
//        //info.getD
//        try {
//            //反序列化
//            UserInfoOuterClass.UserInfo info2 =UserInfoOuterClass.UserInfo.parseFrom(myinfo);
//            System.out.println(info2.getReceiverName());
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }
//
//    }
//}
