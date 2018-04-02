package my_java.reactor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class StandardReactor {

}

class EventDispatcher {
    Map<EventType, EventHandler> eventHandlerMap = new ConcurrentHashMap<EventType, EventHandler>();

    Demultiplexer selector;

    EventDispatcher(Demultiplexer selector) {
        this.selector = selector;
    }

    public void registEventHandler(EventType eventType, EventHandler eventHandler) {
        eventHandlerMap.put(eventType, eventHandler);
    }

    public void removeEventHandler(EventType eventType) {
        eventHandlerMap.remove(eventType);
    }

    public void handleEvents() {
        dispatch();
    }

    private void dispatch() {
        while (true) {
            List<Event> events = selector.select();
            for (Event event : events) {
                EventHandler eventHandler = eventHandlerMap.get(event.type);
                eventHandler.handle(event);
            }
        }
    }
}

class Demultiplexer {
    private BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>();
    private Object lock = new Object();

    List<Event> select() {
        return select(0);
    }

    List<Event> select(long timeout) {
        if (timeout > 0) {
            if (eventQueue.isEmpty()) {
                synchronized (lock) {
                    if (eventQueue.isEmpty()) {
                        try {
                            lock.wait(timeout);
                        } catch (InterruptedException e) {
                            // ignore it
                        }
                    }
                }
            }
        }
        List<Event> events = new ArrayList<Event>();
        eventQueue.drainTo(events);
        return events;
    }

    public void addEvent(Event e) {
        boolean success = eventQueue.offer(e);
        if (success) {
            synchronized (lock) {
                lock.notify();
            }

        }
    }

}

class Source {
    private Date date = new Date();
    private String id = date.toString() + "_" + System.identityHashCode(date);

    @Override
    public String toString() {
        return id;
    }
}

enum EventType {
    ACCEPT, READ, WRITE, TIMEOUT;
}

class Event {
    public EventType type;
    public Source source;
}

abstract class EventHandler {
    Source source;

    public abstract void handle(Event event);

    public Source getSource() {
        return source;
    }
}

class AcceptEventHandler extends EventHandler {

    private Demultiplexer selector;

    public AcceptEventHandler(Demultiplexer selector) {
        this.selector = selector;
    }

    @Override
    public void handle(Event event) {
        if (event.type == EventType.ACCEPT) {
            System.out.println("得到个Accept的请求");
            Event readEvent = new Event();
            readEvent.source = event.source;
            readEvent.type = EventType.READ;

            selector.addEvent(readEvent);
        }
    }

}

class ReadEventHandler extends EventHandler {
    // private Pipeline pipeline;

    @Override
    public void handle(Event event) {
        // create channel with a pipeline
        // register the channel to this event dispatcher or a child event dispatcher


        // handle event use the pipeline :
        // step 1:  read to a frame buffer
        // step 2:  use frame decoder to decode buffer as a message (maybe a business object)
        // step 3:  handle the message or submit the message to business thread pool
        // step 4:  register a message event

    }

}

class WriteEventHandler extends EventHandler {

    @Override
    public void handle(Event event) {
        // step 1: encode a message to byte[]
        // step 2: submit a write task to IOWorker thread pool
    }

}

//-------------------------------分割线--------------------------//

class Acceptor implements Runnable {
    private int port; // server socket port
    private Demultiplexer selector;

    // 代表 serversocket
    private BlockingQueue<Source> sourceQueue = new LinkedBlockingQueue<Source>();

    Acceptor(Demultiplexer selector, int port) {
        this.selector = selector;
        this.port = port;
    }

    public void aNewConnection(Source source) {
        sourceQueue.offer(source);
    }

    public int getPort() {
        return this.port;
    }

    public void run() {
        while (true) {
            Source source = null;
            try {
                // 相当于 serversocket.accept()
                source = sourceQueue.take();
            } catch (InterruptedException e) {
                // ignore it;
            }

            if (source != null) {
                Event acceptEvent = new Event();
                acceptEvent.source = source;
                acceptEvent.type = EventType.ACCEPT;
                selector.addEvent(acceptEvent);
            }

        }
    }

}


class Server {
    Demultiplexer selector = new Demultiplexer();
    EventDispatcher eventLooper = new EventDispatcher(selector);
    Acceptor acceptor;

    Server(int port) {
        acceptor = new Acceptor(selector, port);
    }

    public void start() {
        eventLooper.registEventHandler(EventType.ACCEPT, new AcceptEventHandler(selector));
        new Thread(acceptor, "Acceptor-" + acceptor.getPort()).start();
        eventLooper.handleEvents();
    }

}