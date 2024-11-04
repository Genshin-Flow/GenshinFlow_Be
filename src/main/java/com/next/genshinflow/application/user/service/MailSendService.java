package com.next.genshinflow.application.user.service;

import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import com.next.genshinflow.domain.user.repository.RedisRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Duration;

@Service
@AllArgsConstructor
public class MailSendService {
    private JavaMailSender mailSender;
    private RedisRepository redisRepository;
    private static final long AUTH_NUM_EXPIRE_TIME = 60L;

    public String generateAuthCode() {
        SecureRandom randomGenerator = new SecureRandom();
        StringBuilder randomNum = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            randomNum.append(randomGenerator.nextInt(10));
        }

        return randomNum.toString();
    }

    public String sendVerificationEmail(String email) {
        String authNum = generateAuthCode();
        String fromMail = "nextconnect.lab@gmail.com";
        String toMail = email;
        String title = "Genshin Flow 인증코드";
        String content = loadEmailTemplate().replace("{{authNum}}", authNum);

        sendEmail(fromMail, toMail, title, content, authNum);
        return authNum;
    }

    private String loadEmailTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("EmailSend.html");
            return Files.readString(Paths.get(resource.getURI()), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to read HTML template", e);
        }
    }

    public void sendEmail(String fromMail, String toMail, String title, String content, String authNum) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(fromMail);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);

            mailSender.send(message);
        }
        catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }

        // 인증번호는 1분동안 유효함
        redisRepository.setData(authNum, toMail, Duration.ofSeconds(AUTH_NUM_EXPIRE_TIME));
    }

    public void verifyAuthCode(String email, String authNum) {
        String storedEmail = redisRepository.getData(authNum);

        if (storedEmail == null || !storedEmail.equals(email)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_AUTH_CODE);
        }
    }
}
