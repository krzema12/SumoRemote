package eu.mcft.sumoremote;

import eu.mcft.sumoremote.R;
import eu.mcft.sumoremote.RC5Sender;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, TextWatcher
{
	Button programButton;
	Button startButton;
	Button stopButton;
	
	EditText address;
	
	RC5Sender irSender;
	
	boolean doubleBackToExitPressedOnce = false;
	
	private final static int PROGRAMMING_ADDRESS  = 0x0B;
	private final static int STARTING_STOPPING_ADDRESS = 0x07;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		programButton = (Button)findViewById(R.id.programButton);
		startButton = (Button)findViewById(R.id.startButton);
		stopButton = (Button)findViewById(R.id.stopButton);
		
		address = (EditText)findViewById(R.id.address);
		
		try
		{
			irSender = new RC5Sender(this.getSystemService("irda"), 38028);
		}
		catch(Exception e)
		{
			programButton.setEnabled(false);
			startButton.setEnabled(false);
			stopButton.setEnabled(false);
			address.setEnabled(false);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.ir_not_detected_title)
				.setCancelable(false)
				.setMessage(R.string.ir_not_detected_full)
				.setNegativeButton(R.string.ir_not_detected_button, new DialogInterface.OnClickListener()
				{
		            public void onClick(DialogInterface dialog, int id)
		            {
		                dialog.cancel();
		            }
		        });
			
			AlertDialog alert = builder.create();
			alert.show();
		}
		
		programButton.setOnClickListener(this);
		startButton.setOnClickListener(this);
		stopButton.setOnClickListener(this);
		
		address.addTextChangedListener(this);
		
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		address.setText(Integer.toString(sharedPref.getInt(getString(R.string.address_preference), 0)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_about:
				Intent intent = new Intent(this, AboutActivity.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	// how startmodule remotes work
	// http://www.startmodule.com/implement-yourself/

	@Override
	public void onClick(View v)
	{
		int dohyoAddress = Integer.parseInt(address.getText().toString());
		
		if(v == programButton)
		{
			irSender.SendCommand(PROGRAMMING_ADDRESS, dohyoAddress<<1);
		}
		else if(v == startButton)
		{
			irSender.SendCommand(STARTING_STOPPING_ADDRESS, (dohyoAddress<<1)|1);
		}
		else if(v == stopButton)
		{
			irSender.SendCommand(STARTING_STOPPING_ADDRESS, (dohyoAddress<<1)|0);
		}
	}
	
	@Override
    public void onBackPressed()
	{
        if (doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            return;
        }
        
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.double_back_toast, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
            	doubleBackToExitPressedOnce = false;   
            }
        }, 2000);
    }

	@Override
	public void afterTextChanged(Editable textEdit)
	{
		try
		{
			int addressValue = Integer.parseInt(address.getText().toString());
			SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

			if(addressValue > 31)
			{
				address.setText(Integer.toString(sharedPref.getInt(getString(R.string.address_preference), 0)));
				address.setSelection(address.getText().length());
			}
			else
			{		
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putInt(getString(R.string.address_preference), addressValue);
				editor.commit();
			}
		}
		catch(NumberFormatException nfe) { }
	}

	@Override
	public void beforeTextChanged(CharSequence prev, int arg1, int arg2, int arg3) { }
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { } 
}
