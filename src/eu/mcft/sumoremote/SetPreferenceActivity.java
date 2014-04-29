package eu.mcft.sumoremote;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SetPreferenceActivity extends Activity
{
	 @Override
	 protected void onCreate(Bundle savedInstanceState)
	 {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		if(sharedPref.getBoolean("theme", false) == true)
			setTheme(android.R.style.Theme_Holo);
		else
			setTheme(android.R.style.Theme_Holo_Light);
		 
		 super.onCreate(savedInstanceState);
		 getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
	 }
}
