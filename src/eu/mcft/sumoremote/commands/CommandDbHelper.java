package eu.mcft.sumoremote.commands;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CommandDbHelper extends SQLiteOpenHelper
{
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "database.db";
	public static final String DB_COMMANDS_TABLE = "Commands";
	
	public static final String KEY_ID = "id";
	public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
	
	public static final String KEY_NAME = "Name";
	public static final String NAME_OPTIONS = "TEXT NOT NULL";
	
	public static final String KEY_ADDRESS = "Address";
	public static final String ADDRESS_OPTIONS = "INTEGER DEFAULT 0";
	
	public static final String KEY_COMMAND = "Command";
	public static final String COMMAND_OPTIONS = "INTEGER DEFAULT 0";
	
	private static final String DB_CREATE_COMMANDS_TABLE =
			"CREATE TABLE " + DB_COMMANDS_TABLE + "( " +
			KEY_ID + " " + ID_OPTIONS + ", " +
			KEY_NAME + " " + NAME_OPTIONS + ", " +
			KEY_ADDRESS + " " + ADDRESS_OPTIONS + ", " +
			KEY_COMMAND + " " + COMMAND_OPTIONS +
			");";
	
	public CommandDbHelper(Context context)
	{
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database)
	{
		database.execSQL(DB_CREATE_COMMANDS_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(CommandDbHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + DB_COMMANDS_TABLE);
		onCreate(db);
	  }
}
