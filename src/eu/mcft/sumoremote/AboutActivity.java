package eu.mcft.sumoremote;

import eu.mcft.sumoremote.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class AboutActivity extends Activity
{
	private TextView versionNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		if(sharedPref.getBoolean("theme", false) == true)
			setTheme(android.R.style.Theme_Holo);
		else
			setTheme(android.R.style.Theme_Holo_Light);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		versionNumber = (TextView)findViewById(R.id.versionNumber);
		
		// getting version number
		try
		{
			versionNumber.setText("v" + this.getPackageManager()
				    .getPackageInfo(this.getPackageName(), 0).versionName);
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
