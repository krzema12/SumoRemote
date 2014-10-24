package eu.mcft.sumoremote.commands;

public class Command
{
	private long id;
	private String name;
	private int address;
	private int command;
	
	public Command(long id, String name, int address, int command)
	{
		this.id = id;
		this.name = name;
		this.address = address;
		this.command = command;
	}
	
	public Command(String name, int address, int command)
	{
		this.name = name;
		this.address = address;
		this.command = command;
	}

	public Command()
	{

	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public int getAddress()
	{
		return address;
	}
	
	public void setAddress(int address)
	{
		this.address = address;
	}
	
	public int getCommand()
	{
		return command;
	}
	
	public void setCommand(int command)
	{
		this.command = command;
	}
}
