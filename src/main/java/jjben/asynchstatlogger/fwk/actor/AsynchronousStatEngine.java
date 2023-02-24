package jjben.asynchstatlogger.fwk.actor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

import jjben.asynchstatlogger.fwk.configuration.Configuration;
import jjben.asynchstatlogger.fwk.dto.DataDto;
import jjben.asynchstatlogger.fwk.dto.StatisticsDto;
import jjben.asynchstatlogger.fwk.dto.StatisticsDtoFactory;
import jjben.asynchstatlogger.fwk.writer.AggregatorWriter;

public class AsynchronousStatEngine<D extends DataDto, S extends  StatisticsDto<D, S> > {

	private static final Logger LOGGER = Logger.getLogger(AsynchronousStatEngine.class.getName());
	
	private final Configuration config;

	private final ConcurrentLinkedDeque<D> queue;

	private StatAggregatorThread<D,S> statAggregator;
	
	private AggregatorWriter<D,S> aggregatorWriter;

	private StatisticsDtoFactory<D, S> factory;

	private final List<Thread> threads;

	private State state;
	
	private enum State{

		NOT_INILIAZIED, RUNNING, STOPPED,
	}


	public AsynchronousStatEngine(AggregatorWriter<D,S> aggregatorWriter, StatisticsDtoFactory<D, S> factory) {

		this.config= new Configuration("configuration.properties");
		this.aggregatorWriter= aggregatorWriter;
		this.statAggregator = new StatAggregatorThread<D, S>(this);
		this.queue = new ConcurrentLinkedDeque<>();
		this.threads = new ArrayList<>();
		this.factory= factory;
		this.state= State.NOT_INILIAZIED;
	}

	private void init() throws IOException  {
		
		this.config.initConfig();

		 for (int i = 0; i < config.getNbThreadConsummer(); i++) {
			 threads.add( new Thread(new ConsummerThread<D, S>(this),"ConsummerStatThread "+i));
		 }

		 threads.add(new Thread(statAggregator));

	}


	public void log(D statDto) {

		if(state!=State.RUNNING)
			throw new IllegalStateException("AsynchronousStatLogger must be started before logging");

		boolean successPush = queue.offer(statDto);

		//log pour connaitre les echecs en cours de fort débit
		if(!successPush)
			LOGGER.info(Thread.currentThread().getName()+" Queue full log opearation "+statDto);
	}



	public void startLogger() throws IOException  {

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
	

	public Configuration getConfig() {
		return config;
	}

	public ConcurrentLinkedDeque<D> getQueue() {
		return queue;
	}

	public StatAggregatorThread<D, S> getStatAggregator() {
		return statAggregator;
	}

	public AggregatorWriter<D, S> getAggregatorWriter() {
		return aggregatorWriter;
	}

	public StatisticsDtoFactory<D, S> getFactory() {
		return factory;
	}

	public State getState() {
		return state;
	}
	
	

}
