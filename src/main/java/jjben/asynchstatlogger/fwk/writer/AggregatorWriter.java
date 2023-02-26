package jjben.asynchstatlogger.fwk.writer;

import java.util.Collection;

import jjben.asynchstatlogger.fwk.dto.DataDto;
import jjben.asynchstatlogger.fwk.dto.StatisticsDto;

@FunctionalInterface
public interface AggregatorWriter {

    public <D extends DataDto> void write(Collection<StatisticsDto<D>> collection);

}
