package jjben.exampleAppXXX.dto;

import jjben.asynchstatlogger.fwk.dto.StatisticsDto;

public class OperationStatisticsDto extends StatisticsDto<OperationDto> {

	private String name;
	private long count;
	private long timeTaken;
	private long nbOK;

	public OperationStatisticsDto(String name) {

		super(name);
		this.name=name;

	}
	
	@Override
	public void addData(OperationDto operation)
	{
		this.count++;
		this.timeTaken+=operation.timeTaken;
		nbOK += operation.ok ? 1:0;
	}


	private long getMoyenne() {
		return timeTaken/count;
	}


	private long getNbKo() {
		return count-nbOK;
	}

	
	@Override
	public String print() {
		return this.toString();
	}
	
	@Override
	public String toString() {
		return "OperationStatisticsDto [name=" + name + ", count=" + count + ", timeTaken=" + timeTaken + ", nbOK="
				+ nbOK + ", getMoyenne()=" + getMoyenne() + ", getNbKo()=" + getNbKo() + "]";
	}




}
