package com.sogeti.mci.eventmanager.business;

import java.io.InputStream;
import java.util.List;

import com.google.api.services.gmail.model.Thread;
import com.sogeti.mci.eventmanager.helper.ConstantList;
import com.sogeti.mci.eventmanager.model.MultipleFormatMail;
import com.sogeti.mci.eventmanager.service.ConversionService;
import com.sogeti.mci.eventmanager.service.GmailService;
import com.sogeti.mci.eventmanager.service.MultipleMailFormatService;

public class MailManagerStream {
	
	public static void doJob() throws Exception {
		
		loadLicense();
				
		String userId = ConstantList.USER;
		
		List<Thread> listThreads = GmailService.listThreadsMatchingQuery(userId,"in:inbox  is:unread");
		
		if (listThreads.size() == 0) {
			System.err.println("No unread message in mailbox");
		} else {
			for (int indice=0;indice<listThreads.size();indice++) {
								
				Thread t = listThreads.get(indice);
				
				MultipleFormatMail multipleFormatMail = MultipleMailFormatService.create(userId, t);
				
				multipleFormatMail = MultipleMailFormatService.constructMailWithAttachments(multipleFormatMail, userId);
				    
			    multipleFormatMail = MultipleMailFormatService.constructOutput(multipleFormatMail);
			    
			    ConversionService.convertToDoc(multipleFormatMail);
			}
		}

	}
	
	public static void main(String args[]) {

		try {
			System.out.println("Job Started ...");
			MailManagerStream.doJob();
			System.out.println("Job ended ...");
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
