package eu.mcft.sumoremote;

import android.content.Context;
import android.os.Build;

public abstract class IRSender
{
	public abstract void SendCommand(int address, int command);
	public abstract void SendCommand(int data);
	
	public static IRSender create(Context context)
	{
		IRSender face = null;

		// HTC
		if (face == null)
		{
			try
			{
				face = new HTCRC5Sender(context);
			}
			catch (Exception ex){}
		}

		// Samsung
		if (face == null)
		{
			try
			{
				face = new SamsungRC5Sender(context);
			}
			catch (Exception ex){}
		}

		// universal (KitKat and newer)
		if (face == null)
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			{
				try
				{
					face = new KitKatRC5Sender(context);
				}
				catch (Exception ex){}
			}
		}

		return face;
	}
}
