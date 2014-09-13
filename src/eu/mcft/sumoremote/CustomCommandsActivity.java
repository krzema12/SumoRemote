package eu.mcft.sumoremote;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class CustomCommandsActivity extends PrefsAdjustedActivity
{
	private TextView noCommandsTextView;
	private ListView commandsListView;
	private CustomCommandsListAdapter listViewAdapter;
	
	private CommandDbAdapter dbAdapter;
	private ArrayList<Command> commands;
	
	private static final int SEND = 0;
	private static final int EDIT = 1;
	private static final int DELETE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
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
		
		menu.add(Menu.NONE, SEND, SEND, this.getString(R.string.send));
		menu.add(Menu.NONE, EDIT, EDIT, this.getString(R.string.edit));
		menu.add(Menu.NONE, DELETE, DELETE, this.getString(R.string.delete));
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		long commandID;
		
		switch(item.getItemId())
		{
			case SEND:
				Command command = commands.get(info.position);
				SharedIRSender.getSender(this).SendCommand(command.getAddress(), command.getCommand());
			break;
			case EDIT:
				commandID = commands.get(info.position).getId();
				
				Intent intent = new Intent(this, NewCommandActivity.class);
				intent.putExtra("commandID", commandID);
				startActivityForResult(intent, 0);
				
				loadCommandsToListView();
			break;
			case DELETE:
				commandID = commands.get(info.position).getId();
				dbAdapter.deleteCommand(commandID);
				
				loadCommandsToListView();
			break;
			
			default:
		}

		return true;
	}
	
	private void loadCommandsToListView()
	{
		commands = dbAdapter.getAllCommands(this);

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
