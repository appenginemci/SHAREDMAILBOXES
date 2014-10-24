package com.sogeti.mci.eventmanager.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.Thread;
import com.sogeti.mci.eventmanager.authentication.CredentialLoader;

public class GmailService {

	static Gmail service = CredentialLoader.getGmailService();

	private static List<Message> listMessagesMatchingQuery(String userId,
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
}
