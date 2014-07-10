package eu.mcft.sumoremote;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class NewCommandActivity extends Activity implements TextWatcher
{
	EditText name;
	EditText address;
	EditText command;
	Menu menu;
	
	int addressValue = 0;
	int commandValue = 0;
	
	boolean correctName = true;
	boolean correctAddress = true;
	boolean correctCommand = true;
	
	CommandDbAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		if(sharedPref.getBoolean("theme", false) == true)
			setTheme(R.style.CustomDark);
		else
			setTheme(R.style.CustomLight);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_command);
		
		name = (EditText)findViewById(R.id.name);
		name.setText(getString(R.string.command_default_name_prefix));
		address = (EditText)findViewById(R.id.address);
		command = (EditText)findViewById(R.id.command);
		
		name.addTextChangedListener(this);
		address.addTextChangedListener(this);
		command.addTextChangedListener(this);
		
		dbAdapter = new CommandDbAdapter(getApplicationContext());
		dbAdapter.open();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_command, menu);
		this.menu = menu;
		
		return true;
	}

	@Override
	public void afterTextChanged(Editable textEdit)
	{
		if (name.getText() == textEdit)
		{
			String newName = textEdit.toString();
			
			correctName = newName.length() > 0;
			name.setError(correctName == false ? getString(R.string.name_too_short) : null);

		}
		else
		{
			try
			{
				int changedValue = Integer.parseInt(textEdit.toString());
				
				if (address.getText() == textEdit)
				{
					if(changedValue > 31)
					{
						address.setText(Integer.toString(addressValue));
						address.setSelection(address.getText().length());
					}
					else
					{		
						addressValue = changedValue;
						correctAddress = true;
					}
				}
				else if (command.getText() == textEdit)
				{
					if(changedValue > 63)
					{
						command.setText(Integer.toString(commandValue));
						command.setSelection(command.getText().length());
					}
					else
					{		
						commandValue = changedValue;
						correctCommand = true;
					}
				}
			}
			catch(NumberFormatException nfe)
			{
				if (address.getText() == textEdit)
					correctAddress = false;
				else if (command.getText() == textEdit)
					correctCommand = false;
			}
		}
		
		menu.findItem(R.id.action_add_command_confirm).setVisible(correctName && correctAddress && correctCommand);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_add_command_confirm:
				dbAdapter.insertCommand(	new Command(name.getText().toString(),
												Integer.parseInt(address.getText().toString()),
												Integer.parseInt(command.getText().toString())));
				
				setResult(Activity.RESULT_OK);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence prev, int arg1, int arg2, int arg3) { }
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
	
	protected void onDestroy()
	{
		if (dbAdapter != null)
			dbAdapter.close();
		
		super.onDestroy();
	}
}
