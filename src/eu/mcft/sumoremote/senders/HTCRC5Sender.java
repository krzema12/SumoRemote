package eu.mcft.sumoremote.senders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.htc.circontrol.CIRControl;
import com.htc.htcircontrol.HtcIrData;

public class HTCRC5Sender extends IRSender
{
	CIRControl htcIrControl;
	static final int FREQUENCY = 38000;
	List<Integer> frame = new ArrayList<Integer>();
	
	boolean currentState;
	boolean firstBit;
	
	private static final int CYCLES_IN_BURST = 32;
	
	public HTCRC5Sender(Context context) throws Exception
	{
		try
		{
			htcIrControl = new CIRControl(context, null);
			htcIrControl.start();
		}
		catch (NoClassDefFoundError ncde)
		{
			throw new Exception("No HTC Device");
		}
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
			HtcIrData ird = new HtcIrData(1, FREQUENCY, frameArray);
			htcIrControl.transmitIRCmd(ird, true);
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
		if(currentState == false)
			frame.add(CYCLES_IN_BURST);
	}

}
