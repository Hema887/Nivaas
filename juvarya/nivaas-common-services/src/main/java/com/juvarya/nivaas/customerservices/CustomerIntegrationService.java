package com.juvarya.nivaas.customerservices;

import java.io.IOException;

public interface CustomerIntegrationService {

	boolean triggerSMS(String mobileNumber, String otp) throws IOException;

}
