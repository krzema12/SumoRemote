package eu.mcft.sumoremote.commands;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import eu.mcft.sumoremote.*;
import eu.mcft.sumoremote.preferences.PrefsAdjustedActivity;

public class NewCommandActivity extends PrefsAdjustedActivity implements TextWatcher
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
	
	private CommandsDataSource dataSource;
	long commandID; // used if we're editing a command
	boolean newCommand; // true if we're creating a command, false if we're editing a command
	long existingCommandID;
	String oldName;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_command);
		
		name = (EditText)findViewById(R.id.name);
		address = (EditText)findViewById(R.id.address);
		command = (EditText)findViewById(R.id.command);

		dataSource = new CommandsDataSource(getApplicationContext());
		dataSource.open();
		
		commandID = getIntent().getLongExtra("commandID", -1);
		newCommand = commandID == -1;
		
		if (newCommand)
		{
			name.setText(getString(R.string.command_default_name_prefix) + " " + (dataSource.getNumberOfCommands() + 1));
		}
		else
		{
			Command commandToEdit = dataSource.getCommand(commandID);
			name.setText(commandToEdit.getName());
			address.setText(Integer.toString(commandToEdit.getAddress()));
			command.setText(Integer.toString(commandToEdit.getCommand()));
			oldName = commandToEdit.getName();
		}
		
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
		
		return true;
	}

	@Override
	public void afterTextChanged(Editable textEdit)
	{
		// in landscape modes, this method get called when it shouldn't,
		// so this little workaround should suppress the problem
		if (this.menu == null)
			return;
		
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

				if (newCommand == false && oldName.equals(name.getText().toString()))
				{
					dataSource.updateCommand(commandID,
							name.getText().toString(),
							Integer.parseInt(address.getText().toString()),
							Integer.parseInt(command.getText().toString()));
				}
				else
				{	
					existingCommandID = dataSource.findCommandIDByName(name.getText().toString());
					
					if (existingCommandID == -1) // if a command with such name doesn't exist
					{
						if (newCommand)
						{
							dataSource.insertCommand(new Command(name.getText().toString(),
																Integer.parseInt(address.getText().toString()),
																Integer.parseInt(command.getText().toString())));
						}
						else
						{
							dataSource.updateCommand(commandID,
									name.getText().toString(),
									Integer.parseInt(address.getText().toString()),
									Integer.parseInt(command.getText().toString()));
						}
					}
					else
					{
						Command existingCommand = dataSource.getCommand(existingCommandID);
						
						if (existingCommand.getAddress() != Integer.parseInt(address.getText().toString()) ||
							existingCommand.getCommand() != Integer.parseInt(command.getText().toString()))
						{
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
							alertDialogBuilder.setTitle(getString(R.string.overwrite));
							alertDialogBuilder.setMessage(getString(R.string.overwrite_full_sentence));
							alertDialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									dataSource.updateCommand(existingCommandID,
											name.getText().toString(),
											Integer.parseInt(address.getText().toString()),
											Integer.parseInt(command.getText().toString()));
									
									if (newCommand == false)
									{
										dataSource.deleteCommand(commandID);
									}
									
									dialog.dismiss();
									
									setResult(Activity.RESULT_OK);
									finish();
								}
							});
							alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									dialog.dismiss();
								}
							});
							
							alertDialogBuilder.show();
							return true;
						}
					}
				}
				
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
		if (dataSource != null)
			dataSource.close();
		
		super.onDestroy();
	}
}
