package com.code.supportportal.service.email;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static com.code.supportportal.constant.EmailConstant.*;

@Service
public class EmailService {

    public void sendEmailWithNewPassword(String firstName, String password, String email) throws MessagingException {
        Message message = createMessage(firstName, password, email);
        SMTPTransport transport = (SMTPTransport) getEmailSession().getTransport();
        transport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    } // end method

    private Message createMessage(String firstName, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(RecipientType.TO, InternetAddress.parse(email, false));
        message.setRecipients(RecipientType.CC, InternetAddress.parse(CC_MAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText(String.format("Hello %s,\n\nYour new account password is: %s\n\nThe support team",
                firstName, password));
        message.setSentDate(new Date());
        message.saveChanges();

        return message;
    } // end message

    private Session getEmailSession(){
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLED, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);

        return Session.getDefaultInstance(properties, null);
    } // end session email

} // end email service class
