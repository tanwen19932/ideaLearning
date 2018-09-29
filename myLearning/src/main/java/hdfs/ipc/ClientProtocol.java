package hdfs.ipc;

/**
 * @author TW
 * @date TW on 2017/5/2.
 */
public interface ClientProtocol {
    public boolean mkdirs(String path, boolean isAuth , boolean createParent);
}
