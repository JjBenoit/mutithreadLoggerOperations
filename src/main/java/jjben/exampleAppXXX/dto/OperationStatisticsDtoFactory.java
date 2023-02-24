package jjben.exampleAppXXX.dto;

import jjben.asynchstatlogger.fwk.dto.StatisticsDtoFactory;

public class OperationStatisticsDtoFactory implements StatisticsDtoFactory<OperationDto> {

	public OperationStatisticsDto make(String key)
	{
		return new OperationStatisticsDto(key);
	}


}
