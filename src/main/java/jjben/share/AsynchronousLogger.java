package jjben.share;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import jjben.actor.ConsummerThread;
import jjben.actor.LogAggregatorThread;
import jjben.dto.OperationDto;

public class AsynchronousLogger {
	
	
	private final ConcurrentLinkedDeque<OperationDto> queue;
	
	private LogAggregatorThread logsAggregator;
	
	private final List<Thread> threads;
	
	private int nbThreadConsummer;
	
	private LoggerState state;
	
	public AsynchronousLogger(int nbThreadConsummer, int aggragationPeriodictyInSeconds) {
		
		queue = new ConcurrentLinkedDeque<OperationDto>();
		threads = new ArrayList<>(nbThreadConsummer);
		logsAggregator = new LogAggregatorThread(aggragationPeriodictyInSeconds);
		this.nbThreadConsummer = nbThreadConsummer;
		state= LoggerState.NOT_INILIAZIED;
	}
	
	private void init()  {
		 
		 for (int i = 0; i < nbThreadConsummer; i++) {
			 threads.add( new Thread(new ConsummerThread(queue,logsAggregator),"ConsummerThreadLog "+i));
		 }
		 
		 threads.add(new Thread(logsAggregator));
		 
	}
	

	public void log(OperationDto operationDto) {
		
		if(state!=LoggerState.RUNNING)
			throw new IllegalStateException("Logger must be started before logging");
		
		boolean successPush = queue.offer(operationDto);
		
		//log pour connaitre les echecs en cours de fort débit
		if(!successPush)
			System.out.print(Thread.currentThread().getName()+" Queue full pour log opearation "+operationDto);
	}
	

	
	public void startLogger()  {
		
		if(state==LoggerState.STOPPED)
			throw new IllegalStateException("Logger can not be start after being stopped");
		
		init() ;
		
		for (Thread thread : threads) {
			thread.start();
		}
		
		state= LoggerState.RUNNING;
	}
	
	public void stopLogger()  {	
		
		if(state!=LoggerState.RUNNING)
			throw new IllegalStateException("Logger can not be stoped, if it is already stopped or not initialized");
		
		state= LoggerState.STOPPED;

		for (Thread thread : threads) {
			thread.interrupt();
		}
	
	}
	
	enum LoggerState{
		
		NOT_INILIAZIED, RUNNING,STOPPED,
	}
	
}
