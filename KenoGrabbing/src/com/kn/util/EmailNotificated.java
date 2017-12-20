package com.kn.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailNotificated {
	private String broadcastAccount;
	private String broadcastPassword;
	private String smtpName;
	private String smtpPort;
	private String emailReciverList;

	public void emailNotify(SmtpInfo smtpInfo, String subject, String content) {
		broadcastAccount = smtpInfo.getBroadcastAccount();
		broadcastPassword = smtpInfo.getBroadcastPassword();
		smtpName = smtpInfo.getSmtpName();
		System.out.println(broadcastAccount + broadcastPassword + smtpName);
		smtpPort = smtpInfo.getSmtpPort();
		emailReciverList = smtpInfo.getEmailReceivers();
		Properties props = System.getProperties();
		props.put("mail.smtp.starttls.enable", true); // added this line
		// props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.ssl.trust", smtpName);
		props.put("mail.smtp.user", broadcastAccount);
		props.put("mail.smtp.password", broadcastPassword);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.auth", true);

		Session session = Session.getInstance(props, null);
		MimeMessage message = new MimeMessage(session);
		System.out.println("sent notification");
		System.out.println("Port: " + smtpInfo.getSmtpPort());

		// Create the email addresses involved
		try {
			InternetAddress from = new InternetAddress(broadcastAccount);
			message.setSubject("[" + smtpInfo.getEmailLocation().toUpperCase() + "] " + subject);
			message.setText(content);
			message.setFrom(from);
			
			// Add email receivers
			if (emailReciverList.contains(";")) {
				String[]emailReciver = emailReciverList.split(";");
				for (int i = 0; i < emailReciver.length; i++) {
					message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(emailReciver[i]));
				}
			}else {
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(emailReciverList));
			}

			// Send message
			Transport transport = session.getTransport("smtp");
			transport.connect(smtpName, broadcastAccount, broadcastPassword);
			System.out.println("Transport: " + transport.toString());
			transport.sendMessage(message, message.getAllRecipients());

		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
