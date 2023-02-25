package jjben.exampleAppXXX;

import java.io.IOException;

import jjben.asynchstatlogger.fwk.share.AsynchronousStatLogger;
import jjben.exampleAppXXX.actor.ProducerThreadLog;
import jjben.exampleAppXXX.dto.OperationDto;
import jjben.exampleAppXXX.dto.OperationStatisticsDto;
import jjben.exampleAppXXX.dto.OperationStatisticsDtoFactory;
import jjben.exampleAppXXX.writer.SimpleAggregatirWriter;

public class Launcher {

    public static void main(String[] args) throws InterruptedException, IOException {

	Launcher launcher = new Launcher();
	launcher.start();
    }

    public void start() throws InterruptedException, IOException {

	AsynchronousStatLogger<OperationDto, OperationStatisticsDto> queue = new AsynchronousStatLogger<OperationDto, OperationStatisticsDto>(
		new SimpleAggregatirWriter(), new OperationStatisticsDtoFactory());

	queue.startLogger();

	for (int i = 0; i < 100; i++) {
	    new Thread(new ProducerThreadLog(queue), "ProducerThreadLog " + i).start();
	}

    }

}
