package pub.developers.forum.infrastructure.mail;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pub.developers.forum.common.enums.ErrorCodeEn;
import pub.developers.forum.common.exception.BizException;
import pub.developers.forum.domain.entity.Message;
import pub.developers.forum.domain.service.MailService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.security.Security;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * @author Qiangqiang.Bian
 * @create 2020/11/4
 * @desc
 **/
@Slf4j
@Data
@ConfigurationProperties(prefix = "custom-config.mail.smtp")
@Component
public class Mail163ServiceImpl implements MailService {

    private String host;

    private String port;

    private String socketFactoryPort;

    private String auth;

    private String account;

    private String password;

    private String fromAddress;

    /**
     * 发送html内容（异步）
     * @param mailMessage
     */
    @Async("asyncExecutor")
    @Override
    public void sendHtml(Message mailMessage) {
        log.info("异步发送HTML邮件开始, 收件人: {}, 线程: {}", mailMessage.getReceiver().getId(), Thread.currentThread().getName());
        sendMail(mailMessage, (message) -> {
            Multipart mainPart = new MimeMultipart();
            BodyPart html = new MimeBodyPart();
            try {
                html.setContent(mailMessage.getContent(), "text/html; charset=utf-8");
                mainPart.addBodyPart(html);
                message.setContent(mainPart);
                log.info("HTML邮件内容设置完成, 收件人: {}", mailMessage.getReceiver().getId());
            } catch (MessagingException e) {
                log.error("HTML邮件内容设置失败, 收件人: {}", mailMessage.getReceiver().getId(), e);
                throw new BizException(ErrorCodeEn.MESSAGE_SYSTEM_MAIL_SEND_FAIL);
            }
        });
        log.info("异步发送HTML邮件完成, 收件人: {}", mailMessage.getReceiver().getId());
    }

    /**
     * 发送文本内容（异步）
     * @param mailMessage
     */
    @Async("asyncExecutor")
    @Override
    public void sendText(Message mailMessage) {
        log.info("异步发送文本邮件开始, 收件人: {}, 线程: {}", mailMessage.getReceiver().getId(), Thread.currentThread().getName());
        sendMail(mailMessage, (message) -> {
            try {
                message.setText(mailMessage.getContent());
                log.info("文本邮件内容设置完成, 收件人: {}", mailMessage.getReceiver().getId());
            } catch (MessagingException e) {
                log.error("文本邮件内容设置失败, 收件人: {}", mailMessage.getReceiver().getId(), e);
                throw new BizException(ErrorCodeEn.MESSAGE_SYSTEM_MAIL_SEND_FAIL);
            }
        });
        log.info("异步发送文本邮件完成, 收件人: {}", mailMessage.getReceiver().getId());
    }

    private void sendMail(Message emailMessage, Consumer<MimeMessage> consumer) {
        log.info("开始发送邮件, 收件人: {}, 主题: {}", emailMessage.getReceiver().getId(), emailMessage.getTitle());
        try {
            Session session = Session.getDefaultInstance(getProperties(), new Authenticator() {
                //身份认证
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(account, password);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress));
            message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(emailMessage.getReceiver().getId()));
            message.setSubject(emailMessage.getTitle());

            consumer.accept(message);

            message.saveChanges();
            Transport.send(message);
            log.info("邮件发送成功, 收件人: {}", emailMessage.getReceiver().getId());
        } catch (Exception e) {
            log.error("邮件发送失败, 收件人: {}, 主题: {}", emailMessage.getReceiver().getId(), emailMessage.getTitle(), e);
            throw new BizException(ErrorCodeEn.MESSAGE_SYSTEM_MAIL_SEND_FAIL);
        }
    }

    private Properties getProperties() {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.smtp.socketFactory.port", socketFactoryPort);
        props.setProperty("mail.smtp.auth", auth);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        return props;
    }
}
