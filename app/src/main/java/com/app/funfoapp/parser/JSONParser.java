package com.app.funfoapp.parser;

import org.apache.http.NameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class JSONParser {

	InputStream is = null;
	JSONObject jObj = null;
	String json = "", response = "";
	// constructor
	public JSONParser() {
	}

	public String getJSONFromUrl(String str_url, String method, List<NameValuePair> params) {
		// Making HTTP request
		try {
			URL url = new URL(str_url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);

			if (method.equals("POST")) {
				conn.setRequestMethod("POST");

				OutputStream os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(os, "UTF-8"));
				writer.write(getQuery(params));
				writer.flush();
				writer.close();
				os.close();

				int responseCode=conn.getResponseCode();

				if (responseCode == HttpsURLConnection.HTTP_OK) {
					String line;
					BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
					while ((line=br.readLine()) != null) {
						json+=line;
					}
				}
				else {
					json="";
				}

			} else {
				conn.setRequestMethod("GET");
				is = new BufferedInputStream(conn.getInputStream());
				json = org.apache.commons.io.IOUtils.toString(is, "UTF-8");
			}
			// read the response
			//Log.e("tag","Response Code: "+ conn.getResponseCode());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
	{
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params)
		{
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}

}
