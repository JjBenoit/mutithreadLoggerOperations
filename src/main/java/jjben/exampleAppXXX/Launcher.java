package jjben.exampleAppXXX;

import jjben.asynchstatlogger.fwk.share.AsynchronousStatLogger;
import jjben.exampleAppXXX.actor.ProducerThreadLog;
import jjben.exampleAppXXX.dto.OperationDto;
import jjben.exampleAppXXX.dto.OperationStatisticsDto;
import jjben.exampleAppXXX.dto.OperationStatisticsDtoFactory;
import jjben.exampleAppXXX.writer.SimpleAggregatirWriter;

public class Launcher {



	public static void main(String[] args) throws InterruptedException {

		Launcher launcher = new Launcher();
		launcher.start(10,1,10);
	}


	public void start(int nbThreadProducer, int nbThreadConsummer,int aggragationPeriodictyInSeconds) throws InterruptedException {

		AsynchronousStatLogger<OperationDto, OperationStatisticsDto> queue = new AsynchronousStatLogger<OperationDto, OperationStatisticsDto>
		(nbThreadConsummer,aggragationPeriodictyInSeconds, new SimpleAggregatirWriter(),new OperationStatisticsDtoFactory());

		queue.startLogger();

		for (int i = 0; i < nbThreadProducer; i++) {
			  new Thread(new ProducerThreadLog(queue), "ProducerThreadLog "+i).start();
		 }

	}






}
