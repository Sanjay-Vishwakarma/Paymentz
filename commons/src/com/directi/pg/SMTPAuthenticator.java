package com.directi.pg;

import javax.mail.PasswordAuthentication;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Feb 3, 2012
 * Time: 8:39:58 PM
 * To change this template use File | Settings | File Templates.
 */
public  class SMTPAuthenticator extends javax.mail.Authenticator {

        private PasswordAuthentication passwordAuthentication;


          public SMTPAuthenticator()
          {
                /*
                String username = RB.getString("SMTP_AUTH_USER");
                String password = RB.getString("SMTP_AUTH_PWD");
                passwordAuthentication = new PasswordAuthentication(username, password);*/
          }

          public  SMTPAuthenticator(String username, String password)
          {
              passwordAuthentication = new PasswordAuthentication(username, password);
          }


          public PasswordAuthentication getPasswordAuthentication()
          {

                return  passwordAuthentication;
           }
        }


