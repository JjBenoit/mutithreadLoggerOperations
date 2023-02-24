package jjben.dto;

import jjben.dto.interfaces.StatisticsDto;

public class OperationStatisticsDto extends StatisticsDto<OperationDto,OperationStatisticsDto> {

	private String name;
	private long count;
	private long timeTaken;
	private long nbOK;
	
	public OperationStatisticsDto(String name) {

		super(name);
		this.name=name;

	}

	public void addData(OperationDto operation)
	{
		this.count++;
		this.timeTaken+=operation.timeTaken;
		nbOK += operation.ok ? 1:0;
	}
	
	public void mergeStats(OperationStatisticsDto operations)
	{
		this.count+=operations.count;
		this.timeTaken+=operations.timeTaken;
		nbOK += operations.nbOK;
	}


	private long getMoyenne() {
		return timeTaken/count;
	}

	
	private long getNbKo() {
		return count-nbOK;
	}

	@Override
	public String toString() {
		return "OperationStatisticsDto [name=" + name + ", count=" + count + ", timeTaken=" + timeTaken + ", nbOK="
				+ nbOK + ", getMoyenne()=" + getMoyenne() + ", getNbKo()=" + getNbKo() + "]";
	}




}
