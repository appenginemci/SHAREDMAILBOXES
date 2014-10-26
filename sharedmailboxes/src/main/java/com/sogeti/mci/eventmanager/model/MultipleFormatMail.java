package com.sogeti.mci.eventmanager.model;

import javax.mail.internet.MimeMessage;

import com.aspose.email.MailMessage;
import com.google.api.services.gmail.model.Message;

public class MultipleFormatMail {
	
	private MimeMessage mimeMessage;
	private Message gmailMessage;
	private MailMessage asposeMessage;
	private Message gmailRawMessage;
	private String nameEmail;
	private boolean existInDrive;
	
	public MimeMessage getMimeMessage() {
		return mimeMessage;
	}
	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}
	public Message getGmailMessage() {
		return gmailMessage;
	}
	public void setGmailMessage(Message gmailMessage) {
		this.gmailMessage = gmailMessage;
	}
	public MailMessage getAsposeMessage() {
		return asposeMessage;
	}
	public void setAsposeMessage(MailMessage asposeMessage) {
		this.asposeMessage = asposeMessage;
	}
	public Message getGmailRawMessage() {
		return gmailRawMessage;
	}
	public void setGmailRawMessage(Message gmailRawMessage) {
		this.gmailRawMessage = gmailRawMessage;
	}
	public String getNameEmail() {
		return nameEmail;
	}
	public void setNameEmail(String nameEmail) {
		this.nameEmail = nameEmail;
	}
	public boolean isExistInDrive() {
		return existInDrive;
	}
	public void setExistInDrive(boolean existInDrive) {
		this.existInDrive = existInDrive;
	}	

}
