package eu.mcft.sumoremote;

import eu.mcft.sumoremote.R;
import eu.mcft.sumoremote.preferences.PrefsAdjustedActivity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends PrefsAdjustedActivity
{
	private TextView versionNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
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
