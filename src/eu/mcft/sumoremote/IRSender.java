package eu.mcft.sumoremote;

public interface IRSender
{
	public void SendCommand(int address, int command);
	public void SendCommand(int data);
}
