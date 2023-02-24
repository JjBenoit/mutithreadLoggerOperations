package jjben;


import jjben.actors.ProducerThreadLog;
import jjben.dto.OperationDto;
import jjben.dto.OperationStatisticsDto;
import jjben.dto.OperationStatisticsDtoFactory;
import jjben.share.AsynchronousStatLogger;
import jjben.writers.SimpleAggregatirWriter;

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
