package com.project.societyManagement.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class OtpUtil {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000)); // 6-digit
    }

    public String hashOtp(String otp) {
        return encoder.encode(otp);
    }

    public boolean matches(String rawOtp, String hash) {
        return encoder.matches(rawOtp, hash);
    }
}
