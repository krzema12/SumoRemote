package eu.mcft.sumoremote.senders;

import android.content.Context;
import android.os.Build;

public abstract class IRSender
{
	public void SendCommand(int address, int command)
	{
		SendCommand(((address&0x1F)<<6)|(command&0x3F));
	}
	
	public abstract void SendCommand(int data);
	
	public static IRSender create(Context context)
	{
		IRSender face = null;
		
		// universal (KitKat and newer)
		if (face == null)
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			{
				try
				{
					face = new KitKatRC5Sender(context);
				}
				catch (Exception ex) { }
			}
		}

		// Samsung
		if (face == null)
		{
			try
			{
				face = new SamsungRC5Sender(context);
			}
			catch (Exception ex) { }
		}
		
		// LG
		if (face == null)
		{
			try
			{
				face = new LGRC5Sender(context);
			}
			catch (Exception ex) { }
		}
		
		// HTC
		if (face == null)
		{
			try
			{
				face = new HTCRC5Sender(context);
			}
			catch (Exception ex) { }
		}

		return face;
	}
}
