package jjben.actor;


import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import jjben.dto.OperationDto;
import jjben.dto.OperationStatisticsDto;

public class ConsummerThread implements Runnable{

	private final Queue<OperationDto> queue;
	private final LogAggregatorThread collector;
	private ConcurrentHashMap<String,OperationStatisticsDto> logsMap;
	private boolean logsAskedFromAgregator;

	
	public ConsummerThread(Queue<OperationDto> queue,LogAggregatorThread collector) {
		this.queue = queue;
		this.logsMap = new ConcurrentHashMap<String,OperationStatisticsDto>();
		this.collector=collector;
		collector.register(this);
 	}
	
	
	public void run()
	{

		while(!Thread.currentThread().isInterrupted()) {
			
			OperationDto operationDto = queue.poll();
			if(operationDto!=null)
			{
				OperationStatisticsDto operationStatisticsDto =	logsMap.get(operationDto.name);
				if(operationStatisticsDto == null)
				{
					operationStatisticsDto = new OperationStatisticsDto(operationDto.name);
					logsMap.put(operationDto.name, operationStatisticsDto);
					operationStatisticsDto = logsMap.get(operationDto.name);
				}
				
				operationStatisticsDto.addInfos(operationDto);
				
				if(logsAskedFromAgregator)
				{
					logsAskedFromAgregator = false;
					collector.receiveStats(logsMap);
					this.logsMap = new ConcurrentHashMap<String,OperationStatisticsDto>();

				}
			}
	
		}
		
		collector.unregister(this);

	}
	
	// use by agregator to query statistics from consummer thread
	public void ask4NewLogs()
	{
		logsAskedFromAgregator=true;
	}
	
	
}
