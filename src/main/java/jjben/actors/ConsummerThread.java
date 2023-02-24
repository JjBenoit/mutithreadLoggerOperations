package jjben.actors;


import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jjben.dto.interfaces.DataDto;
import jjben.dto.interfaces.StatisticsDto;
import jjben.dto.interfaces.StatisticsDtoFactory;

public class ConsummerThread<D extends DataDto, S extends  StatisticsDto<D, S> > implements Runnable{
	
	private static final Logger LOGGER = Logger.getLogger(ConsummerThread.class.getName());  

	private final Queue<D> queue;
	private final StatAggregatorThread<D,S> collector;
	private ConcurrentHashMap<String,S> statisticsRepository;
	private StatisticsDtoFactory<D, S> factory;

	private boolean logsAskedFromAgregator;

	
	public ConsummerThread(Queue<D> queue,StatAggregatorThread<D,S> collector, StatisticsDtoFactory<D, S> factory) {
		this.queue = queue;
		this.statisticsRepository = new ConcurrentHashMap<>();
		this.collector=collector;
		this.factory=factory;
		collector.register(this);
 	}
	
	
	public void run()
	{

		while(!Thread.currentThread().isInterrupted()) {
			
			D statDto = queue.poll();
			if(statDto!=null)
			{
				S statisticsDto = statisticsRepository.get(statDto.getKey());
				if(statisticsDto == null)
				{
					statisticsDto = factory.make(statDto.getKey());
					statisticsRepository.put(statDto.getKey(), statisticsDto);
				}
				
				statisticsDto.addData(statDto);
				
				if(logsAskedFromAgregator)
				{
					logsAskedFromAgregator = false;
					
					collector.receiveStats(statisticsRepository);
					LOGGER.log(Level.FINEST, "logs sended to Agregator from "+Thread.currentThread().getName());

					this.statisticsRepository = new ConcurrentHashMap<String,S>();
					LOGGER.log(Level.FINEST, "new map created by "+Thread.currentThread().getName());


				}
			}
	
		}
		
		collector.unregister(this);

	}
	
	// use by agregator to query statistics from consummer thread
	public void ask4NewLogs()
	{
		LOGGER.log(Level.FINEST, "logs Asked From Agregator");
		logsAskedFromAgregator=true;
	}
	
	
}
