package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.utils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class Mailer {

    public void sendPlainMsg(
            String _messageString
            , String _subjectString
            , String _fromAddressString
            , String _fromNameString
            , String _toAddressString
            , String _toNameString
    ) throws MessagingException, UnsupportedEncodingException {

        Properties props = new Properties();
        props.setProperty("mail.mime.charset", "UTF-8");
        Session session = Session.getDefaultInstance(props, null);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(_fromAddressString, _fromNameString));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(_toAddressString, _toNameString));
        msg.setSubject(_subjectString);
        msg.setText(_messageString);
        Transport.send(msg);
    }
}
