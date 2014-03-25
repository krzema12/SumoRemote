package eu.mcft.sumoremote;

import eu.mcft.sumoremote.R;
import eu.mcft.sumoremote.RC5Sender;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.Theme;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, TextWatcher, OnTouchListener
{
	Button programButton;
	Button startButton;
	Button stopButton;
	Button tacticsButton;
	
	EditText address;
	
	RC5Sender irSender;
	
	boolean doubleBackToExitPressedOnce = false;
	int addressValue = 0;
	
	private final static int PROGRAMMING_ADDRESS  = 0x0B;
	private final static int STARTING_STOPPING_ADDRESS = 0x07;
	private final static int TACTICS_ADDRESS = 0x0D;
	
	SharedPreferences sharedPref;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		if(sharedPref.getBoolean("theme", false) == true)
			setTheme(android.R.style.Theme_Holo);
		else
			setTheme(android.R.style.Theme_Holo_Light);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		programButton = (Button)findViewById(R.id.programButton);
		startButton = (Button)findViewById(R.id.startButton);
		stopButton = (Button)findViewById(R.id.stopButton);
		tacticsButton = (Button)findViewById(R.id.tacticsButton);
		
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
		stopButton.setOnTouchListener(this);	// "onTouch" to handle long pressing
		tacticsButton.setOnClickListener(this);
		
		address.addTextChangedListener(this);
		
		address.setText(Integer.toString(addressValue = sharedPref.getInt(getString(R.string.address_preference), 0)));
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
		Intent intent;
		
		switch (item.getItemId())
		{
			case R.id.action_about:
				intent = new Intent(this, AboutActivity.class);
				startActivity(intent);
				return true;
			case R.id.action_preferences:
				intent = new Intent(MainActivity.this, SetPreferenceActivity.class);
				startActivityForResult(intent, 0);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 0)
		{
			if(sharedPref.getBoolean("theme", false) == true)
				setTheme(android.R.style.Theme_Holo);
			else
				setTheme(android.R.style.Theme_Holo_Light);

			finish();
		    Intent intent = new Intent(this, MainActivity.class);
		    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(intent);
		}
	}
	
	// how startmodule remotes work
	// http://www.startmodule.com/implement-yourself/

	@Override
	public void onClick(View v)
	{		
		if(v == programButton)
			irSender.SendCommand(PROGRAMMING_ADDRESS, addressValue<<1);
		else if(v == startButton)
			irSender.SendCommand(STARTING_STOPPING_ADDRESS, (addressValue<<1)|1);
		else if(v == tacticsButton)
			irSender.SendCommand(TACTICS_ADDRESS, addressValue);
	}
	
	private Handler repeatHandler;

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if(v == stopButton)
		{
			switch(event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					if (repeatHandler != null)
						return true;
					
					repeatHandler = new Handler();
					repeatHandler.postDelayed(sendStop, 0);
					break;
				case MotionEvent.ACTION_UP:
					if (repeatHandler == null)
						return true;
					
					repeatHandler.removeCallbacks(sendStop);
					repeatHandler = null;
					break;
			}
		}

		return false;
	}
	
	Runnable sendStop = new Runnable()
	{
        @Override public void run()
        {
        	irSender.SendCommand(STARTING_STOPPING_ADDRESS, addressValue<<1);
            repeatHandler.postDelayed(this, 100);
        }
    };
		
	
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
			int changedAddressValue = Integer.parseInt(address.getText().toString());

			if(changedAddressValue > 31)
			{
				address.setText(Integer.toString(sharedPref.getInt(getString(R.string.address_preference), 0)));
				address.setSelection(address.getText().length());
			}
			else
			{		
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putInt(getString(R.string.address_preference), changedAddressValue);
				editor.commit();
				addressValue = changedAddressValue;
			}
		}
		catch(NumberFormatException nfe) { }
		
		if(address.getText().toString().equalsIgnoreCase(""))
		{
			programButton.setEnabled(false);
			startButton.setEnabled(false);
			stopButton.setEnabled(false);
		}
		else
		{
			programButton.setEnabled(true);
			startButton.setEnabled(true);
			stopButton.setEnabled(true);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence prev, int arg1, int arg2, int arg3) { }
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
}
