package com.sogeti.mci.eventmanager.model;

public class Event {
	
	private String recipient;
	private String name;
	private Long id;
	private String idFolderRoot;
	private String idFolderNew;
	private String idFolderAttachment;
	private String idFolderProgress;
	private String idFolderClosed;
	
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getIdFolderNew() {
		return idFolderNew;
	}
	public void setIdFolderNew(String idFolderNew) {
		this.idFolderNew = idFolderNew;
	}
	public String getIdFolderAttachment() {
		return idFolderAttachment;
	}
	public void setIdFolderAttachment(String idFolderAttachment) {
		this.idFolderAttachment = idFolderAttachment;
	}
	public String getIdFolderProgress() {
		return idFolderProgress;
	}
	public void setIdFolderProgress(String idFolderProgress) {
		this.idFolderProgress = idFolderProgress;
	}
	public String getIdFolderClosed() {
		return idFolderClosed;
	}
	public void setIdFolderClosed(String idFolderClosed) {
		this.idFolderClosed = idFolderClosed;
	}
	public String getIdFolderRoot() {
		return idFolderRoot;
	}
	public void setIdFolderRoot(String idFolderRoot) {
		this.idFolderRoot = idFolderRoot;
	}
	
	

}
