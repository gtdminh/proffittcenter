/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package proffittcenter;
/*
 * @(#)sendfile.java  1.11 03/06/19
 *
 * Copyright 1996-2003 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES OR LIABILITIES
 * SUFFERED BY LICENSEE AS A RESULT OF  OR RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL
 * SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 * FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Dave
 */
public class SimpleSSLMail_1 {

    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final int SMTP_HOST_PORT = 465;//587 did not work
    private static String SMTP_AUTH_USER = Main.server.backupUser;
    private static String SMTP_AUTH_PWD  = Main.server.backupPassword;

    public static void main(String[] args) throws Exception{
//       new SimpleSSLMail().test();
    }

    public void test(MimeMessage message,Transport transport,String user,String password) {
        try {
            SMTP_AUTH_USER = user;
            SMTP_AUTH_PWD  = password;
            message.setSentDate(new Date());

            message.addRecipient(Message.RecipientType.TO,
                 new InternetAddress(user));

            transport.connect
              (SMTP_HOST_NAME, SMTP_HOST_PORT, user, password);

            transport.sendMessage(message,
                message.getRecipients(Message.RecipientType.TO));
            transport.close();
        } catch (MessagingException ex) {
            Logger.getLogger(SimpleSSLMail_1.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}

