package jjben.actors;

import jjben.dto.OperationDto;
import jjben.dto.interfaces.DataDto;
import jjben.dto.interfaces.StatisticsDto;
import jjben.share.AsynchronousStatLogger;

public class ProducerThreadLog<D extends DataDto, S extends  StatisticsDto<D, S> > implements Runnable {

	private final AsynchronousStatLogger<D, S> logQueue;

		
	public ProducerThreadLog(AsynchronousStatLogger<D, S> logQueue) {
		super();
		this.logQueue = logQueue;
	}


	public void run()
	{
		while(!Thread.currentThread().isInterrupted()) {
		
			//do some work , whatever ....
			long deb =System.currentTimeMillis();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				//DO nothing
			}
			long fin =System.currentTimeMillis();
			
			
			D operationLog = (D) new OperationDto(Thread.currentThread().getName(), true, fin-deb);
			logQueue.log(operationLog);
			


			}
	
		}

}
