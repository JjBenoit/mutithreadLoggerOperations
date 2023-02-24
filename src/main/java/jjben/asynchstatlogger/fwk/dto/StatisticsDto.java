package jjben.asynchstatlogger.fwk.dto;

public abstract class StatisticsDto<D extends DataDto>  {

	private String key;

	public StatisticsDto(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public abstract void addData(D data);

	public abstract String print();





}
