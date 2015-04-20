package eu.mcft.sumoremote.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import eu.mcft.sumoremote.R;
import eu.mcft.sumoremote.preferences.PrefsAdjustedActivity;

public class ImportCommandsActivity extends PrefsAdjustedActivity implements OnClickListener
{
	private List<Command> loadedCommands = new ArrayList<Command>();
	private ListView loadedCommandsListView;
	private Button confirmButton;
	private Button cancelButton;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import);
		
		loadedCommandsListView = (ListView)findViewById(R.id.loadedCommandsListView);
		confirmButton = (Button)findViewById(R.id.startButton);
		cancelButton = (Button)findViewById(R.id.stopButton);
		
		confirmButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);

		Intent intent = getIntent();
		Uri data = intent.getData();
		
		if (data != null)
		{
			intent.setData(null);
		    
			try
			{
				importData(data);
		    }
			catch (Exception e)
			{
				// TODO warn user about bad data here
				finish();
				return;
			}
		}

		// TODO launch home Activity (with FLAG_ACTIVITY_CLEAR_TOP) here
	}
	
	private void importData(Uri data) throws Exception
	{
		InputStream input = getContentResolver().openInputStream(data);
        InputSource is = new InputSource(input);
        CustomCommandsXMLParser parser = new CustomCommandsXMLParser(getApplicationContext(), loadedCommands);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser sp = factory.newSAXParser();
        XMLReader reader = sp.getXMLReader();
        
        reader.setContentHandler(parser);
        reader.parse(is);
        
        CustomCommandsListAdapter listViewAdapter = new CustomCommandsListAdapter(this, (ArrayList<Command>)loadedCommands);
        loadedCommandsListView.setAdapter(listViewAdapter);
	}

	@Override
	public void onClick(View v)
	{		
		if (v == confirmButton)
		{
			CommandsDataSource dbAdapter = new CommandsDataSource(getApplicationContext());
			dbAdapter.open();
			dbAdapter.dropAllCommands();
			
			for (Command c : loadedCommands)
			{
				dbAdapter.insertCommand(c);
			}
			
			dbAdapter.close();
		}
		
		finish();
	}
}
