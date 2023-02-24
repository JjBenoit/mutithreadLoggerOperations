package jjben.exampleAppXXX.writer;

import java.util.Map;

import jjben.asynchstatlogger.fwk.dto.StatisticsDto;
import jjben.asynchstatlogger.fwk.writer.AggregatorWriter;
import jjben.exampleAppXXX.dto.OperationDto;
import jjben.exampleAppXXX.dto.OperationStatisticsDto;

public class SimpleAggregatirWriter implements AggregatorWriter<OperationDto,OperationStatisticsDto>
{

	public void write(Map<String,OperationStatisticsDto> infos) {

		System.out.println("New Periode");

		for (StatisticsDto<OperationDto,OperationStatisticsDto> operationDto : infos.values()) {

				System.out.println(operationDto.print());

			}

	}

}
