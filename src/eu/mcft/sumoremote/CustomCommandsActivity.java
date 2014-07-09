package eu.mcft.sumoremote;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import eu.mcft.sumoremote.R;

public class CustomCommandsActivity extends Activity
{
	TextView noCommandsTextView;
	ListView commandsListView;
	
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
		
		String[] values = new String[] {	"Fast straight#Address: 3  Command: 0",
											"Wait and drive#Address: 3  Command: 1",
											"Left 90deg and right arc#Address: 3  Command: 2",
											"Right 90deg and left arc#Address: 3  Command: 4",
											"Zig-zag#Address: 3  Command: 5"};
		
		CustomCommandsListAdapter adapter = new CustomCommandsListAdapter(this, values);
		commandsListView.setAdapter(adapter);
		
		if (commandsListView.getCount() > 0)
			noCommandsTextView.setVisibility(View.GONE);
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
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
