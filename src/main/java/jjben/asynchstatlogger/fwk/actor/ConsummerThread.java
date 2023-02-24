package jjben.asynchstatlogger.fwk.actor;


import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jjben.asynchstatlogger.fwk.dto.DataDto;
import jjben.asynchstatlogger.fwk.dto.StatisticsDto;


public class ConsummerThread<D extends DataDto, S extends  StatisticsDto<D> > implements Runnable{

	private static final Logger LOGGER = Logger.getLogger(ConsummerThread.class.getName());

	private final AsynchronousStatEngine<D,S> engine;
	
	private Map<String, S> localRepo;
	
	private boolean mustRefreshStatRepoRef;


	public ConsummerThread(AsynchronousStatEngine<D,S> engine) {
		
		this.engine=engine;
		this.engine.getStatAggregator().register(this);
		this.localRepo = engine.getStatAggregator().getAggregationLogs();
 	}


	@Override
	public void run()
	{

		while(!Thread.currentThread().isInterrupted()) {

			D statDto = engine.getQueue().poll();
			if(statDto!=null)
			{
				S statisticsDto = engine.getStatAggregator().getAggregationLogs().get(statDto.getKey());
				if(statisticsDto == null)
				{
					statisticsDto = engine.getFactory().make(statDto.getKey());
					engine.getStatAggregator().getAggregationLogs().putIfAbsent(statDto.getKey(), statisticsDto);
					statisticsDto = engine.getStatAggregator().getAggregationLogs().get(statDto.getKey());
				}
				
				synchronized (statisticsDto) {
					statisticsDto.addData(statDto);
				}

				if(mustRefreshStatRepoRef)
				{
					mustRefreshStatRepoRef = false;
					this.localRepo = engine.getStatAggregator().getAggregationLogs();
					LOGGER.log(Level.FINEST, "Changed Ref sended to Agregator from "+Thread.currentThread().getName());
				}
			}

		}

		engine.getStatAggregator().unregister(this);

	}

	// use by agregator to query statistics from consummer thread
	public void notifyMustRefreshStatRepoRef()
	{
		LOGGER.log(Level.FINEST, "Notifiy mustRefreshStatRepoRef From Agregator");
		mustRefreshStatRepoRef=true;
	}


	public boolean isMustRefreshStatRepoRef() {
		return mustRefreshStatRepoRef;
	}


}
