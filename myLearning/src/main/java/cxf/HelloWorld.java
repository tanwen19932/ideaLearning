package cxf;


import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;


@WebService
public interface HelloWorld {
    String sayHi(@WebParam(name = "text") String text);

    String sayHiToUser(User user);

    String[] SayHiToUserList(List<User> userList);
}