package com.kn.util;

public class SmtpInfo {
	private String smtpName;
	private String smtpPort;
	private String broadcastAccount;
	private String broadcastPassword;
	private String emailReceivers;
	private String emailFailSubject;
	private String emailFailText;
	private String emailSuccessfulSubject;
	private String emailSuccessfulText;

	public String getSmtpName() {
		return smtpName;
	}

	public void setSmtpName(String smtpName) {
		this.smtpName = smtpName;
	}

	public String getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getBroadcastAccount() {
		return broadcastAccount;
	}

	public void setBroadcastAccount(String broadcastAccount) {
		this.broadcastAccount = broadcastAccount;
	}

	public String getBroadcastPassword() {
		return broadcastPassword;
	}

	public void setBroadcastPassword(String broadcastPassword) {
		this.broadcastPassword = broadcastPassword;
	}

	public String getEmailReceivers() {
		return emailReceivers;
	}

	public void setEmailReceivers(String emailReceivers) {
		this.emailReceivers = emailReceivers;
	}

	public String getEmailFailSubject() {
		return emailFailSubject;
	}

	public void setEmailFailSubject(String emailFailSubject) {
		this.emailFailSubject = emailFailSubject;
	}

	public String getEmailFailText() {
		return emailFailText;
	}

	public void setEmailFailText(String emailFailText) {
		this.emailFailText = emailFailText;
	}

	public String getEmailSuccessfulSubject() {
		return emailSuccessfulSubject;
	}

	public void setEmailSuccessfulSubject(String emailSuccessfulSubject) {
		this.emailSuccessfulSubject = emailSuccessfulSubject;
	}

	public String getEmailSuccessfulText() {
		return emailSuccessfulText;
	}

	public void setEmailSuccessfulText(String emailSuccessfulText) {
		this.emailSuccessfulText = emailSuccessfulText;
	}

}
