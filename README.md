# WatchBoy
Simple directory watching service that's really simple to extend and use

## How to use
```shell script
Usage: [options]
  Options:
    --help, -h
      Get usage details
    -debug
      Debug mode
      Default: false
    -logfile, -lf
      Log file path. If set, watchboy automatically logs to specified file
    -ll, -loglevel
      1 for console/file logging only depending on if logfile is set or not, 2 
      for both
      Default: 2
    -paths, -p
      Comma-separated list of paths to watch
      Default: []
    -recursive, -r
      Watch all files under the set path recursively
      Default: false
```