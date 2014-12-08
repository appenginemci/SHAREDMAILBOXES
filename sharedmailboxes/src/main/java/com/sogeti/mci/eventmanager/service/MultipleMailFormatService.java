package com.sogeti.mci.eventmanager.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
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
import com.sogeti.mci.eventmanager.dao.SettingsDAO;
import com.sogeti.mci.eventmanager.helper.ConstantList;
import com.sogeti.mci.eventmanager.model.MultipleFormatMail;

public class MultipleMailFormatService {
	 
	public static MultipleFormatMail create(String userId, Message message) throws MessagingException, IOException {
		
		Message m = GmailService.getMessageById(userId, message.getId());						
		
		Message gmailMessage = GmailService.getMessageById(userId, m.getId());
		
		MimeMessage mimeMessage = getMimeMessage(userId, m.getId());
	    InputStream isMessage = mimeMessage.getInputStream();
	    MailMessage asposeMessage = MailMessage.load(isMessage);
	    
	    MultipleFormatMail multipleFormatMail = new MultipleFormatMail();
	    multipleFormatMail.setAsposeMessage(asposeMessage);
	    multipleFormatMail.setMimeMessage(mimeMessage);
	    multipleFormatMail.setGmailMessage(gmailMessage);
	    
	    String name = retrieveEmailName(mimeMessage);
	    multipleFormatMail.setNameEmail(name);
	    
	    multipleFormatMail.setEvent(EventService.getEventByRecipient((mimeMessage.getAllRecipients())));
	    	   
	    multipleFormatMail.setDocument(DocumentService.getDocument(multipleFormatMail));
	    if (multipleFormatMail.getDocument().getdocumentId() != null) {
	    	multipleFormatMail.setNewEmail(false);
	    }
	    
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
    	if (parts!=null) {
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
					        File pj = DriveService.storeAttachmentToDrive(os, multipleFormatMail, filename, "attachment No"+part.getPartId(), part.getMimeType(), part.getMimeType(), "");
					        if (pj!=null) {
					        	multipleFormatMail.getDocument().addAttachmentId(pj.getId());
					        }
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
    	}
	  multipleFormatMail.setAsposeMessage(asposeMessage);
	  return multipleFormatMail;
	}
	
	public static MultipleFormatMail constructOutput(MultipleFormatMail multipleFormatMail) throws MessagingException {		
		MultipleFormatMail a = multipleFormatMail;
		MailMessage asposeMessage = multipleFormatMail.getAsposeMessage();
		MimeMessage mimeMessage = multipleFormatMail.getMimeMessage();
		Message gMailMessage = multipleFormatMail.getGmailMessage();
		StringBuffer messageHeader = new StringBuffer();
		messageHeader.append("<h1>1. Message Header</h1>");// TODO MCH iterate over To adresses
		messageHeader.append("<br>From : ").append(((InternetAddress)mimeMessage.getFrom()[0]).getAddress());
		//messageHeader.append("<br>From  : ").append(((InternetAddress)mimeMessage.getFrom()[0]).getPersonal());
		String stringTo = "";
		Address[] addressesTo =  mimeMessage.getRecipients(javax.mail.Message.RecipientType.TO);
		if (addressesTo != null && addressesTo.length > 0){
			for (int i=0; i<addressesTo.length; i++){
				InternetAddress tmpAddr = (InternetAddress)addressesTo[i];
				if (!stringTo.equals("")){
					stringTo = stringTo +  ", " + tmpAddr.getAddress();
				} else {
					stringTo = tmpAddr.getAddress();
				}
			}
		}
		messageHeader.append("<br>To : " + stringTo);
		String stringCc = "";
		Address[] addressesCc =  mimeMessage.getRecipients(javax.mail.Message.RecipientType.CC);
		if (addressesCc != null && addressesCc.length > 0){
			for (int i=0; i<addressesCc.length; i++){
				InternetAddress tmpAddr = (InternetAddress)addressesCc[i];
				if (!stringCc.equals("")){
					stringCc = stringCc +  ", " + tmpAddr.getAddress();
				} else {
					stringCc = tmpAddr.getAddress();
				}
			}
		}	
		messageHeader.append("<br>Cc : " + stringCc);
		messageHeader.append("<br>Date : ").append(mimeMessage.getSentDate());
		messageHeader.append("<br>Subject : ").append(mimeMessage.getSubject());
		messageHeader.append("<br>Thread Id : ").append(gMailMessage.getThreadId());
		messageHeader.append("<br>Mail Id : ").append(gMailMessage.getId());
		//messageHeader.append("<br>Event Email Address : ").append("googleforwork-zurich2015-reg@g.mci-group.com");
		messageHeader.append("<br>Event Email Address : ").append(multipleFormatMail.getEvent().getEmail());
		//messageHeader.append("<br>idRootFolder : ").append(multipleFormatMail.getEvent().getIdFolderRoot());
		//messageHeader.append("<br>Temporary Folder : ").append(getOutboxTempFolderId());
		messageHeader.append("<br>Temporary Folder : ").append(SettingsDAO.getInstance().getSetting("outboxTempFolderId"));
		messageHeader.append("<div>Status : </div>");
		messageHeader.append("<div>Reply by : </div>");
		messageHeader.append("<div>Reply date : </div>");
		
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
		responses.append("<br><h2>4.1. answer in progress</h2>").append("<br>");
		
		asposeMessage.setHtmlBody(messageHeader.append(originalMail).append(workingNotes).append(responses).toString());
		
		multipleFormatMail.setAsposeMessage(asposeMessage);
		
		return a;
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
