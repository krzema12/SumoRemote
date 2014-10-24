package eu.mcft.sumoremote.senders;

import java.lang.reflect.Method;

import android.content.Context;

public class SamsungRC5Sender extends IRSender
{
	Object irdaService;
	Method irWrite;
	static final int FREQUENCY = 38028;
	
	boolean currentState;
	boolean firstBit;
	StringBuilder rc5Frame;
	
	private static final int CYCLES_IN_BURST = 32;
	
	public SamsungRC5Sender(Context context) throws NoSuchMethodException
	{
		irdaService = context.getSystemService("irda");
		
		Class<? extends Object> c = irdaService.getClass();
		Class<?> p[] = { String.class };

		try
		{
			irWrite = c.getMethod("write_irsend", p);
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
	}

	public void SendCommand(int data)
	{
		rc5Frame = new StringBuilder();
		
		// frequency in Hz
		rc5Frame.append(Integer.toString(FREQUENCY) + ",");
		
		currentState = true;
		
		addOneBit(); // start bits (two at once, sic!)
		addZeroBit(); // toggle bit
		
		// sending the actual data
		for(int s=0x400; s!=0; s>>=1)
		{
			if((data&s) != 0)
				addOneBit();
			else
				addZeroBit();
		}
		
		// finishing the sequence
		flush();
		
		// sending via IR
		try
		{
			irWrite.invoke(irdaService, rc5Frame.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void addZeroBit()
	{
		if(currentState == true)
			rc5Frame.append(Integer.toString(CYCLES_IN_BURST*2) + ",");
		else
		{
			rc5Frame.append(Integer.toString(CYCLES_IN_BURST) + ",");
			rc5Frame.append(Integer.toString(CYCLES_IN_BURST) + ",");
		}
		
		currentState = false;
	}
	
	private void addOneBit()
	{
		if(currentState == false)
			rc5Frame.append(Integer.toString(CYCLES_IN_BURST*2) + ",");
		else
		{
			rc5Frame.append(Integer.toString(CYCLES_IN_BURST) + ",");
			rc5Frame.append(Integer.toString(CYCLES_IN_BURST) + ",");
		}
		
		currentState = true;
	}
	
	private void flush()
	{
		if(currentState == true)
			rc5Frame.append(Integer.toString(CYCLES_IN_BURST) + ",");
	}

}
