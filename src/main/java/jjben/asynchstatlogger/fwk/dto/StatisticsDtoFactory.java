package jjben.asynchstatlogger.fwk.dto;

@FunctionalInterface
public interface StatisticsDtoFactory<D extends DataDto> {

    public StatisticsDto<D> make(String key);

}
