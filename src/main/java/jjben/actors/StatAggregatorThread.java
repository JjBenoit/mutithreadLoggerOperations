package jjben.actors;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jjben.dto.interfaces.DataDto;
import jjben.dto.interfaces.StatisticsDto;
import jjben.writers.AggregatorWriter;

public class StatAggregatorThread<D extends DataDto, S extends  StatisticsDto<D, S> > implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(StatAggregatorThread.class.getName());  

	private List<ConsummerThread<D,S>> consummerThreads;
	private List<Map<String,S>> aggregationLogs;
	private int aggragationPeriodictyInSeconds;
	private AggregatorWriter<D,S> aggregatorWriter;
	
	public StatAggregatorThread(int aggragationPeriodictyInSeconds, AggregatorWriter<D,S> aggregatorWriter) {
		this.aggragationPeriodictyInSeconds =  aggragationPeriodictyInSeconds;
		this.consummerThreads = new ArrayList<>();
		this.aggregatorWriter = aggregatorWriter;
	}
	
	

	public synchronized void register(ConsummerThread<D,S> consummerThreadLog) {
		consummerThreads.add(consummerThreadLog);
	}
	
	public synchronized void unregister(ConsummerThread<D,S> consummerThreadLog) {
		consummerThreads.remove(consummerThreadLog);
	}
	
	public synchronized void receiveStats(Map<String,S> stats) {
		aggregationLogs.add(stats);
	}
	

	public void run() {
		
		while (!Thread.currentThread().isInterrupted()) {
					
		waitForNextPeriod();
		
		LOGGER.log(Level.FINEST, "Begining to flush and write aggregating datas");
		
		LOGGER.log(Level.FINEST, "notify consummers threads to send theirs datas");
		notifyAnwWaitNewLogs();
		
		LOGGER.log(Level.FINEST, "Datas from consummers threads received");
		Map<String,S> logsConsolided =  consolidationLogs();
	
		LOGGER.log(Level.FINEST, "Datas consolidation done, datas will be write and flush");
		
		aggregatorWriter.write(logsConsolided);
		
		
		}
	
	}
	
	private Map<String,S> consolidationLogs()
	{
		Map<String,S> logsConsolided = new HashMap<>();

		for (Map<String,S> logsPart : aggregationLogs) {
			
			
			//merge all the same logs ( same operation name ) sended by consummer threads
			for (String key : logsPart.keySet()) {
				
				S log = logsConsolided.get(key);
				
				if(log !=null)
				{
					log.mergeStats(logsPart.get(key));
				}
				else
				{
					log=logsPart.get(key);
				}
				
				logsConsolided.put(key, log);
			}
		}
		return logsConsolided;
	}
	
	private void notifyAnwWaitNewLogs()
	{
		aggregationLogs = new ArrayList<>();

		
		for (ConsummerThread<D,S> thread : consummerThreads) {
			thread.ask4NewLogs();
		}
		
		while(aggregationLogs.size()<consummerThreads.size())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				//Do nothing
			}
		}
	}
	
	private void waitForNextPeriod()
	{
		try {
			Thread.sleep(1000*aggragationPeriodictyInSeconds);
		} catch (InterruptedException e) {
			//Do nothing
		}
	}
	

}
