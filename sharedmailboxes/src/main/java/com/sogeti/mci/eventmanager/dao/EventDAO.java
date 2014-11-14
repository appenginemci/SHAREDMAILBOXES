package com.sogeti.mci.eventmanager.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import com.sogeti.mci.eventmanager.model.Event;

public class EventDAO {
	
	final static String QUERY_EVENTS = "SELECT * FROM event";	
	final static String QUERY_SINGLE_EVENT = "SELECT * FROM event WHERE email='";
	
	private HashMap<String, Event> events = new HashMap<String, Event>();
	private LoggerDAO logger = LoggerDAO.getInstance();

	private static EventDAO instance = null;

	public static EventDAO getInstance() {
		if(instance == null) {
			synchronized (EventDAO.class){
				if(instance == null) {
					instance = new EventDAO();
				}
			}
		}
		return instance;
	}

	protected EventDAO()  {	

	}
	
	

	public Collection<Event> getAllEvents() {
		Collection<Event> events = new ArrayList<Event>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try {
			conn = InitializatorDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("QUERY_EVENTS");
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				Event ev = new Event();
				ev.setId(resultSet.getLong("id"));
				ev.setEventName(resultSet.getString("eventName"));
				ev.setGoogleGroupId(resultSet.getString("googleGroupId"));
				ev.setFolderId(resultSet.getString("folderId"));
				ev.setEmail(resultSet.getString("email"));
				ev.setInboxNewFolderId(resultSet.getString("inboxNewFolderId"));
				ev.setAttachmentFolderId(resultSet.getString("attachmentFolderId"));
				events.add(ev);
				logger.debug("Retrieved event: " + ev.toString());
			}
		} catch (Exception e){
			logger.error("Error while retrieving all events from database", e);
			//System.out.println("Unable to retrieve the events from database");
			//System.out.println(LoggerDAO.getStackTrace(e));
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();	
			} catch (Exception e){
				// do not log
			}
		}
		return events;
	}
	
	public Event getEventByRecipient(String eventEmail){
		Event ev = null;
		Statement stmt = null;
		Connection conn = null;
		if (events.containsKey(eventEmail)) {
			ev = events.get(eventEmail);
		} else {
			try {
				conn = InitializatorDAO.getInstance().getConnection();
				stmt = conn.createStatement();
				ResultSet resultSet = stmt.executeQuery(QUERY_SINGLE_EVENT + eventEmail + "'");
				if (resultSet.first()) {
					ev = new Event();
					ev.setId(resultSet.getLong("id"));
					ev.setEventName(resultSet.getString("eventName"));
					ev.setGoogleGroupId(resultSet.getString("googleGroupId"));
					ev.setFolderId(resultSet.getString("folderId"));
					ev.setEmail(resultSet.getString("email"));
					ev.setInboxNewFolderId(resultSet.getString("inboxNewFolderId"));
					ev.setAttachmentFolderId(resultSet.getString("attachmentFolderId"));
					events.put(eventEmail, ev);
					logger.debug("Retrieved event: " + ev.toString());
				}
			} catch (Exception e){
				logger.error("Error while retrieving all event " + eventEmail + " from database", e);
				//System.out.println("Unable to retrieve event info from database, for event " + eventEmail);
				//System.out.println(LoggerDAO.getStackTrace(e));
			} finally {
				try {
					if (stmt != null) stmt.close();
					if (conn != null) conn.close();	
				} catch (Exception e){
					// do not log
				}
			}
		}
		return ev;
	}

	/*public static Event getEventByRecipient(String e) {
		Event test = new Event();
		test.setIdFolderAttachment(ConstantList.idAttachementFolderMCI);
		test.setIdFolderNew(ConstantList.idFolderMCI);
		test.setIdFolderTemporary(ConstantList.idTemporaryFolderMCI);
		test.setRecipient("Recipient Name");
		test.setIdFolderRoot("Inconnu");
		test.setName("Name");
		test.setId(1L);
		return test;
	}*/

}
