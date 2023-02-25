package jjben.asynchstatlogger.fwk.actor;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jjben.asynchstatlogger.fwk.dto.DataDto;
import jjben.asynchstatlogger.fwk.dto.StatisticsDto;

public class ConsummerThread<D extends DataDto> implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ConsummerThread.class.getName());

    private final AsynchronousStatEngine<D> engine;

    private Map<String, StatisticsDto<D>> localRepo;

    private boolean mustRefreshStatRepoRef;

    public ConsummerThread(AsynchronousStatEngine<D> engine) {

	this.engine = engine;
	this.engine.getStatAggregator().register(this);
	this.localRepo = engine.getStatAggregator().getAggregationLogs();
    }

    @Override
    public void run() {

	while (!Thread.currentThread().isInterrupted()) {

	    // important that no wait here
	    D statDto = engine.getQueue().poll();
	    if (statDto != null) {
		StatisticsDto<D> statisticsDto = localRepo.get(statDto.getKey());
		if (statisticsDto == null) {
		    statisticsDto = engine.getFactory().make(statDto.getKey());
		    localRepo.putIfAbsent(statDto.getKey(), statisticsDto);
		    statisticsDto = localRepo.get(statDto.getKey());
		}

		synchronized (statisticsDto) {
		    statisticsDto.addData(statDto);
		}

	    }

	    if (mustRefreshStatRepoRef) {
		this.localRepo = engine.getStatAggregator().getAggregationLogs();
		mustRefreshStatRepoRef = false;
		LOGGER.log(Level.FINEST, "Changed Ref sended to Agregator from " + Thread.currentThread().getName());
	    }

	}

	engine.getStatAggregator().unregister(this);

    }

    // use by agregator to query statistics from consummer thread
    public void notifyMustRefreshStatRepoRef() {
	LOGGER.log(Level.FINEST, "Notifiy mustRefreshStatRepoRef From Agregator");
	mustRefreshStatRepoRef = true;
    }

    public boolean isMustRefreshStatRepoRef() {
	return mustRefreshStatRepoRef;
    }

}
