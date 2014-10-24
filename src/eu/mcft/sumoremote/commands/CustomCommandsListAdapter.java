package eu.mcft.sumoremote.commands;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import eu.mcft.sumoremote.*;
import eu.mcft.sumoremote.senders.SharedIRSender;

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
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.custom_commands_list_adapter, parent, false);
		
		TextView commandName = (TextView)rowView.findViewById(R.id.commandName);
		commandName.setText(commands[position].getName());
		
		TextView addressAndCommand = (TextView)rowView.findViewById(R.id.addressAndCommand);
		String addressAndCommandValue = context.getString(R.string.address) + ": " + commands[position].getAddress() +
				"   " + context.getString(R.string.command) + ": " + commands[position].getCommand();
		addressAndCommand.setText(addressAndCommandValue);
		
		Button sendButton = (Button)rowView.findViewById(R.id.sendButton);
		sendButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SharedIRSender.getSender(context).SendCommand(commands[position].getAddress(), commands[position].getCommand());
			}
		});
		
		return rowView;
	}
}
