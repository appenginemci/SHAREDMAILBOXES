package com.sogeti.mci.eventmanager.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.sogeti.mci.eventmanager.authentication.CredentialLoader;
import com.sogeti.mci.eventmanager.helper.ConstantList;

public class DriveService {

	static Drive service = CredentialLoader.getDriveService();

	static File storeAttachmentToDrive(ByteArrayOutputStream baos,
			String folder, String name, String description,
			String mimetypeBody, String mimetypeFile, String extension)
			throws IOException, GeneralSecurityException, URISyntaxException {
		System.out.println("Storing attachment file " + name + " in folder "
				+ folder);

		File mciFolder = getRecipientFolderById(ConstantList.idAttachementFolderMCI);

		mciFolder = createFolder(folder, mciFolder.getId());

		return doInsertion(baos, name, description, mimetypeBody, mimetypeFile,
				extension, service, mciFolder);

	}

	public static File storeToDrive(ByteArrayOutputStream baos, String name,
			String description, String mimetypeBody, String mimetypeFile,
			String extension) throws IOException, GeneralSecurityException,
			URISyntaxException {
		System.out.println("Storing file " + name + " in folder new");
		Drive service = CredentialLoader.getDriveService();
		// File mciFolder = createRecipientFolder(service);

		File mciFolder = getRecipientFolderById(ConstantList.idFolderMCI);

		return doInsertion(baos, name, description, mimetypeBody, mimetypeFile,
				extension, service, mciFolder);

	}

	private static File getRecipientFolderById(String idFolder) {
		File file = new File();
		file.setId(idFolder);
		return file;
	}

	private static File createRecipientFolder(Drive service)
			throws IOException, GeneralSecurityException, URISyntaxException {

		File mciFolder = createFolder(ConstantList.MCIFOLDER, service.about().get()
				.execute().getRootFolderId());

		File mciFolder1 = createFolder("GoogleForWork-Zurich2015",
				mciFolder.getId());

		File mciFolder2 = createFolder("20-Inbox", mciFolder1.getId());

		File mciFolder3 = createFolder("10-New", mciFolder2.getId());

		return mciFolder3;
	}

	private static File createFolder(String name, String parentId)
			throws IOException, GeneralSecurityException, URISyntaxException {
		File mciFolder = getMCIFolder(name, parentId);
		if (mciFolder == null) {
			mciFolder = createMCIFolder(name, parentId);
		}
		return mciFolder;
	}

	private static File doInsertion(ByteArrayOutputStream baos, String name,
			String description, String mimetypeBody, String mimetypeFile,
			String extension, Drive service, File mciFolder) throws IOException {
		// Insert a file
		File body = new File();
		body.setTitle(name);
		body.setDescription(description);
		body.setMimeType(mimetypeBody);

		if (mciFolder.getId() != null && mciFolder.getId().length() > 0) {
			body.setParents(Arrays.asList(new ParentReference().setId(mciFolder
					.getId())));
		}

		ByteArrayContent mediaContent = new ByteArrayContent(mimetypeFile,
				baos.toByteArray());

		File file = service.files().insert(body, mediaContent).execute();

		return file;
	}

	private static File getMCIFolder(String folderName,
			String parentId) throws IOException, GeneralSecurityException,
			URISyntaxException {

		File toReturn = null;

		Drive.Files.List request;
		request = service.files().list();

		String query = "mimeType='application/vnd.google-apps.folder' AND trashed=false AND title='"
				+ folderName + "' AND '" + parentId + "' in parents";
		request = request.setQ(query);
		FileList files = request.execute();

		if (files.getItems().size() > 0) {
			toReturn = files.getItems().get(0);
		}

		return toReturn;
	}

	private static File createMCIFolder(String folderName,
			String parentId) throws IOException {
		File toReturn = new File();
		toReturn.setTitle(folderName);
		toReturn.setMimeType("application/vnd.google-apps.folder");
		toReturn.setParents(Arrays.asList(new ParentReference().setId(parentId)));
		toReturn = service.files().insert(toReturn).execute();

		return toReturn;
	}

	public static boolean existsDocument(String name, String parentId) throws IOException {

		boolean toReturn = true;

		Drive.Files.List request;
		request = service.files().list();

		String query = "mimeType='application/vnd.google-apps.document' AND trashed=false AND title=\""
				+ name + "\" AND '" + parentId + "' in parents";
		request = request.setQ(query);
		try {
			FileList list = request.execute();
			toReturn = list.getItems().size()>0;
		} catch (Exception e) {
			toReturn = false;
		}

		return toReturn;
	}
}
