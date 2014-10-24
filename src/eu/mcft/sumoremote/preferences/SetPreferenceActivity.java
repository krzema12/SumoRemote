package eu.mcft.sumoremote.preferences;

import android.os.Bundle;

public class SetPreferenceActivity extends PrefsAdjustedActivity
{
	 @Override
	 protected void onCreate(Bundle savedInstanceState)
	 {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
	 }
}
