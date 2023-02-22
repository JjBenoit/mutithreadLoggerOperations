package jjben.actor;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jjben.dto.OperationStatisticsDto;

public class LogAggregatorThread implements Runnable{

	private List<ConsummerThread> consummerThreads;
	private List<Map<String,OperationStatisticsDto>> aggregationLogs;
	private int aggragationPeriodictyInSeconds;
	
	public LogAggregatorThread(int aggragationPeriodictyInSeconds) {
		this.aggragationPeriodictyInSeconds =  aggragationPeriodictyInSeconds;
		this.consummerThreads = new ArrayList<ConsummerThread>();
	}
	
	

	public synchronized void register(ConsummerThread consummerThreadLog) {
		consummerThreads.add(consummerThreadLog);
	}
	
	public synchronized void unregister(ConsummerThread consummerThreadLog) {
		consummerThreads.remove(consummerThreadLog);
	}
	
	public synchronized void receiveStats(Map<String,OperationStatisticsDto> stats) {
		aggregationLogs.add(stats);
	}
	

	public void run() {
		
		while (!Thread.currentThread().isInterrupted()) {
					
		waitForNextPeriod();
		
		notifyAnwWaitNewLogs();
				
		Map<String,OperationStatisticsDto> logsConsolided =  consolidationLogs();
	
		printLog(logsConsolided);
		
		}
	
	}
	
	private Map<String,OperationStatisticsDto> consolidationLogs()
	{
		Map<String,OperationStatisticsDto> logsConsolided = new HashMap<>();

		for (Map<String,OperationStatisticsDto> logsPart : aggregationLogs) {
			
			
			//merge all the same logs ( same operation name ) sended by consummer threads
			for (String operationName : logsPart.keySet()) {
				
				OperationStatisticsDto log = logsConsolided.get(operationName);
				
				if(log !=null)
				{
					log.mergeStatsInfos(logsPart.get(operationName));
				}
				else
				{
					log=logsPart.get(operationName);
				}
				
				logsConsolided.put(operationName, log);
			}
		}
		return logsConsolided;
	}
	
	private void notifyAnwWaitNewLogs()
	{
		aggregationLogs = new ArrayList<>();

		
		for (ConsummerThread thread : consummerThreads) {
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
	
	private void printLog(Map<String,OperationStatisticsDto> statsConsolided) {
		
		System.out.println("New Periode");

		for (OperationStatisticsDto operationDto : statsConsolided.values()) {
				
				System.out.println(operationDto.toString());
	
			}
		
	}
	
}
