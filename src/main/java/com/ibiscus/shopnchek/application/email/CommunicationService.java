package com.ibiscus.shopnchek.application.email;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class CommunicationService {

    private final String host;

    private final int port;

    private final String user;

    private final String password;

    public CommunicationService(String host, int port, String user,
            String password) {
        super();
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public void sendMail(final String from, final String to, final String subject, final String message) {
        HtmlEmail email = new HtmlEmail();

        email.setHostName(host);
        email.setSmtpPort(port);
        email.setAuthenticator(new DefaultAuthenticator(user, password));
        email.setStartTLSEnabled(true);
        try {
            email.setFrom(from);
            email.addTo(to);
            email.setSubject(subject);
            email.setDebug(true);
            email.setHtmlMsg(message);
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
}
