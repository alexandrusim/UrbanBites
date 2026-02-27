package gio.backend.service;

import com.google.common.io.BaseEncoding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import gio.backend.dto.TwoFaSetupDTO;
import gio.backend.dto.TwoFaVerifyDTO;
import gio.backend.entity.User;
import gio.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class TwoFactorService {

    private final UserRepository userRepository;
    private static final int TOTP_TIME_STEP = 30; // 30 seconds
    private static final int TOTP_DIGITS = 6;
    private static final String HMAC_ALGORITHM = "HmacSHA1";

    public String generateSecret() {
        byte[] buffer = new byte[20];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(buffer);
        Base32 base32 = new Base32();
        return base32.encodeToString(buffer);
    }

    public String generateQRCodeImage(String username, String secret, String issuer) {
        try {
            String otpAuthUri = String.format(
                    "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                    issuer, username, secret, issuer
            );

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthUri, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] qrCode = outputStream.toByteArray();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(qrCode);
        } catch (Exception e) {
            throw new RuntimeException("Eroare la generarea codului QR", e);
        }
    }

    
    public boolean verifyCode(String secret, String code) {
        try {
            if (code == null || code.length() != TOTP_DIGITS) {
                return false;
            }

            long timeCounter = System.currentTimeMillis() / 1000 / TOTP_TIME_STEP;

            for (int i = -1; i <= 1; i++) {
                if (verifyCodeForTimeWindow(secret, code, timeCounter + i)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean verifyCodeForTimeWindow(String secret, String code, long timeCounter) {
        try {
            String generatedCode = generateCodeForTimeWindow(secret, timeCounter);
            return generatedCode.equals(code);
        } catch (Exception e) {
            return false;
        }
    }

    private String generateCodeForTimeWindow(String secret, long timeCounter) throws Exception {
        Base32 base32 = new Base32();
        byte[] decodedSecret = base32.decode(secret);

        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(new SecretKeySpec(decodedSecret, 0, decodedSecret.length, HMAC_ALGORITHM));

        byte[] timeCounterBytes = ByteBuffer.allocate(8).putLong(timeCounter).array();
        byte[] hash = mac.doFinal(timeCounterBytes);

        int offset = hash[hash.length - 1] & 0xf;
        int code = (hash[offset] & 0x7f) << 24
                | (hash[offset + 1] & 0xff) << 16
                | (hash[offset + 2] & 0xff) << 8
                | (hash[offset + 3] & 0xff);

        return String.format("%06d", code % 1000000);
    }

    @Transactional
    public TwoFaSetupDTO setupTwoFa(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizator nu a fost găsit"));

        String secret = generateSecret();
        String qrCode = generateQRCodeImage(user.getEmail(), secret, "UrbanBites");

        TwoFaSetupDTO response = new TwoFaSetupDTO();
        response.setSecret(secret);
        response.setQrCode(qrCode);
        response.setEmail(user.getEmail());

        return response;
    }

    @Transactional
    public boolean verifyAndEnable2Fa(Integer userId, String secret, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizator nu a fost găsit"));

        if (!verifyCode(secret, code)) {
            return false;
        }

        user.setTwoFaSecret(secret);
        user.setTwoFaEnabled(true);
        user.setTwoFaVerified(true);
        userRepository.save(user);

        return true;
    }

    public boolean verify2FaCodeForLogin(Integer userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizator nu a fost găsit"));

        if (!user.getTwoFaEnabled() || user.getTwoFaSecret() == null) {
            return false;
        }

        return verifyCode(user.getTwoFaSecret(), code);
    }

    @Transactional
    public void disable2Fa(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizator nu a fost găsit"));

        user.setTwoFaEnabled(false);
        user.setTwoFaSecret(null);
        user.setTwoFaVerified(false);
        userRepository.save(user);
    }

    public boolean get2FaStatus(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizator nu a fost găsit"));
        
        return user.getTwoFaEnabled() != null && user.getTwoFaEnabled();
    }
}
