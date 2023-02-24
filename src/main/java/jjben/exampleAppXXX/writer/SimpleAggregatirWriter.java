package jjben.exampleAppXXX.writer;

import java.util.Map;

import jjben.asynchstatlogger.fwk.dto.StatisticsDto;
import jjben.asynchstatlogger.fwk.writer.AggregatorWriter;
import jjben.exampleAppXXX.dto.OperationDto;

public class SimpleAggregatirWriter implements AggregatorWriter<OperationDto>
{
	@Override
	public void write(Map<String,StatisticsDto<OperationDto>> infos) {

		System.out.println("New Periode");

		for (StatisticsDto<OperationDto> operationDto : infos.values()) {

				System.out.println(operationDto.print());

			}

	}

}
