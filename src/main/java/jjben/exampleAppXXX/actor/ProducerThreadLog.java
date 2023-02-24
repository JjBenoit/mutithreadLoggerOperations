package jjben.exampleAppXXX.actor;

import jjben.asynchstatlogger.fwk.share.AsynchronousStatLogger;
import jjben.exampleAppXXX.dto.OperationDto;
import jjben.exampleAppXXX.dto.OperationStatisticsDto;

public class ProducerThreadLog implements Runnable {

	private final AsynchronousStatLogger<OperationDto,OperationStatisticsDto> logQueue;


	public ProducerThreadLog(AsynchronousStatLogger<OperationDto,OperationStatisticsDto> logQueue) {
		super();
		this.logQueue = logQueue;
	}


	@Override
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
