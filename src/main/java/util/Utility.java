package util;

import com.beust.jcommander.JCommander;
import ephraim.JLogger;
import model.Settings;

import java.nio.file.Paths;

public class Utility {
    public static final Settings SETTING = new Settings();
    public static final JLogger LOGGER = new JLogger();

    public static void init(String[] args) {
        JCommander cmd = JCommander.newBuilder()
                .addObject(SETTING)
                .build();
        cmd.parse(args);
        setupLogging();
        if (SETTING.help) {
            cmd.usage();
        }
    }

    private static void setupLogging() {
        if (SETTING.logfile != null) {
            LOGGER.setLogToFile(true);
            LOGGER.setLogFilePath(Paths.get(SETTING.logfile));
        }
        if (SETTING.verbose == 1 && SETTING.logfile == null) {
            LOGGER.setLogToConsole(true);
        }
        if (SETTING.verbose == 2) {
            LOGGER.setLogToConsole(true);
        }
    }
}
