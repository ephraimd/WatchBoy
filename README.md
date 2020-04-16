# WatchBoy
Simple directory watching service that's really simple to extend and use.
This is still pretty much work in progress as the final functionalities are yet to be coded

## How to use
Here's the raw usage dump
```
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

#### Examples
*   Print usage dump
    ```shell
    $ WatchBoy-1.0.jar -h
    ```
*   Watch path
    ```shell
    $ WatchBoy-1.0.jar -p C:\Users\ME\Codes\Java\projects\raw-ML
    ```
*   Watch multiple paths. If there's space in your path, put em all in double quotes
    ```shell
    $ WatchBoy-1.0.jar -p "C:\Users\ME\Codes\Java\projects\raw-ML,C:\Users\me\codes\Simple Ebay SDK"
    ```
*   Put all logs in a logfile
    ```shell
    $ WatchBoy-1.0.jar -lf logging.txt -p "C:\Users\ME\Codes\Java\projects\raw-ML,C:\Users\me\codes\Simple Ebay SDK"
    ```
*   Log to both file and console. If no logfile is provided, all logs will go to the console only. If provided, all logs will go to the file only except:
    * verbosity level (ll) = 2
    * debug = true 
    ```shell
    $ WatchBoy-1.0.jar -lf logging.txt -ll 2 -p "C:\Users\ME\Codes\Java\projects\raw-ML,C:\Users\me\codes\Simple Ebay SDK"
    ```