package jjben.asynchstatlogger.fwk.dto;

public abstract class StatisticsDto<D extends DataDto, S extends  StatisticsDto<D, S> > {

	private String key;

	public StatisticsDto(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public abstract void addData(D data);

	public abstract void mergeStats(S statistics);




}
