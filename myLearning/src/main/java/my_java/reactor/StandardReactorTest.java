package my_java.reactor;

/**
 * @author TW
 * @date TW on 2017/3/11.
 */
public class StandardReactorTest {
    public static void main(String[] args) {
        Server server = new Server(100);
        Event event = new Event();
        event.type = EventType.ACCEPT;
        server.selector.addEvent(event);
        server.start();
    }
}
