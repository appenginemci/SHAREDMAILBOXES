package com.sogeti.mci.eventmanager.business;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import com.google.api.services.drive.model.File;
import com.google.api.services.gmail.model.Message;
import com.sogeti.mci.eventmanager.helper.ConstantList;
import com.sogeti.mci.eventmanager.model.MultipleFormatMail;
import com.sogeti.mci.eventmanager.service.ConversionService;
import com.sogeti.mci.eventmanager.service.DriveService;
import com.sogeti.mci.eventmanager.service.GmailService;
import com.sogeti.mci.eventmanager.service.MultipleMailFormatService;

public class MailManagerStream {
	
	public static void doJob() throws Exception {

		loadLicense();

		String userId = ConstantList.USER;

		List<com.google.api.services.gmail.model.Thread> threads = GmailService.listThreadsMatchingQuery(userId, "in:inbox is:unread -label:"+ConstantList.LABEL);

		if (threads.size() == 0) {
			System.err.println("No message in mailbox");
		} else {
			for (com.google.api.services.gmail.model.Thread thread : threads) {

				Message message = GmailService.getLastMessageFromThread(userId, thread);

				MultipleFormatMail multipleFormatMail = MultipleMailFormatService.create(userId, message);

				if (multipleFormatMail.getEvent() != null) {
					System.out.println("Converting message from Event : "+multipleFormatMail.getEvent().getEmail());
					System.out.println(multipleFormatMail.getNameEmail());
				
					multipleFormatMail = MultipleMailFormatService.constructMailWithAttachments(multipleFormatMail, userId);
				    
					multipleFormatMail = MultipleMailFormatService.constructOutput(multipleFormatMail);
			    
					ConversionService.convertToDoc(multipleFormatMail);
					
					if (multipleFormatMail.isExistInDrive()) {
						if (!GmailService.labelizeThread(userId, message.getThreadId())) {
							System.err.println("Failed to labelize message");
						}
					} else {
						if (!DriveService.deleteFiles(multipleFormatMail.getDocumentProperties())) {
							System.err.println("Failed to delete corrupted files");
							// TODO LOG IN DB
						}
					}
				}else {
					System.err.println("No event found for email: " +multipleFormatMail.getNameEmail());
				} 
			}
		}

	}
	
	public static void main(String args[]) {

		try {
			System.out.println(new Date()+" : Job Started ...");
			MailManagerStream.doJob();
			System.out.println(new Date()+" : Job ended ...");
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void loadLicense() {
		try
		 {
		   //Create a stream object containing the license file
			   InputStream email= MailManagerStream.class.getResourceAsStream("/"+"Aspose.Email.lic");
			   InputStream words= MailManagerStream.class.getResourceAsStream("/"+"Aspose.Words.lic");
		   
		   //Instantiate the License class
			   com.aspose.email.License licenseEmail=new com.aspose.email.License();
			   com.aspose.words.License licenseWords=new com.aspose.words.License();

		   //Set the license through the stream object
		   licenseEmail.setLicense(email);
		   licenseWords.setLicense(words);
		  
		   if(email != null)
			   email.close();
		   if(words != null)
			   words.close();
		 }
		 catch(Exception ex)
		 {
		   //Printing the exception, if it occurs
		   System.out.println(ex.toString());
		 }
	}
}
