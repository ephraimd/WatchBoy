
import core.DirectoryWatcher;
import ephraim.JSSDispatcher;
import ephraim.Slot;
import util.Utility;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Main watchman class
 */
public class WatchMain {
    public WatchMain() {
        if (Utility.SETTING.files.isEmpty()) {
            System.out.println("There are no paths to watch...");
        } else {
            setup();
        }
    }

    public static void main(String[] args) {
        Utility.init(args);
        new WatchMain();
        //System.out.println("Logfile: " + Utility.SETTING.logfile);
    }

    private void setup() {
        JSSDispatcher.getDispatcher().addSlot("eventOccured()", new Slot() {
            @Override
            public void exec(Object... objects) {
                WatchEvent<Path> event = (WatchEvent<Path>) objects[0];
                try {
                    System.out.printf("Occured -> %s: %s\n", event.kind().name(), event.context().toRealPath());
                } catch (IOException ex) {
                    Utility.LOGGER.error(ex);
                }
            }
        });

        for (Path path : Utility.SETTING.files) {
            new DirectoryWatcher(path, Utility.SETTING.recursive)
                    .setCommunicator(JSSDispatcher.getDispatcher())
                    .start();
        }
    }
}
