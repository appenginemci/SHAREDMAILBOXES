package com.sogeti.mci.eventmanager.dao;

import java.util.ArrayList;
import java.util.Collection;

import com.sogeti.mci.eventmanager.helper.ConstantList;
import com.sogeti.mci.eventmanager.model.Event;

public class EventDAO {
	
	public static Collection<Event> getAllEvents() {
		Collection<Event> returnedCollection = new ArrayList<Event>();
		//Connect to DB
		// retrieve all Events
		Event test = new Event();
		test.setIdFolderAttachment(ConstantList.idAttachementFolderMCI);
		test.setIdFolderNew(ConstantList.idFolderMCI);
		test.setRecipient("Recipient Name");
		test.setId(1L);
		
		returnedCollection.add(test);
		
		return returnedCollection;		
	}

	public static Event getEventByRecipient(String string) {
		Event test = new Event();
		test.setIdFolderAttachment(ConstantList.idAttachementFolderMCI);
		test.setIdFolderNew(ConstantList.idFolderMCI);
		test.setRecipient("Recipient Name");
		test.setName("Name");
		test.setId(1L);
		return test;
	}

}
