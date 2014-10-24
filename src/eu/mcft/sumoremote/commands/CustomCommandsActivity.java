package eu.mcft.sumoremote.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import eu.mcft.sumoremote.R;
import eu.mcft.sumoremote.preferences.PrefsAdjustedActivity;
import eu.mcft.sumoremote.senders.SharedIRSender;

public class CustomCommandsActivity extends PrefsAdjustedActivity
{
	private TextView noCommandsTextView;
	private ListView commandsListView;
	private CustomCommandsListAdapter listViewAdapter;

	private CommandsDataSource dataSource;
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
		commandsListView = (ListView)findViewById(R.id.loadedCommandsListView);
		registerForContextMenu(commandsListView);
		
		dataSource = new CommandsDataSource(getApplicationContext());
		dataSource.open();
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
				dataSource.deleteCommand(commandID);
				
				loadCommandsToListView();
			break;
			
			default:
		}

		return true;
	}
	
	private void loadCommandsToListView()
	{
		commands = (ArrayList<Command>)dataSource.getAllCommands();

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
		getMenuInflater().inflate(R.menu.custom_commands, menu);
		return super.onCreateOptionsMenu(menu);
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
			case R.id.action_share:
				share();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	protected void share()
	{
		try
		{
			File cacheDir = getExternalCacheDir();
			File outputFile = new File(cacheDir, "CustomCommands.src");
			
			FileOutputStream fos = new FileOutputStream(outputFile);
			CustomCommandsXMLSerializer xmlSerializer = new CustomCommandsXMLSerializer(dataSource);
			fos.write(xmlSerializer.getCommandsAsXML().getBytes());
			fos.close();
			
			Intent i = new Intent(Intent.ACTION_SEND);
		    i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		    i.setType("text/xml");
		    i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outputFile));
		    
		    startActivity(Intent.createChooser(i, getResources().getString(R.string.share_commands)));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		loadCommandsToListView();
	}
	
	protected void onDestroy()
	{
		if (dataSource != null)
			dataSource.close();
		
		super.onDestroy();
	}
}
