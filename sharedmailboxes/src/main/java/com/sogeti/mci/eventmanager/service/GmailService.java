package com.sogeti.mci.eventmanager.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.google.api.services.gmail.model.Thread;
import com.sogeti.mci.eventmanager.authentication.CredentialLoader;

public class GmailService {

	static Gmail service = CredentialLoader.getGmailService();

	public static List<Message> listMessagesMatchingQuery(String userId,
			String query) throws IOException {
		ListMessagesResponse response = service.users().messages().list(userId)
				.setQ(query).execute();

		List<Message> messages = new ArrayList<Message>();
		while (response.getMessages() != null) {
			messages.addAll(response.getMessages());
			if (response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();
				response = service.users().messages().list(userId).setQ(query)
						.setPageToken(pageToken).execute();
			} else {
				break;
			}
		}

		// for (Message message : messages) {
		// System.out.println(message.toPrettyString());
		// }

		return messages;
	}

	public static List<com.google.api.services.gmail.model.Thread> listThreadsMatchingQuery(
			String userId, String query) throws IOException {
		ListThreadsResponse response = service.users().threads().list(userId)
				.setQ(query).execute();
		List<com.google.api.services.gmail.model.Thread> threads = new ArrayList<com.google.api.services.gmail.model.Thread>();
		while (response.getThreads() != null) {
			threads.addAll(response.getThreads());
			if (response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();
				response = service.users().threads().list(userId).setQ(query)
						.setPageToken(pageToken).execute();
			} else {
				break;
			}
		}
		//
		// for(Thread thread : threads) {
		// System.out.println(thread.toPrettyString());
		// }

		return threads;
	}

	public static Thread getThreadById(String userId, String threadId)
			throws IOException {
		return service.users().threads().get(userId, threadId).execute();
	}

	public static Message getLastMessageFromThread(String userId, Thread thread) throws IOException {
		Thread t = getThreadById(userId, thread.getId());
		return t.getMessages().get(t.getMessages().size() - 1);
	}

	public static Message getMessageById(String userId, String id) throws IOException {
		return service.users().messages().get(userId, id).execute();
	}

	public static Message getRawMessageById(String userId, String id) throws IOException {
		return service.users().messages().get(userId, id).setFormat("raw").execute();
	}
	
	public static MessagePartBody getMessagePartBody(String userId, String id, String attId) throws IOException {
		return service.users().messages().attachments().get(userId, id, attId).execute();
	}
	
	public static void main (String args[]) throws IOException {
		String userId = "mimoun.chikhi@capgemini-sogeti.com";
		List<Message> list = listMessagesMatchingQuery(userId, "in:inbox  is:unread");
		
//		Label label1 = createLabel(userId, "label1");
//		List<String> listLabel1 = new ArrayList<String>();
//		listLabel1.add(label1.getId());
		

//		Label label2 = createLabel(userId, "label2");
//		List<String> listLabel2 = new ArrayList<String>();
//		listLabel2.add(label2.getId());
		
//		for (Message message : list) {
//			Message m = getMessageById(userId, message.getId());
//		    ModifyMessageRequest mods = new ModifyMessageRequest().setAddLabelIds(listLabel);
//		    m = service.users().messages().modify(userId, message.getId(), mods).execute();
//			System.out.println(m.getId() +" - "+ m.toPrettyString());
//		}
		
//		System.out.println();
//		List<Thread> threads = listThreadsMatchingQuery(userId, "in:inbox  is:unread");
//		
//		for (Thread t : threads) {
//			t = getThreadById(userId, t.getId());
//			 List<Message> ts = t.getMessages();
//			 int i=1;
//			 System.out.println(ts.size());
//			 for (Message message : ts) {
//				    ModifyMessageRequest mods1 = new ModifyMessageRequest().setAddLabelIds(listLabel1);
//				    ModifyMessageRequest mods2 = new ModifyMessageRequest().setAddLabelIds(listLabel2);
//				    if (i==2) {
//					    message = service.users().messages().modify(userId, message.getId(), mods1).execute();
//				    }
//				 System.out.println(message.getId()+" - "+ message.toPrettyString());
//				 i++;
//			}
//		}
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		
		List<Message> messages = listMessagesMatchingQuery(userId, "in:inbox  is:unread -label:label1");
		System.out.println(messages.size());
		for (Message t : messages) {
		    //ModifyMessageRequest mods1 = new ModifyMessageRequest().setAddLabelIds(listLabel1);

		    //t = service.users().messages().modify(userId, t.getId(), mods1).execute();
				 Message message = getMessageById(userId, t.getId());
				 System.out.println(message.getId());
				 System.out.println(message.getLabelIds());
		}
		
	}
	
	public static Message labelizeMessage(String userId, String messageId, List<String> labels) throws IOException {
	    ModifyMessageRequest mods1 = new ModifyMessageRequest().setAddLabelIds(labels);
	    return service.users().messages().modify(userId, messageId, mods1).execute();
	}
		
  private static Label createLabel( String userId, String newLabelName) throws IOException {
	    Label label = new Label().setName(newLabelName).setMessageListVisibility("show").setLabelListVisibility("labelShow");
	    label = service.users().labels().create(userId, label).execute();

	    System.out.println("Label id: " + label.getId());
	    System.out.println(label.toPrettyString());

	    return label;
	  }

  private static void deleteLabel( String userId, String labelId) throws IOException{
	    service.users().labels().delete(userId, labelId).execute();
	    System.out.println("Label with id: " + labelId + " deleted successfully.");
	  }	

}
