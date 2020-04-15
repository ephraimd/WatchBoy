package core;

import contract.WatcherService;
import ephraim.JDispatcher;
import util.Utility;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 * Watches single directory for filesystem events
 */
public class DirectoryWatcher implements WatcherService {
    private static HashMap<WatchKey, Path> registeredPaths = new HashMap<>(100);
    private static WatchService watcher;
    private static int instanceCounter = 0;

    static {
        //one watcher for all watches
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            Utility.LOGGER.error(ex);
        }
    }

    private HashMap<Long, Event> activityLog = new HashMap<>(300); //each DirectoryWatcher instance keeps its activity log
    private JDispatcher dispatcher;
    private boolean isWatching = true,
            recursive = false;
    private int id;

    public DirectoryWatcher(Path pathToWatch, boolean recursive) {
        if (recursive) {
            this.recursive = recursive;
            registerPathRecursive(pathToWatch);
        } else {
            registerPath(pathToWatch);
        }
    }

    public int incInstanceCount() {
        return ++instanceCounter;
    }
    /*
    public void setup(){
        try{
             watcher = FileSystems.getDefault().newWatchService();
        }catch (IOException ex){
            Utility.LOGGER.error(ex);
        }
    }*/

    public void registerPath(final Path pathToWatch) {
        if (!Files.exists(pathToWatch)) {
            throw new IllegalArgumentException("Path received does not exist");
        }
        WatchKey key = null;
        try {
            key = pathToWatch.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.OVERFLOW);
        } catch (IOException ex) {
            Utility.LOGGER.error(ex);
        }
        Utility.LOGGER.println((registeredPaths.get(key) != null ? "Updated Registered Path: " : "Registered new Path: ") + pathToWatch.toString());
        registeredPaths.put(key, pathToWatch);
    }

    public void registerPathRecursive(final Path pathToWatch) {
        if (!Files.exists(pathToWatch)) {
            throw new IllegalArgumentException("Path received does not exist");
        }
        try {
            // register directory and sub-directories
            Files.walkFileTree(pathToWatch, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    registerPath(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            Utility.LOGGER.error(ex);
        }
    }

    private void processEvents() {
        while (isWatching) {
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException ex) {
                Utility.LOGGER.error(ex);
                return;
            }

            Path dir = registeredPaths.get(key);
            if (dir == null) {
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                // TBD - provide example of how OVERFLOW event is handled
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path name = ev.context();
                Path child = dir.resolve(name);

                dispatcher.emit("eventOccured()", ev);
                if (Utility.SETTING.debug) {
                    // print out event
                    Utility.LOGGER.print("%s: %s\n", event.kind().name(), child);
                }

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == StandardWatchEventKinds.ENTRY_CREATE)) {
                    if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                        Utility.LOGGER.println("Adding new folder to watch list: " + child.toString());
                        registerPathRecursive(child);
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                registeredPaths.remove(key);
                // all directories are inaccessible
                if (registeredPaths.isEmpty()) {
                    Utility.LOGGER.println("No registered Path remain anymore. Service will stop");
                    destroy();
                }
            }
        }
    }

    /**
     * Returns the activity log since path is registered.
     * The Key contains timestamp of activity, and the value has the detail about the event that occured
     *
     * @return
     */
    @Override
    public Map<Long, Event> getActivityLog() {
        return activityLog;
    }

    /**
     * For now we're using the SignalSlot4J library to emit events
     */
    @Override
    public DirectoryWatcher setCommunicator(JDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        return this;
    }

    @Override
    public void start() {
        this.id = incInstanceCount();
        //Utility.LOGGER.println("Starting Directory Watcher: " + id);
        processEvents();
    }

    @Override
    public void destroy() {
        isWatching = false;
    }
}
