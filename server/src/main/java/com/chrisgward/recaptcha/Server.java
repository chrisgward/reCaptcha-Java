package com.chrisgward.recaptcha;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Server {
	public static class CaptchaResponse {
		private final boolean success;
		private final String errorMessage;

		CaptchaResponse(boolean success, String errorMessage) {
			this.success = success;
			this.errorMessage = errorMessage;
		}
		public boolean isSuccess() {
			return success;
		}

		public String getErrorMessage() {
			return errorMessage;
		}
	}

	public static CaptchaResponse validate(String privateKey, String remoteIp, String challenge, String response) {
		try {
			String postParameters = "privatekey=" + URLEncoder.encode(privateKey) + "&remoteip=" + URLEncoder.encode(remoteIp) +
					"&challenge=" + URLEncoder.encode(challenge) + "&response=" + URLEncoder.encode(response);

			URL url = new URL("http://www.google.com/recaptcha/api/verify");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(postParameters.getBytes().length));
			connection.setUseCaches (false);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(postParameters);
			wr.flush();
			wr.close();
			connection.disconnect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			boolean success = Boolean.parseBoolean(reader.readLine());
			String errorReason = null;
			if (!success) {
				errorReason = reader.readLine();
			}
			return new CaptchaResponse(success, errorReason);

		} catch (Exception e) {
			return new CaptchaResponse(false, e.getMessage());
		}
	}
}
