package jjben.actor;

import jjben.dto.OperationDto;
import jjben.share.AsynchronousLogger;

public class ProducerThreadLog implements Runnable {

	private final AsynchronousLogger logQueue;

		
	public ProducerThreadLog(AsynchronousLogger logQueue) {
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
			
			
			OperationDto operationLog = new OperationDto(Thread.currentThread().getName(), true, fin-deb);
			logQueue.log(operationLog);
			


			}
	
		}

}
