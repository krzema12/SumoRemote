package eu.mcft.sumoremote;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import eu.mcft.sumoremote.R;

public class CustomCommandsActivity extends Activity
{
	private TextView noCommandsTextView;
	private ListView commandsListView;
	private CustomCommandsListAdapter listViewAdapter;
	
	private CommandDbAdapter dbAdapter;
	private Cursor commandCursor;
	private ArrayList<Command> commands;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		if(sharedPref.getBoolean("theme", false) == true)
			setTheme(R.style.CustomDark);
		else
			setTheme(R.style.CustomLight);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_commands);
		
		noCommandsTextView = (TextView)findViewById(R.id.noCommandsTextView);
		commandsListView = (ListView)findViewById(R.id.commandsListView);
		registerForContextMenu(commandsListView);
		
		dbAdapter = new CommandDbAdapter(getApplicationContext());
		dbAdapter.open();
		
		loadCommandsToListView();
	}
	
	// http://www.mikeplate.com/2010/01/21/show-a-context-menu-for-long-clicks-in-an-android-listview/
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		menu.setHeaderTitle(commands.get(info.position).getName());
		
		menu.add(Menu.NONE, 0, 0, "Delete");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		long commandID = commands.get(info.position).getId();
		
		dbAdapter.deleteCommand(commandID);
		loadCommandsToListView();
		
		return true;
	}
	
	private void loadCommandsToListView()
	{
		commands = new ArrayList<Command>();
		commandCursor = dbAdapter.getAllCommands();
		
		if (commandCursor != null)
		{
			startManagingCursor(commandCursor);
			commandCursor.moveToFirst();
		}
		
		if (commandCursor != null && commandCursor.moveToFirst())
		{
			do
			{
				long id = commandCursor.getLong(CommandDbAdapter.ID_COLUMN);
				String name = commandCursor.getString(CommandDbAdapter.NAME_COLUMN);
				int address = commandCursor.getInt(CommandDbAdapter.ADDRESS_COLUMN);
				int command = commandCursor.getInt(CommandDbAdapter.COMMAND_COLUMN);
				
				commands.add(new Command(id, name, address, command));
			}
			while (commandCursor.moveToNext());
		}
		
		listViewAdapter = new CustomCommandsListAdapter(this, commands);
		commandsListView.setAdapter(listViewAdapter);
		
		if (commandsListView.getCount() > 0)
			noCommandsTextView.setVisibility(View.GONE);
		else
			noCommandsTextView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.custom_commands, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent intent;
		
		switch (item.getItemId())
		{
			case R.id.action_add_command:
				intent = new Intent(this, NewCommandActivity.class);
				startActivityForResult(intent, 0);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == Activity.RESULT_OK)
		{
			loadCommandsToListView();
		}
	}
	
	protected void onDestroy()
	{
		if (dbAdapter != null)
			dbAdapter.close();
		
		super.onDestroy();
	}
}
