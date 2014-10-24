package com.sogeti.mci.eventmanager.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.aspose.email.LinkedResource;
import com.aspose.email.MailMessage;
import com.google.api.client.util.Base64;
import com.google.api.services.drive.model.File;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.sogeti.mci.eventmanager.helper.ConstantList;
import com.sogeti.mci.eventmanager.model.MultipleFormatMail;

public class MultipleMailFormatService {
	
	public static MultipleFormatMail create(String userId, com.google.api.services.gmail.model.Thread thread) throws MessagingException, IOException {
		
		Message m = GmailService.getLastMessageFromThread(userId, thread);						
		
		Message gmailMessage = GmailService.getMessageById(userId, m.getId());
		
		MimeMessage mimeMessage = getMimeMessage(userId, m.getId());
	    InputStream isMessage = mimeMessage.getInputStream();
	    MailMessage asposeMessage = MailMessage.load(isMessage);
	    
	    MultipleFormatMail multipleFormatMail = new MultipleFormatMail();
	    multipleFormatMail.setAsposeMessage(asposeMessage);
	    multipleFormatMail.setMimeMessage(mimeMessage);
	    multipleFormatMail.setGmailMessage(gmailMessage);
	    multipleFormatMail.setNameEmail(retrieveEmailName(mimeMessage));
	    
	    return multipleFormatMail;	    
	}
	
	private static MimeMessage getMimeMessage( String userId, String messageId)
		      throws IOException, MessagingException {
	    Message message = GmailService.getRawMessageById(userId, messageId);
	    		    
	    byte[] emailBytes = Base64.decodeBase64(message.getRaw());

	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	    MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

	    return email;
	}

	public static MultipleFormatMail constructMailWithAttachments (MultipleFormatMail multipleFormatMail, String userId) throws IOException, GeneralSecurityException, URISyntaxException {
	    Message gMailMessage = multipleFormatMail.getGmailMessage();
	    MailMessage asposeMessage = multipleFormatMail.getAsposeMessage();
	    
    	List<MessagePart> parts = gMailMessage.getPayload().getParts();
	    for (MessagePart part : parts) {
		      if (part.getMimeType().equals("text/html") && asposeMessage.getHtmlBody().equals("")) {
		    	  System.out.println("Transferring HTML content from Gmail to Aspose");
		    	  asposeMessage.setHtmlBody(new String(Base64.decodeBase64(part.getBody().getData()), "UTF-8"));
		      }
		      if (part.getFilename() != null && part.getFilename().length() > 0) {
		        String attId = part.getBody().getAttachmentId();
		        MessagePartBody attachPart = GmailService.getMessagePartBody(userId, gMailMessage.getId(), attId);
		        byte[] byteArray = Base64.decodeBase64(attachPart.getData());
			    if (getHeaderValue(part.getHeaders(),"Content-Disposition").startsWith("attachment")) {
				        String filename = part.getFilename();
				        ByteArrayOutputStream os = new ByteArrayOutputStream(); 
				        try { 
				        	os.write(byteArray); 
				        	os.close(); 
			        	} catch (IOException e) { 
			        		e.printStackTrace(); 
			        	}
				        File pj = DriveService.storeAttachmentToDrive(os, multipleFormatMail.getNameEmail(), filename, "attachment No"+part.getPartId(), part.getMimeType(), part.getMimeType(), "");
				        asposeMessage.setHtmlBody(asposeMessage.getHtmlBody()+"<br><a href=\""+pj.getAlternateLink()+"\">"+filename+"</a>");					        
			    } else if ((getHeaderValue(part.getHeaders(),"Content-Disposition").startsWith("inline"))) {
			    		LinkedResource res = new LinkedResource( new ByteArrayInputStream(byteArray), part.getMimeType());// MediaTypeNames.Image.JPEG				    	
				    	String cid = getHeaderValue(part.getHeaders(),"X-Attachment-Id");
				    	if (cid.equals("")) {
				    		cid = getHeaderValue(part.getHeaders(),"Content-ID");
				    	}
			    		res.setContentId(cid);
				    	asposeMessage.getLinkedResources().addItem(res);
			    }
		      }
		 }
	  multipleFormatMail.setAsposeMessage(asposeMessage);
	  return multipleFormatMail;
	}
	
	public static MultipleFormatMail constructOutput(MultipleFormatMail multipleFormatMail) throws MessagingException {		
		MailMessage asposeMessage = multipleFormatMail.getAsposeMessage();
		MimeMessage mimeMessage = multipleFormatMail.getMimeMessage();
		Message gMailMessage = multipleFormatMail.getGmailMessage();
		
		StringBuffer messageHeader = new StringBuffer();
		messageHeader.append("<h1>1. Message Header</h1>");// TODO MCH iterate over To adresses
		messageHeader.append("<br>From Email : ").append(((InternetAddress)mimeMessage.getFrom()[0]).getAddress());
		messageHeader.append("<br>From  : ").append(((InternetAddress)mimeMessage.getFrom()[0]).getPersonal());
		messageHeader.append("<br>To : ").append(((InternetAddress)mimeMessage.getAllRecipients()[0]).getAddress()); 
		messageHeader.append("<br>Date : ").append(mimeMessage.getSentDate());
		messageHeader.append("<br>Subject : ").append(mimeMessage.getSubject());
		messageHeader.append("<br>Message Id : ").append(gMailMessage.getId());
		messageHeader.append("<br>Thread Id : ").append(gMailMessage.getThreadId());
		messageHeader.append("<br><br><br><br>");
		
		StringBuffer originalMail = new StringBuffer();
		originalMail.append("<h1>2. Original Mail</h1>");
		originalMail.append(multipleFormatMail.getAsposeMessage().getHtmlBody());
		originalMail.append("<br><br><br><br>");
		
		StringBuffer workingNotes = new StringBuffer();
		workingNotes.append("<h1>3. Working Notes</h1>");
		workingNotes.append("<br><br><br><br>");
		
		StringBuffer responses = new StringBuffer();
		responses.append("<h1>4. Responses</h1>");
		responses.append("<br><h2>4.1. answer in progress</h2>");
		responses.append("<br>[From] : ").append(((InternetAddress)mimeMessage.getAllRecipients()[0]).getAddress()); 
		responses.append("<br>[Subject] : ").append(mimeMessage.getSubject());
		responses.append("<br>[Begin response]<br>");
		responses.append("<br><br>[End response]");
		
		asposeMessage.setHtmlBody(messageHeader.append(originalMail).append(workingNotes).append(responses).toString());
		
		multipleFormatMail.setAsposeMessage(asposeMessage);
		
		return multipleFormatMail;
	}
	
	private static String getHeaderValue(List<MessagePartHeader> list, String name) {
		String toReturn = "";
		for (MessagePartHeader messagePartHeader : list) {
			if (name.equalsIgnoreCase(messagePartHeader.getName())) {
				toReturn = messagePartHeader.getValue();				
			}
		}		
		return toReturn;
	}	
	
	private static String retrieveEmailName(MimeMessage mimeMessage) throws MessagingException {
		String nameEmail = "no_name";
		nameEmail = null == ((InternetAddress)mimeMessage.getFrom()[0]).getPersonal() ? ((InternetAddress)mimeMessage.getFrom()[0]).getAddress() : ((InternetAddress)mimeMessage.getFrom()[0]).getPersonal();
		nameEmail = validateName(nameEmail, mimeMessage);
		System.out.println("Converting message ...");
		System.out.println(nameEmail);
		if (nameEmail.length()>50) {
			nameEmail = nameEmail.substring(0, 50);
		}
		return nameEmail;
	}

	private static String validateName(String nameEmail, MimeMessage mimeMessage)
			throws MessagingException {
		nameEmail = nameEmail+" - "+(mimeMessage.getSubject().length() == 0 ? "empty" : mimeMessage.getSubject());
		for (char c : ConstantList.ILLEGAL_CHARACTERS) {
			if (nameEmail.indexOf(c)>-1) {
				nameEmail = nameEmail.replaceAll("\\"+c, "");
			}
		}
		return nameEmail;
	}
	
}
