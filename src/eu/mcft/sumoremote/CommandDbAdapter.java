package eu.mcft.sumoremote;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CommandDbAdapter
{
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "database.db";
	private static final String DB_COMMANDS_TABLE = "Commands";
	
	public static final String KEY_ID = "id";
	public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
	public static final int ID_COLUMN = 0;
	
	public static final String KEY_NAME = "Name";
	public static final String NAME_OPTIONS = "TEXT NOT NULL";
	public static final int NAME_COLUMN = 1;
	
	public static final String KEY_ADDRESS = "Address";
	public static final String ADDRESS_OPTIONS = "INTEGER DEFAULT 0";
	public static final int ADDRESS_COLUMN = 2;
	
	public static final String KEY_COMMAND = "Command";
	public static final String COMMAND_OPTIONS = "INTEGER DEFAULT 0";
	public static final int COMMAND_COLUMN = 3;
	
	private static final String DB_CREATE_COMMANDS_TABLE =
		"CREATE TABLE " + DB_COMMANDS_TABLE + "( " +
		KEY_ID + " " + ID_OPTIONS + ", " +
		KEY_NAME + " " + NAME_OPTIONS + ", " +
		KEY_ADDRESS + " " + ADDRESS_OPTIONS + ", " +
		KEY_COMMAND + " " + COMMAND_OPTIONS +
		");";
	
	private static final String DROP_TODO_TABLE =
		"DROP TABLE IF EXISTS " + DB_COMMANDS_TABLE;
	
	private SQLiteDatabase db;
	private Context context;
	private DatabaseHelper dbHelper;
	
	public CommandDbAdapter(Context context)
	{
		this.context = context;
	}
	
	public CommandDbAdapter open()
	{
		dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
		
		try
		{
			db = dbHelper.getWritableDatabase();
		}
		catch (SQLException e)
		{
			db = dbHelper.getReadableDatabase();
		}
		
		return this;
	}
	
	public void close()
	{
		dbHelper.close();
	}
	
	public long insertCommand(Command command)
	{
		ContentValues newCommandValues = new ContentValues();
		newCommandValues.put(KEY_NAME, command.getName());
		newCommandValues.put(KEY_ADDRESS, command.getAddress());
		newCommandValues.put(KEY_COMMAND, command.getCommand());
		
		return db.insert(DB_COMMANDS_TABLE, null, newCommandValues);
	}
	
	public boolean updateCommand(Command command)
	{
		return updateCommand(command.getId(), command.getName(), command.getAddress(), command.getCommand());
	}
	
	public boolean updateCommand(long id, String name, int address, int command)
	{
		String where = KEY_ID + "=" + id;
		
		ContentValues updateCommand = new ContentValues();
		updateCommand.put(KEY_NAME, name);
		updateCommand.put(KEY_ADDRESS, address);
		updateCommand.put(KEY_COMMAND, command);
		
		return db.update(DB_COMMANDS_TABLE, updateCommand, where, null) > 0;
	}
	
	public boolean deleteCommand(long id)
	{
		String where = KEY_ID + "=" + id;
		return db.delete(DB_COMMANDS_TABLE, where, null) > 0;
	}
	
	private Cursor getAllCommands()
	{
		String[] columns = {KEY_ID, KEY_NAME, KEY_ADDRESS, KEY_COMMAND};
		String orderBy = KEY_ADDRESS + ", " + KEY_COMMAND + ", " + KEY_NAME;
		return db.query(DB_COMMANDS_TABLE, columns, null, null, null, null, orderBy);
	}
	
	public Command getCommand(long id)
	{
		String[] columns = {KEY_ID, KEY_NAME, KEY_ADDRESS, KEY_COMMAND};
		String where = KEY_ID + "=" + id;
		Cursor cursor = db.query(DB_COMMANDS_TABLE, columns, where, null, null, null, null);
		Command thisCommand = null;
		
		if(cursor != null && cursor.moveToFirst())
		{
			String name = cursor.getString(NAME_COLUMN);
			int address = cursor.getInt(ADDRESS_COLUMN);
			int command = cursor.getInt(COMMAND_COLUMN);
			thisCommand = new Command(id, name, address, command);
		}
		
		return thisCommand;
	}
	
	public ArrayList<Command> getAllCommands(Activity activity)
	{
		ArrayList<Command> commands = new ArrayList<Command>();
		
		Cursor commandCursor = getAllCommands();
		
		if (commandCursor != null)
		{
			activity.startManagingCursor(commandCursor);
			commandCursor.moveToFirst();
		}
		
		if (commandCursor != null && commandCursor.moveToFirst())
		{
			do
			{
				long id = commandCursor.getLong(CommandDbAdapter.ID_COLUMN);
				String name = commandCursor.getString(CommandDbAdapter.NAME_COLUMN);
				int address = commandCursor.getInt(CommandDbAdapter.ADDRESS_COLUMN);
				int command = commandCursor.getInt(CommandDbAdapter.COMMAND_COLUMN);
				
				commands.add(new Command(id, name, address, command));
			}
			while (commandCursor.moveToNext());
		}
		
		return commands;
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		public DatabaseHelper(Context context, String name, CursorFactory factory, int version)
		{
			super(context, name, factory, version);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(DB_CREATE_COMMANDS_TABLE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL(DROP_TODO_TABLE);
			onCreate(db);
		}
	}
	
	
}
