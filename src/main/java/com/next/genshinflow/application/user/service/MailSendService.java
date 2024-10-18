package com.next.genshinflow.application.user.service;

import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import com.next.genshinflow.util.RedisUtil;
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
import java.util.Random;

@Service
@AllArgsConstructor
public class MailSendService {
    private JavaMailSender mailSender;
    private RedisUtil redisUtil;
    private static final long AUTH_NUM_EXPIRE_TIME = 60L;

    public int makeRandomNum() {
        Random num = new Random();
        StringBuilder randomNum = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            randomNum.append(num.nextInt(10));
        }

        return Integer.parseInt(randomNum.toString());
    }

    public String joinEmail(String email) {
        int authNum = makeRandomNum();
        String setFrom = "nextconnect.lab@gmail.com";
        String toMail = email;
        String title = "Genshin Flow 인증코드";
        String content = readHtmlTemplate().replace("{{authNum}}", Integer.toString(authNum));

        mailSend(setFrom, toMail, title, content, authNum);
        return Integer.toString(authNum);
    }

    private String readHtmlTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("EmailSend.html");
            return new String(Files.readAllBytes(Paths.get(resource.getURI())), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to read HTML template", e);
        }
    }

    public void mailSend(String setFrom, String toMail, String title, String content, int authNum) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);

            mailSender.send(message);
        }
        catch (MessagingException e) {
            throw new RuntimeException("Failed to wend email", e);
        }

        // 인증번호는 1분동안 유효함
        redisUtil.setDataExpire(Integer.toString(authNum), toMail, AUTH_NUM_EXPIRE_TIME);
    }

    public void checkAuthNum(String email, String authNum) {
        String storedEmail = redisUtil.getData(authNum);

        if (storedEmail == null && !storedEmail.equals(email)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_AUTH_CODE);
        }
    }
}
