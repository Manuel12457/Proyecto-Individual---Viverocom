package com.example.viverocom;

import androidx.lifecycle.ViewModel;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailViewModel extends ViewModel {

    private Thread thread = null;

    public void enviarCorreo(String receptor, String codigo) {
        String body = "Para proceder con su solicitud de cambio de contraseña, deberá ingresar en la aplicación el sigueinte código: " + codigo;
        String emisor = "viverocom001@gmail.com";
        String password = "givjnkxuxgyyvelw";

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emisor, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emisor));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receptor));
            message.setSubject("Solicitud - Cambio de contraseña");
            message.setText(body);

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(message);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
