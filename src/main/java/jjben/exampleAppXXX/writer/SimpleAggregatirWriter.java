package jjben.exampleAppXXX.writer;

import java.util.Collection;

import jjben.asynchstatlogger.fwk.dto.DataDto;
import jjben.asynchstatlogger.fwk.dto.StatisticsDto;
import jjben.asynchstatlogger.fwk.writer.AggregatorWriter;

public class SimpleAggregatirWriter implements AggregatorWriter {

    @Override
    public <D extends DataDto> void write(Collection<StatisticsDto<D>> collection) {

	System.out.println("New Periode");

	for (StatisticsDto<D> operationDto : collection) {

	    System.out.println(operationDto.print());

	}
    }

}
