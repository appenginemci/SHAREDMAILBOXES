package com.sogeti.mci.eventmanager.service;

import com.sogeti.mci.eventmanager.dao.DocumentDAO;
import com.sogeti.mci.eventmanager.dao.ThreadDAO;
import com.sogeti.mci.eventmanager.model.Document;
import com.sogeti.mci.eventmanager.model.MultipleFormatMail;

public class DocumentService {

	public static boolean insertOrUpdateDocument(MultipleFormatMail multipleFormatMail) {
		boolean toReturn = true;
		String threadId = multipleFormatMail.getGmailMessage().getThreadId();
		String documentId = multipleFormatMail.getDocument().getdocumentId();
		if (multipleFormatMail.isNewEmail()) {
			toReturn = toReturn && ThreadDAO.insertThread(multipleFormatMail.getEvent().getId(), threadId);
			if (toReturn) {
				toReturn = toReturn && DocumentDAO.insertDocument(documentId, threadId, multipleFormatMail.getNameEmail());
			}
		} else {
			toReturn = toReturn && DocumentDAO.updateDocument(documentId, threadId);
		}
		return toReturn;
	}
	
	public static String getDocumentId(MultipleFormatMail multipleFormatMail) {
		return DocumentDAO.getDocumentIdByThreadId(multipleFormatMail.getGmailMessage().getThreadId());
	}
	
	public static Document getDocument(MultipleFormatMail multipleFormatMail) {
		Document doc = new Document();
		doc.setdocumentId(DocumentDAO.getDocumentIdByThreadId(multipleFormatMail.getGmailMessage().getThreadId()));
		return doc;
	}

}
