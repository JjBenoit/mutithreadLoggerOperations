package jjben.asynchstatlogger.fwk.actor;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jjben.asynchstatlogger.fwk.dto.DataDto;
import jjben.asynchstatlogger.fwk.dto.StatisticsDto;



public class StatAggregatorThread<D extends DataDto, S extends  StatisticsDto<D> > implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(StatAggregatorThread.class.getName());

	private List<ConsummerThread<D,S>> consummerThreads;
	private Map<String,S> aggregationLogs;
	private AsynchronousStatEngine<D,S> engine;

	public StatAggregatorThread(AsynchronousStatEngine<D,S> engine) {
		this.consummerThreads = new ArrayList<>();
		this.engine = engine;
		this.aggregationLogs= new ConcurrentHashMap<>();
	}



	public synchronized void register(ConsummerThread<D,S> consummerThreadLog) {
		consummerThreads.add(consummerThreadLog);
	}

	public synchronized void unregister(ConsummerThread<D,S> consummerThreadLog) {
		consummerThreads.remove(consummerThreadLog);
	}

	@Override
	public void run() {

				
		while (!Thread.currentThread().isInterrupted()) {
		
		waitForNextPeriod();

		LOGGER.log(Level.FINEST, "Begining to flush and write aggregating datas");

		LOGGER.log(Level.FINEST, "Notify consummers threads : claim theirs datas");
		notifyAnwWaitNewLogs();

		LOGGER.log(Level.FINEST, "Datas consolidation done, datas will be written and flush");

		Map<String, S> aggregationLogsOld = this.aggregationLogs;
				
		this.aggregationLogs= new ConcurrentHashMap<>();

		engine.getAggregatorWriter().write(aggregationLogsOld);


		}

	}

	
	private void notifyAnwWaitNewLogs()
	{

		for (ConsummerThread<D,S> thread : consummerThreads) {
			thread.notifyMustRefreshStatRepoRef();
		}

		boolean allThreadHaveSwitchedToNewRepo = false ;

		while(!allThreadHaveSwitchedToNewRepo)
		{
			allThreadHaveSwitchedToNewRepo =true ;
			
			for (ConsummerThread<D, S> consummerThread : consummerThreads) {
				
				if(consummerThread.isMustRefreshStatRepoRef())
					allThreadHaveSwitchedToNewRepo=false;
			}
			
			
		
		}
	}

	private void waitForNextPeriod()
	{
		try {
			Thread.sleep(1000* engine.getConfig().getAggragationPeriodictyInSeconds());
		} catch (InterruptedException e) {
			//Do nothing
		}
	}



	public Map<String, S> getAggregationLogs() {
		return aggregationLogs;
	}


}
