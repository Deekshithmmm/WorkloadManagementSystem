package com.wms.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsAlertService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.from-number}")
    private String fromNumber;

    public void sendAlert(String toPhoneNumber, String message) {
        try {
            Twilio.init(accountSid, authToken);

            Message.creator(
                new PhoneNumber(toPhoneNumber),  // e.g. "+919876543210"
                new PhoneNumber(fromNumber),
                message
            ).create();

            System.out.println("📱 SMS sent to " + toPhoneNumber);

        } catch (Exception e) {
            System.out.println("❌ SMS failed: " + e.getMessage());
        }
    }
}
