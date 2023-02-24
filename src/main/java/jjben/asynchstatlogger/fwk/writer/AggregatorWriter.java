package jjben.asynchstatlogger.fwk.writer;

import java.util.Map;

import jjben.asynchstatlogger.fwk.dto.DataDto;
import jjben.asynchstatlogger.fwk.dto.StatisticsDto;

@FunctionalInterface
public interface AggregatorWriter<D extends DataDto, S extends  StatisticsDto<D, S> > {

	public void write(Map<String,S> infos);

}
