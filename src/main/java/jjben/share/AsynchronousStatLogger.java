package jjben.share;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

import jjben.actors.ConsummerThread;
import jjben.actors.StatAggregatorThread;
import jjben.dto.interfaces.DataDto;
import jjben.dto.interfaces.StatisticsDto;
import jjben.dto.interfaces.StatisticsDtoFactory;
import jjben.writers.AggregatorWriter;

public class AsynchronousStatLogger<D extends DataDto, S extends  StatisticsDto<D, S> > {
	
	private static final Logger LOGGER = Logger.getLogger(AsynchronousStatLogger.class.getName());  
	   
	private final ConcurrentLinkedDeque<D> queue;
	
	private StatAggregatorThread<D,S> statAggregator;
	
	private StatisticsDtoFactory<D, S> factory;
	
	private final List<Thread> threads;
	
	private int nbThreadConsummer;
	
	private State state;
	
	
	public AsynchronousStatLogger(int nbThreadConsummer, int aggragationPeriodictyInSeconds,  AggregatorWriter<D,S> aggregatorWriter, StatisticsDtoFactory<D, S> factory) {
		
		queue = new ConcurrentLinkedDeque<>();
		threads = new ArrayList<>(nbThreadConsummer);
		statAggregator = new StatAggregatorThread<D, S>(aggragationPeriodictyInSeconds,aggregatorWriter);
		this.nbThreadConsummer = nbThreadConsummer;
		this.factory= factory;
		state= State.NOT_INILIAZIED;
	}
	
	private void init()  {
		 
		 for (int i = 0; i < nbThreadConsummer; i++) {
			 threads.add( new Thread(new ConsummerThread<D, S>(queue,statAggregator,factory),"ConsummerStatThread "+i));
		 }
		 
		 threads.add(new Thread(statAggregator));
		 
	}
	

	public void log(D statDto) {
		
		if(state!=State.RUNNING)
			throw new IllegalStateException("AsynchronousStatLogger must be started before logging");
		
		boolean successPush = queue.offer(statDto);
		
		//log pour connaitre les echecs en cours de fort débit
		if(!successPush)
			LOGGER.info(Thread.currentThread().getName()+" Queue full pour log opearation "+statDto);
	}
	

	
	public void startLogger()  {
		
		if(state==State.STOPPED)
			throw new IllegalStateException("AsynchronousStatLogger can not be start after being stopped");
		
		init() ;
		
		for (Thread thread : threads) {
			thread.start();
		}
		
		state= State.RUNNING;
	}
	
	public void stopLogger()  {	
		
		if(state!=State.RUNNING)
			throw new IllegalStateException("AsynchronousStatLogger can not be stoped, if it is already stopped or not initialized");
		
		state= State.STOPPED;

		for (Thread thread : threads) {
			thread.interrupt();
		}
	
	}
	
	enum State{
		
		NOT_INILIAZIED, RUNNING, STOPPED,
	}
	
}
