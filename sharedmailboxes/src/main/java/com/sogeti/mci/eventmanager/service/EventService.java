package com.sogeti.mci.eventmanager.service;

import java.util.Collection;



import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import com.sogeti.mci.eventmanager.dao.EventDAO;
import com.sogeti.mci.eventmanager.dao.LoggerDAO;
import com.sogeti.mci.eventmanager.dao.SettingsDAO;
import com.sogeti.mci.eventmanager.model.Event;

public class EventService {	

	/*public static Collection<Event> getAllEvents() {
		return EventDAO.getAllEvents();
	}*/
	

	public static Event getEventByRecipient(Address[] addresses) {
		Event ev = null;
		String pattern = SettingsDAO.getInstance().getSetting("patternEventEmail");
		if (addresses != null && addresses.length > 0){
			for (int i=0; i<addresses.length; i++){
				InternetAddress tmpAddr = (InternetAddress)addresses[i];
				if (tmpAddr.getAddress().matches(pattern)){
					ev = EventDAO.getInstance().getEventByRecipient(tmpAddr.getAddress());
					if (ev != null) break;
				} else {
					LoggerDAO.getInstance().debug(tmpAddr.getAddress() + " does not match with the pattern " + pattern);
					System.out.println(tmpAddr.getAddress() + " does not match with the pattern " + pattern);
				}
			}
		}
		return ev;
	}

}
