package eu.mcft.sumoremote.commands;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CommandsDataSource
{
	private SQLiteDatabase database;
	private CommandDbHelper dbHelper;
	private String[] allColumns = {
			CommandDbHelper.KEY_ID,
			CommandDbHelper.KEY_NAME,
			CommandDbHelper.KEY_ADDRESS,
			CommandDbHelper.KEY_COMMAND
	      };
	
	public CommandsDataSource(Context context)
	{
		dbHelper = new CommandDbHelper(context);
	}
	
	public void open() throws SQLException
	{
		database = dbHelper.getWritableDatabase();
	}
	
	public void close()
	{
		dbHelper.close();
	}
	
	public long insertCommand(Command command)
	{
		ContentValues values = new ContentValues();
		values.put(CommandDbHelper.KEY_NAME, command.getName());
		values.put(CommandDbHelper.KEY_ADDRESS, command.getAddress());
		values.put(CommandDbHelper.KEY_COMMAND, command.getCommand());
		
		long insertId = database.insert(CommandDbHelper.DB_COMMANDS_TABLE, null, values);
		
		return insertId;
	}
	
	public boolean deleteCommand(long id)
	{
		return database.delete(CommandDbHelper.DB_COMMANDS_TABLE,
				CommandDbHelper.KEY_ID + " = " + id, null) > 0;
	}
	
	public boolean updateCommand(Command command)
	{
		return updateCommand(command.getId(), command.getName(), command.getAddress(), command.getCommand());
	}	
	
	public boolean updateCommand(long id, String name, int address, int command)
	{
		ContentValues values = new ContentValues();
		values.put(CommandDbHelper.KEY_NAME, name);
		values.put(CommandDbHelper.KEY_ADDRESS, address);
		values.put(CommandDbHelper.KEY_COMMAND, command);
		
		return database.update(CommandDbHelper.DB_COMMANDS_TABLE, values,
				CommandDbHelper.KEY_ID + " = " + id, null) > 0;
	}
	
	public List<Command> getAllCommands()
	{
		List<Command> commands = new ArrayList<Command>();
		String orderBy = CommandDbHelper.KEY_ADDRESS + ", " + CommandDbHelper.KEY_COMMAND
				+ ", " + CommandDbHelper.KEY_NAME;
		
		Cursor cursor = database.query(CommandDbHelper.DB_COMMANDS_TABLE,
				allColumns, null, null, null, null, orderBy);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast())
		{
			Command command = getCommandAtCursorPos(cursor);
			commands.add(command);
			cursor.moveToNext();
		}
		
		cursor.close();
		return commands;
	}
	
	public void dropAllCommands()
	{
		database.delete(CommandDbHelper.DB_COMMANDS_TABLE, null, null);
	}
	
	private Command getCommandAtCursorPos(Cursor cursor)
	{
		Command command = new Command();
		command.setId(cursor.getLong(0));
		command.setName(cursor.getString(1));
		command.setAddress(cursor.getInt(2));
		command.setCommand(cursor.getInt(3));
		
		return command;
	}

	public long getNumberOfCommands()
	{
		return DatabaseUtils.queryNumEntries(database, CommandDbHelper.DB_COMMANDS_TABLE);
	}

	public Command getCommand(long commandID)
	{
		Cursor cursor = database.query(CommandDbHelper.DB_COMMANDS_TABLE, allColumns,
				CommandDbHelper.KEY_ID + "=" + commandID, null, null, null, null);
		Command thisCommand = null;
		
		if (cursor != null && cursor.moveToFirst())
		{
			String name = cursor.getString(1);
			int address = cursor.getInt(2);
			int command = cursor.getInt(3);
			thisCommand = new Command(commandID, name, address, command);
		}
		
		return thisCommand;
	}

	public long findCommandIDByName(String name)
	{
		String[] columns = { CommandDbHelper.KEY_ID };
		
		Cursor cursor = database.query(CommandDbHelper.DB_COMMANDS_TABLE, columns,
				CommandDbHelper.KEY_NAME + "='" + name + "'", null, null, null, null);
		
		if (cursor != null && cursor.moveToFirst())
		{
			int ID = cursor.getInt(0);
			return ID;
		}
		else
		{
			return -1;
		}
	}
}
