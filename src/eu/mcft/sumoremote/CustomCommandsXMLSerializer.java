package eu.mcft.sumoremote;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.content.Context;
import android.util.Xml;

public class CustomCommandsXMLSerializer
{
	private CommandDbAdapter dbAdapter;
	
	public CustomCommandsXMLSerializer(CommandDbAdapter dbAdapter)
	{
		this.dbAdapter = dbAdapter;
	}
	
	public String getCommandsAsXML(Activity activity)
	{
		try
		{
			StringWriter writer = new StringWriter();
			XmlSerializer serializer = Xml.newSerializer();
			
			dbAdapter.open();
			ArrayList<Command> commands = dbAdapter.getAllCommands(activity);

			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			serializer.startTag(null, "commands");
			
			for (Command c : commands)
			{
				serializer.startTag(null, "command");
				serializer.attribute(null, "address", Integer.toString(c.getAddress()));
				serializer.attribute(null, "command", Integer.toString(c.getCommand()));
				serializer.text(c.getName());
				serializer.endTag(null, "command");
			}
			
			serializer.endTag(null, "commands");
			serializer.endDocument();
	        serializer.flush();
			
			dbAdapter.close();
			
			return writer.toString();
		
		} catch (IllegalArgumentException | IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	

}
