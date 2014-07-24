package me.ninjachen.util;

import android.os.AsyncTask;

public class GetTask extends AsyncTask<String, String, String> {
	private String mTargetURI;

	public GetTask(String uri) {
		this.mTargetURI = uri;
	}

	protected String doInBackground(String[] strs) {
		APIClient apiClient = new APIClient();
		return apiClient.request(String.format(mTargetURI, strs[0]));
	}
}