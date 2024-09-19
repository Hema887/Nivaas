package com.juvarya.nivaas.customerservices.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;

import com.juvarya.nivaas.customerservices.CustomerIntegrationService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CustomerIntegrationServiceImpl implements CustomerIntegrationService {

	@Value("${otp.trigger}")
	private boolean triggerOtp;

	@Override
	public boolean triggerSMS(String mobileNumber, String otp) throws IOException {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url("https://api.authkey.io/request?authkey=5784b393579d5d3d&mobile="
				+ mobileNumber + "&country_code=+91&sid=12135&name=Twinkle&otp=" + otp).get().build();
		Response response = client.newCall(request).execute();
		return response.isSuccessful();
	}

}
