package model;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Settings {
    @Parameter(names = {"--help", "-h"}, help = true, description = "Get usage details")
    public boolean help = false;

    @Parameter(names = {"-paths", "-p"}, listConverter = FileListConverter.class, description = "Comma-separated list of paths to watch")
    public List<Path> files = new ArrayList<>();

    @Parameter(names = {"-logfile", "-lf"}, description = "Log file path. If set, watchboy automatically logs to specified file")
    public String logfile;

    @Parameter(names = {"-ll", "-loglevel"}, description = "1 for console/file logging only depending on if logfile is set or not, 2 for both")
    public Integer verbose = 2;

    @Parameter(names = {"-recursive", "-r"}, description = "Watch all files under the set path recursively")
    public boolean recursive = false;

    @Parameter(names = "-debug", description = "Debug mode")
    public boolean debug = false;

    public class FileListConverter implements IStringConverter<List<Path>> {
        @Override
        public List<Path> convert(String files) {
            List<Path> pathList = new ArrayList<>();
            files += ",";
            for (String path : files.split(",")) {
                pathList.add(Paths.get(path));
            }
            return pathList;
        }
    }
}
