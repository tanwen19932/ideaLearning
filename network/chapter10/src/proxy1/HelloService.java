package proxy1;
import java.util.Date;
public interface HelloService{
  public String echo(String msg)throws RemoteException;
  public Date getTime()throws RemoteException;
} 


/****************************************************
 * 作者：孙卫琴                                     *
 * 来源：<<Java网络编程精解>>                       *
 * 技术支持网址：www.javathinker.org                *
 ***************************************************/
