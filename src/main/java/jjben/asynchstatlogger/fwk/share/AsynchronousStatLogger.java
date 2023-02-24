package jjben.asynchstatlogger.fwk.share;

import java.io.IOException;

import jjben.asynchstatlogger.fwk.actor.AsynchronousStatEngine;
import jjben.asynchstatlogger.fwk.dto.DataDto;
import jjben.asynchstatlogger.fwk.dto.StatisticsDto;
import jjben.asynchstatlogger.fwk.dto.StatisticsDtoFactory;
import jjben.asynchstatlogger.fwk.writer.AggregatorWriter;

public class AsynchronousStatLogger<D extends DataDto, S extends  StatisticsDto<D, S> > {

	private final AsynchronousStatEngine<D, S> asynchronousStatEngine ;


	public AsynchronousStatLogger(AggregatorWriter<D,S> aggregatorWriter, StatisticsDtoFactory<D, S> factory) {
		
		asynchronousStatEngine = new AsynchronousStatEngine<>(aggregatorWriter, factory);
	}


	public void log(D statDto) {
		asynchronousStatEngine.log(statDto);
	}
	public void startLogger() throws IOException  {
		
		asynchronousStatEngine.startLogger();
	}

	public void stopLogger()  {
		asynchronousStatEngine.stopLogger();
	}



}
