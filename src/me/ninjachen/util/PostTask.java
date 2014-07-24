package me.ninjachen.util;

import android.os.AsyncTask;

public class PostTask extends AsyncTask<String, String, String> {
	private String mTargetURI;

	public PostTask(String paramString) {
		this.mTargetURI = paramString;
	}

	protected String doInBackground(String[] paramArrayOfString) {
		return new APIClient().post(this.mTargetURI, paramArrayOfString[0]);
	}
}