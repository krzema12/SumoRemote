package eu.mcft.sumoremote;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomCommandsListAdapter extends ArrayAdapter<Command>
{
	private final Context context;
	private final Command[] commands;

	public CustomCommandsListAdapter(Context context, ArrayList<Command> commands)
	{
		super(context, R.layout.custom_commands_list_adapter, commands);
		this.context = context;
		this.commands = commands.toArray(new Command[commands.size()]);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.custom_commands_list_adapter, parent, false);
		
		TextView commandName = (TextView)rowView.findViewById(R.id.commandName);
		commandName.setText(commands[position].getName());
		
		TextView addressAndCommand = (TextView)rowView.findViewById(R.id.addressAndCommand);
		String addressAndCommandValue = context.getString(R.string.address) + ": " + commands[position].getAddress() +
				"   " + context.getString(R.string.command) + ": " + commands[position].getCommand();
		addressAndCommand.setText(addressAndCommandValue);
		
		return rowView;
	}
}
