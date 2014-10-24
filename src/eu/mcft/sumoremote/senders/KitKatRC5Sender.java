package eu.mcft.sumoremote.senders;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.ConsumerIrManager;

@TargetApi(19)
public class KitKatRC5Sender extends IRSender
{
	private ConsumerIrManager mKkIr;
	
	static final int FREQUENCY = 38000;
	List<Integer> frame = new ArrayList<Integer>();
	
	boolean currentState;
	boolean firstBit;
	
	private static final int CYCLES_IN_BURST = 32;
	
	public KitKatRC5Sender(Context c) throws Exception
	{
		mKkIr = (ConsumerIrManager)c.getSystemService(Context.CONSUMER_IR_SERVICE);
		
		if (!mKkIr.hasIrEmitter())
			throw new Exception("No KitKat IR Device");
	}

	@Override
	public void SendCommand(int data)
	{
		frame.clear();
		
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
		
		// converting list to array
		int[] frameArray = new int[frame.size()];
		
		for(int i=0; i<frameArray.length; i++)
			frameArray[i] = frame.get(i);
		
		// sending the code
		try
		{
			mKkIr.transmit(FREQUENCY, frameArray);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void addZeroBit()
	{
		if(currentState == true)
			frame.add(CYCLES_IN_BURST*2);
		else
		{
			frame.add(CYCLES_IN_BURST);
			frame.add(CYCLES_IN_BURST);
		}
		
		currentState = false;
	}
	
	private void addOneBit()
	{
		if(currentState == false)
			frame.add(CYCLES_IN_BURST*2);
		else
		{
			frame.add(CYCLES_IN_BURST);
			frame.add(CYCLES_IN_BURST);
		}
		
		currentState = true;
	}
	
	private void flush()
	{
		if(currentState == true)
			frame.add(CYCLES_IN_BURST);
	}

}
