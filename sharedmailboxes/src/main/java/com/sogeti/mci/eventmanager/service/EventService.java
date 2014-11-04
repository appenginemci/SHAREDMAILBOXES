package com.sogeti.mci.eventmanager.service;

import java.util.Collection;

import javax.mail.Address;

import com.sogeti.mci.eventmanager.dao.EventDAO;
import com.sogeti.mci.eventmanager.model.Event;

public class EventService {	

	public static Collection<Event> getAllEvents() {
		return EventDAO.getAllEvents();
	}

	public static Event getEventByRecipient(Address[] addresses) {
		Address address = addresses[0];
		return EventDAO.getEventByRecipient(address.toString());
	}

}
