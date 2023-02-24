package jjben.exampleAppXXX.actor;

import jjben.asynchstatlogger.fwk.dto.DataDto;
import jjben.asynchstatlogger.fwk.dto.StatisticsDto;
import jjben.asynchstatlogger.fwk.share.AsynchronousStatLogger;
import jjben.exampleAppXXX.dto.OperationDto;

public class ProducerThreadLog<D extends DataDto, S extends  StatisticsDto<D, S> > implements Runnable {

	private final AsynchronousStatLogger<D, S> logQueue;


	public ProducerThreadLog(AsynchronousStatLogger<D, S> logQueue) {
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


			D operationLog = (D) new OperationDto(Thread.currentThread().getName(), true, fin-deb);
			logQueue.log(operationLog);



			}

		}

}
