package org.opensextant.xtext.collectors.mailbox;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 *
 * @author ubaldino
 *
 */
public class NTLMAuth extends Authenticator {
    String username = null;
    String password = null;

    public NTLMAuth(String u, String p) {
        username = u;
        password = p;
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}
