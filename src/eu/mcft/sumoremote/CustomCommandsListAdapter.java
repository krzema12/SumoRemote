package eu.mcft.sumoremote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomCommandsListAdapter extends ArrayAdapter<String>
{
	private final Context context;
	private final String[] values;

	public CustomCommandsListAdapter(Context context, String[] values)
	{
		super(context, R.layout.custom_commands_list_adapter, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.custom_commands_list_adapter, parent, false);

	    TextView commandName = (TextView)rowView.findViewById(R.id.commandName);
	    commandName.setText(values[position]);
	    
	    return rowView;
	}
}
