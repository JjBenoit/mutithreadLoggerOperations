# mutithreadLoggerOperations
mutithreadLoggerOperations

A light multi-thread java architecture to log some datas asynchronously .

Threads producers of logs submit datas in a queue for consumers. Consumers accumulate datas . An aggregator thread will periodically collect all the datas  in order to write them.
