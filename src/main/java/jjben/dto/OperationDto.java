package jjben.dto;

import java.util.UUID;

public class OperationDto {

	public UUID uid;
	public String name;
	public long timeTaken;
	boolean ok;
	
	public OperationDto(String name, boolean ok, long timeTaken)
	{
		this.name = name;
		this.timeTaken=timeTaken;
		this.ok = ok;
		this.uid= UUID.randomUUID();
	}

	@Override
	public String toString() {
		return "OperationDto [uid=" + uid + ", name=" + name + ", timeTaken=" + timeTaken + ", ok=" + ok + "]";
	}
	

	
	
}
