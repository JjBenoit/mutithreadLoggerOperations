package jjben.asynchstatlogger.fwk.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jjben.asynchstatlogger.fwk.dto.DataDto;
import jjben.asynchstatlogger.fwk.dto.StatisticsDto;

public class StatAggregatorThread<D extends DataDto> implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(StatAggregatorThread.class.getName());

    private List<ConsummerThread<D>> consummerThreads;
    private volatile Map<String, StatisticsDto<D>> aggregationLogs;
    private AsynchronousStatEngine<D> engine;

    public StatAggregatorThread(AsynchronousStatEngine<D> engine) {
	this.consummerThreads = Collections.synchronizedList(new ArrayList<>());
	this.engine = engine;
	this.aggregationLogs = new ConcurrentHashMap<>();
    }

    public void register(ConsummerThread<D> consummerThreadLog) {
	consummerThreads.add(consummerThreadLog);
    }

    public void unregister(ConsummerThread<D> consummerThreadLog) {
	consummerThreads.remove(consummerThreadLog);
    }

    @Override
    public void run() {

	while (!Thread.currentThread().isInterrupted()) {

	    waitForNextPeriod();

	    LOGGER.log(Level.FINEST, "Begining to flush and write aggregating datas");

	    LOGGER.log(Level.FINEST, "Notify consummers threads : claim theirs datas");

	    // consumers thread will work on the current ( old ) repo which is saved to be
	    // written
	    Map<String, StatisticsDto<D>> aggregationLogsOld = this.aggregationLogs;
	    // new repo is created
	    this.aggregationLogs = new ConcurrentHashMap<>();

	    notifyAnwWaitNewLogs();

	    LOGGER.log(Level.FINEST, "Datas consolidation done, datas will be written and flush");

	    engine.getAggregatorWriter().write(aggregationLogsOld);

	}

    }

    private void notifyAnwWaitNewLogs() {

	// new consumers thread are not welcomed here, they will wait until synchro
	// process is done
	synchronized (consummerThreads) {

	    for (ConsummerThread<D> thread : consummerThreads) {
		thread.notifyMustRefreshStatRepoRef();
	    }

	    boolean allThreadHaveSwitchedToNewRepo = false;

	    while (!allThreadHaveSwitchedToNewRepo) {
		allThreadHaveSwitchedToNewRepo = true;

		for (ConsummerThread<D> consummerThread : consummerThreads) {

		    if (consummerThread.isMustRefreshStatRepoRef())
			allThreadHaveSwitchedToNewRepo = false;
		}

	    }

	}
    }

    private void waitForNextPeriod() {
	try {
	    Thread.sleep(1000 * engine.getConfig().getAggragationPeriodictyInSeconds());
	} catch (InterruptedException e) {
	    // Do nothing
	}
    }

    public Map<String, StatisticsDto<D>> getAggregationLogs() {
	return aggregationLogs;
    }

}
