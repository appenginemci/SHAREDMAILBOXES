package com.sogeti.mci.eventmanager.service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import com.aspose.email.MailMessageSaveType;
import com.aspose.words.Document;
import com.aspose.words.LoadFormat;
import com.aspose.words.LoadOptions;
import com.aspose.words.SaveFormat;
import com.google.api.services.drive.model.File;
import com.sogeti.mci.eventmanager.helper.ByteArrayInOutStream;
import com.sogeti.mci.eventmanager.model.MultipleFormatMail;

public class ConversionService {
	
	
	public static void convertToDoc(MultipleFormatMail multipleFormatMail) throws Exception {
    ByteArrayInOutStream outputAsposeMessage = new ByteArrayInOutStream();
    multipleFormatMail.getAsposeMessage().save(outputAsposeMessage, MailMessageSaveType.getMHtmlFormat());	
    
    LoadOptions lo = new LoadOptions();
    lo.setLoadFormat(LoadFormat.MHTML);
    Document doc = new Document(outputAsposeMessage.getInputStream(),lo);
    
    ByteArrayOutputStream outputDoc = new ByteArrayOutputStream();
    doc.save(outputDoc, SaveFormat.DOC);
    
	File finalFile = write(outputDoc, multipleFormatMail.getNameEmail());	
	
	if (finalFile!=null && !finalFile.isEmpty()) {
		//service.users().messages().trash(userId, gMailMessage.getId()).execute();
		//System.out.println("Message trashed : "+multipleFormatMail.getGmailMessage().getId());
	}
	}
	
	private static File write(ByteArrayOutputStream baos, String name) throws FileNotFoundException, IOException, GeneralSecurityException, URISyntaxException {
	    return DriveService.storeToDrive(baos, name, "An email converted to a document","application/vnd.google-apps.document", "application/msword", "doc");
	}
	

}
