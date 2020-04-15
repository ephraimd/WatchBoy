package contract;

import core.DirectoryWatcher;
import ephraim.JDispatcher;

import java.util.Map;

public interface WatcherService {
    Map<Long, Event> getActivityLog();

    DirectoryWatcher setCommunicator(JDispatcher dispatcher);

    void start();

    void destroy();

    interface Event<T> {
        T getEventObject();
    }
}
