package me.ninjachen;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PM25CitySetting {
	private static final String LogTag = "pm25";
	private SharedPreferences mPreferences;

	public PM25CitySetting(Context paramContext) {
		mPreferences = paramContext.getSharedPreferences("pm25_city_setting",
				Context.MODE_PRIVATE);
	}

	public String getAutoCity() {
		return mPreferences.getString("auto_city", "");
	}

	public String getCity() {
		return mPreferences.getString("setting_city", "auto");
	}

	public void setAutoCity(String autoCity) {
		Log.d(LogTag, String.format("setting auto city : %s ",
				new Object[] { autoCity }));
		mPreferences.edit().putString("auto_city", autoCity).commit();
	}

	public void setCity(String city) {
		Log.d(LogTag,
				String.format("setting city : %s ", new Object[] { city }));
		mPreferences.edit().putString("setting_city", city).commit();
	}
}