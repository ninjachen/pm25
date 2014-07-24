package me.ninjachen.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public final class APIClient {
	private static final String LogTag = "pm25";
	private static HttpClient mHttpClent = new DefaultHttpClient();

	//http post
	public String post(String uri, String param) {
		Log.d(LogTag, String.format("request post message:%s",
				new Object[] { param }));
		String result = null;
		HttpPost httpPost = new HttpPost(uri);
		try {
			ArrayList<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
			list.add(new BasicNameValuePair("message", param));
			httpPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
			HttpResponse httpResponse = mHttpClent.execute(httpPost);
			Log.d(LogTag, String.format("post StatusCode:%s", httpResponse
					.getStatusLine().getStatusCode()));
			result = EntityUtils.toString(httpResponse.getEntity());
			Log.d(LogTag, String.format("post HttpResponse:%s",
					new Object[] { result }));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	//http-get
	public String request(String param) {
		Log.d(LogTag,
				String.format("request get uri:%s", new Object[] { param }));
		HttpGet httpGet = new HttpGet(param);
		try {
			HttpResponse httpResponse = mHttpClent.execute(httpGet);
			Log.d(LogTag, String.format("get StatusCode:%s", httpResponse
					.getStatusLine().getStatusCode()));
			String result = EntityUtils.toString(httpResponse.getEntity());
			return result;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}