package eu.mcft.sumoremote.preferences;

import eu.mcft.sumoremote.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

public abstract class PrefsAdjustedActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		// setting the theme, according to the preferences
		if (sharedPref.getBoolean("theme", false) == true)
			setTheme(R.style.CustomDark);
		else
			setTheme(R.style.CustomLight);
		
		// setting the orientation, according to the preferences
		String orientation = sharedPref.getString("screen_orientation", "portrait");
		
		switch (orientation)
		{
			case "portrait":
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;
			case "portrait_reversed":
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
				break;
			case "landscape":
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
			case "landscape_reversed":
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
				break;				
		}
	}
}
