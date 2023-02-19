# mutithreadLoggerOperations
mutithreadLoggerOperations

A light multithread java architecture to log some datas asynchronously .

Threads producers of logs submit datas in a queue for consummers. Consummers accumulate datas . An aggregator thread will periodically collect all the datas  in order to write them.
