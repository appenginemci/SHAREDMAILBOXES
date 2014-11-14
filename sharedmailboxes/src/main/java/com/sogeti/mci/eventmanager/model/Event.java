package com.sogeti.mci.eventmanager.model;

public class Event {
	private Long id;
	private String eventName;
	private String googleGroupId;
	private String folderId;
	private String email;
	private String inboxNewFolderId;
	private String attachmentFolderId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getGoogleGroupId() {
		return googleGroupId;
	}
	public void setGoogleGroupId(String googleGroupId) {
		this.googleGroupId = googleGroupId;
	}
	public String getFolderId() {
		return folderId;
	}
	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getInboxNewFolderId() {
		return inboxNewFolderId;
	}
	public void setInboxNewFolderId(String inboxNewFolderId) {
		this.inboxNewFolderId = inboxNewFolderId;
	}
	public String getAttachmentFolderId() {
		return attachmentFolderId;
	}
	public void setAttachmentFolderId(String attachmentFolderId) {
		this.attachmentFolderId = attachmentFolderId;
	}
	public String toString(){
		String value = "id: " + id + "\n"
					+ "eventName: " + eventName + "\n" 
					+ "googleGroupId: " + googleGroupId + "\n"
					+ "folderId: " + folderId + "\n"
					+ "email: " + email + "\n"
					+ "inboxNewFolderId: " + inboxNewFolderId + "\n"
					+ "attachmentFolderId: " + attachmentFolderId;
		return value;
	}

}
