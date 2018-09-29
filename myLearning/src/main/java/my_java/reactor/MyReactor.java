package my_java.reactor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author TW
 * @date TW on 2017/4/18.
 */
public class MyReactor {
    _Demultiplexer demultiplexer = new _Demultiplexer();
    Map<_HandleType,_EventHandler> handlers = new HashMap<>();
    public void registHandler(_HandleType type,_EventHandler handler) {
        handlers.put(type,handler);
    }

    public void removeHandler(_HandleType type) {
        handlers.remove(type);
    }

    public void handleEvents() {
        while (true){
            List<_Handle> handles = demultiplexer.select();
            for (_Handle handle : handles) {
                handlers.get(handle.type).handle(handle);
            }
        }

    }
}

class _Handle {
    _HandleType type;
    String msg;
}

enum _HandleType {
    First,
    Second
}

abstract class _EventHandler {

    abstract public void handle(_Handle handle);
}

class _FirstHanler extends _EventHandler {
    _Demultiplexer selector;
    public _FirstHanler(_Demultiplexer selector){
        this.selector = selector;
    }
    @Override
    public void handle(_Handle handle) {
        System.out.println("处理事件 " + handle.msg + " 类型:" + handle.type.name());
    }
}

class _Demultiplexer {
    BlockingQueue<_Handle> msgs = new LinkedBlockingDeque<>();

    public List<_Handle> select() {
        List<_Handle> handles = new ArrayList<>();
        if (!msgs.isEmpty()) {
            msgs.drainTo(handles);
        }
        return handles;
    }

    public void addEvent(_Handle handle) {
        msgs.add(handle);
    }
}