package eu.mcft.sumoremote;

import android.content.Context;

public class SharedIRSender
{
	private static  IRSender sender;
	
	public static IRSender getSender(Context context)
	{
		if (sender == null)
		{
			sender = IRSender.create(context);
		}
		
		return sender;
	}
}
