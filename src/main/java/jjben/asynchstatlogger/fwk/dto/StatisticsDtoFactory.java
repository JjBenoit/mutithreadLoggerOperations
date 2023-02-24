package jjben.asynchstatlogger.fwk.dto;

@FunctionalInterface
public interface StatisticsDtoFactory<D extends DataDto, S extends  StatisticsDto<D> > {

	public S make(String key);


}
