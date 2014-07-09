package eu.mcft.sumoremote;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	
	boolean correctName = false;
	boolean correctAddress = true;
	boolean correctCommand = true;
	
	SharedPreferences sharedPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		if(sharedPref.getBoolean("theme", false) == true)
			setTheme(R.style.CustomDark);
		else
			setTheme(R.style.CustomLight);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_command);
		
		name = (EditText)findViewById(R.id.name);
		address = (EditText)findViewById(R.id.address);
		command = (EditText)findViewById(R.id.command);
		
		name.addTextChangedListener(this);
		address.addTextChangedListener(this);
		command.addTextChangedListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_command, menu);
		this.menu = menu;
		menu.findItem(R.id.action_add_command_confirm).setVisible(false);
		return true;
	}

	@Override
	public void afterTextChanged(Editable textEdit)
	{
		if (name.getText() == textEdit)
		{
			String newName = textEdit.toString();
			
			correctName = newName.length() > 0;
			correctName = correctName && (!sharedPref.contains("command_" + newName));
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
				
				String key = "command_" + name.getText();
				int value = (addressValue << 6) | commandValue;
				
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putInt(key, value);
				editor.commit();
				
				Map<String, ?> allCommands = sharedPref.getAll();
				
				for(Map.Entry<String,?> entry : allCommands.entrySet())
				{
					System.out.println(entry.getKey() + " -> " + entry.getValue().toString());
				}
				
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
}
