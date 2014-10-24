package eu.mcft.sumoremote.commands;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import android.content.Context;

public class CustomCommandsXMLParser extends DefaultHandler2
{
	private List<Command> commands;
	private Command currentCommand;
	StringBuilder builder;
	
	public CustomCommandsXMLParser(Context context, List<Command> commands)
	{
		this.commands = commands;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		builder = new StringBuilder();
		
		if (localName.equals("command") == false)
			return;

		currentCommand = new Command();
		currentCommand.setAddress(Integer.parseInt(attributes.getValue("address")));
		currentCommand.setCommand(Integer.parseInt(attributes.getValue("command")));
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (localName.equals("command") == false)
			return;
		
		currentCommand.setName(builder.toString());
		commands.add(currentCommand);
	}
	
	@Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
		String tempString = new String(ch, start, length);
		builder.append(tempString);
    }
}
