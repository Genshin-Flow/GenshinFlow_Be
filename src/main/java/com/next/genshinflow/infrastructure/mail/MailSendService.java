package com.next.genshinflow.infrastructure.mail;

import com.next.genshinflow.domain.user.repository.RedisRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@AllArgsConstructor
public class MailSendService {
    private JavaMailSender mailSender;
    private RedisRepository redisRepository;
    private static final SecureRandom randomGenerator = new SecureRandom();
    private static final Duration AUTH_NUM_EXPIRE_DURATION = Duration.ofSeconds(180);

    public String generateAuthCode() {
        StringBuilder randomNum = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            randomNum.append(randomGenerator.nextInt(10));
        }

        return randomNum.toString();
    }

    public void sendVerificationEmail(String email) {
        String authNum = generateAuthCode();
        String fromMail = "nextconnect.lab@gmail.com";
        String title = "Genshin Flow 인증코드";
        String content = loadEmailTemplate().replace("{{authNum}}", authNum);

        sendEmail(fromMail, email, title, content, authNum);
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
            log.info("Email sent to {}", toMail);
        }
        catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }

        // 인증번호는 3분동안 유효함
        redisRepository.setData(authNum, toMail, AUTH_NUM_EXPIRE_DURATION);
    }
}
