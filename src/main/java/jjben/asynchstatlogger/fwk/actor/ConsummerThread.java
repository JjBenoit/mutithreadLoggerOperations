package jjben.asynchstatlogger.fwk.actor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jjben.asynchstatlogger.fwk.dto.DataDto;
import jjben.asynchstatlogger.fwk.dto.StatisticsDto;

public class ConsummerThread<D extends DataDto, S extends StatisticsDto<D, S>> implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ConsummerThread.class.getName());

    private ConcurrentHashMap<String, S> statisticsRepository;
    private final AsynchronousStatEngine<D, S> engine;

    private boolean logsAskedFromAgregator;

    public ConsummerThread(AsynchronousStatEngine<D, S> engine) {

	this.statisticsRepository = new ConcurrentHashMap<>();
	this.engine = engine;
	engine.getStatAggregator().register(this);
    }

    @Override
    public void run() {

	while (!Thread.currentThread().isInterrupted()) {

	    // NO wait here , important coz logsAskedFromAgregator have to be evaluated
	    // regulary
	    D statDto = engine.getQueue().poll();

	    if (statDto != null) {
		S statisticsDto = statisticsRepository.get(statDto.getKey());
		if (statisticsDto == null) {
		    statisticsDto = engine.getFactory().make(statDto.getKey());
		    statisticsRepository.put(statDto.getKey(), statisticsDto);
		}

		statisticsDto.addData(statDto);

	    }
	}
	if (logsAskedFromAgregator) {
	    logsAskedFromAgregator = false;

	    engine.getStatAggregator().receiveStats(statisticsRepository);
	    LOGGER.log(Level.FINEST, "logs sended to Agregator from " + Thread.currentThread().getName());

	    this.statisticsRepository = new ConcurrentHashMap<>();
	    LOGGER.log(Level.FINEST, "new map created by " + Thread.currentThread().getName());

	}

	engine.getStatAggregator().unregister(this);

    }

    // use by agregator to query statistics from consummer thread
    public void ask4NewLogs() {
	LOGGER.log(Level.FINEST, "logs Asked From Agregator");
	logsAskedFromAgregator = true;
    }

}
