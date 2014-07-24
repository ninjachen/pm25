package me.ninjachen.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public final class PM25Geocoder {
	private static final String LogTag = "pm25";
	private Location lastKnownLocation;
	private Context mContext;
	private LocationListener mListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			Log.d(LogTag,
					String.format("onLocationChanged : location=%s ",
							location.toString()));
		}

		public void onProviderDisabled(String disable) {
			Log.d(PM25Geocoder.LogTag, String.format(
					"onProviderDisabled : provider=%s",
					new Object[] { disable }));
		}

		public void onProviderEnabled(String enabled) {
			Log.d(PM25Geocoder.LogTag, String
					.format("onProviderEnabled : provider=%s",
							new Object[] { enabled }));
		}

		public void onStatusChanged(String paramAnonymousString,
				int paramAnonymousInt, Bundle paramAnonymousBundle) {
			Log.d(LogTag, String.format(
					"onStatusChanged : provider=%s , status=%s , extras=%s",
					new Object[] { paramAnonymousString,
							Integer.valueOf(paramAnonymousInt),
							paramAnonymousBundle.toString() }));
		}
	};
	private LocationManager mLocationManager;

	public PM25Geocoder(Context paramContext) {
		this.mContext = paramContext;
		this.mLocationManager = ((LocationManager) this.mContext
				.getSystemService(Context.LOCATION_SERVICE));
	}

	private void checkLastKnownLocation() {

		Location passive = this.mLocationManager
				.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		Location network = this.mLocationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		Location gps = this.mLocationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (passive != null) {
			this.lastKnownLocation = passive;
			Log.d(LogTag,
					String.format("passive_last : > %s", lastKnownLocation));
		}
		if ((network != null) && (passive.getTime() <= network.getTime())) {
			this.lastKnownLocation = network;
			Log.d(LogTag,
					String.format("network_last : > %s", lastKnownLocation));
		}
		if ((gps != null) && (network.getTime() <= gps.getTime())) {
			this.lastKnownLocation = gps;
			Log.d(LogTag, String.format("gps_last : > %s", lastKnownLocation));
		}
	}

	public void check() {
		if (!mLocationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			Toast.makeText(this.mContext, "请在<系统设置>中启用Google位置服务以获得准确定位",
					Toast.LENGTH_SHORT).show();
	}

	public void requestLocalCityName(final CityNameStatus cityNameStatus) {
		checkLastKnownLocation();
		if (this.lastKnownLocation != null) {
			GetTask task = new GetTask(
					"http://maps.googleapis.com/maps/api/geocode/json?latlng=%s&sensor=true") {
				protected void onPostExecute(String result) {
					String city = null;
					if (result != null && result.contains("OK")) {
						try {
							JSONArray jsonArray = new JSONObject(result)
									.getJSONArray("results").getJSONObject(0)
									.getJSONArray("address_components");
							for (int i = 0; i < jsonArray.length(); ++i) {
								JSONObject jsonObject = jsonArray
										.getJSONObject(i);
								String types = jsonObject.getJSONArray("types")
										.toString();
								if ((types.contains("locality"))
										&& (types.contains("political"))
										&& (!types.contains("sublocality"))) {
									Log.d(LogTag, jsonObject.toString());
									city = jsonObject.getString("short_name");
									Log.d(PM25Geocoder.LogTag, city);

								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							mLocationManager.removeUpdates(mListener);
						}
					}
					cityNameStatus.update(city);
				}

				protected void onPreExecute() {
					cityNameStatus.detecting();
				}
			};
			Object[] objects = new Object[2];
			objects[0] = Double.valueOf(lastKnownLocation.getLatitude());
			objects[1] = Double.valueOf(lastKnownLocation.getLongitude());
			Log.i(LogTag, lastKnownLocation.getLatitude() + " : "
					+ lastKnownLocation.getLongitude());
			task.execute(String.format("%s,%s", objects));
		}
		if (this.mLocationManager
				.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))
			this.mLocationManager.requestLocationUpdates(
					LocationManager.PASSIVE_PROVIDER, 1000L, 10.0F,
					this.mListener);
		if (this.mLocationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			this.mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 1000L, 10.0F,
					this.mListener);
		if (this.mLocationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER))
			this.mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1000L, 10.0F, this.mListener);
		Log.i(LogTag, mLocationManager.getProviders(true).toString());
	}

	public static abstract interface CityNameStatus {
		public abstract void detecting();

		public abstract void update(String paramString);
	}
}
