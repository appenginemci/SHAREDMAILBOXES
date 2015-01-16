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
import com.google.api.services.drive.model.ParentList;
import com.google.api.services.drive.model.ParentReference;
import com.sogeti.mci.eventmanager.authentication.CredentialLoader;
import com.sogeti.mci.eventmanager.dao.DriveDAO;
import com.sogeti.mci.eventmanager.dao.SettingsDAO;
import com.sogeti.mci.eventmanager.helper.ConstantList;
import com.sogeti.mci.eventmanager.model.Document;
import com.sogeti.mci.eventmanager.model.MultipleFormatMail;

public class DriveService {

	static Drive service = CredentialLoader.getDriveService();

	static File storeAttachmentToDrive(ByteArrayOutputStream baos,
			MultipleFormatMail multipleFormatEmail, String name, String description,
			String mimetypeBody, String mimetypeFile, String extension)
			 {
		System.out.println("Storing attachment file " + name + " in folder "
				+ multipleFormatEmail.getNameEmail());

		File mciFolder = getRecipientFolderById(multipleFormatEmail.getEvent().getAttachmentFolderId());

		try {
			mciFolder = createFolder(multipleFormatEmail.getNameEmail(), mciFolder.getId());
		} catch (IOException | GeneralSecurityException | URISyntaxException e) {
			// TODO LOG IN DB
			e.printStackTrace();
		} 

		return doAttachementInsertion(baos, name, description, mimetypeBody, mimetypeFile,
				extension, service, mciFolder);

	}

	public static File storeToDrive(ByteArrayOutputStream baos, MultipleFormatMail multipleFormatEmail,
			String description, String mimetypeBody, String mimetypeFile,
			String extension) throws IOException, GeneralSecurityException,
			URISyntaxException {
		System.out.print("Storing file " + multipleFormatEmail.getNameEmail() + " in folder Id ");
		Drive service = CredentialLoader.getDriveService();
		
		ByteArrayContent mediaContent = new ByteArrayContent(mimetypeFile,baos.toByteArray());
		String folderId = null;
		File destFolder = null;	
		File body = null;
		 
		if (multipleFormatEmail.isNewEmail()) {
			folderId = multipleFormatEmail.getEvent().getInboxNewFolderId();
			destFolder = getRecipientFolderById(folderId);
			//Create a new empty file for receiving the copy 
			body = createGoogleFile(multipleFormatEmail.getNameEmail(), description, mimetypeBody, destFolder);
			//Copy the reply add-on on the targeted location
			body = copyFile(service, SettingsDAO.getInstance().getSetting("templateDocId"),body);
		} else {
			folderId = getParentFolderId(multipleFormatEmail.getDocument().getdocumentId());	
			destFolder = getRecipientFolderById(folderId);
			body = DriveDAO.getFile(multipleFormatEmail.getDocument().getdocumentId());
		}

		System.out.println(" ("+folderId+")");
		
		return updateFile(service, body.getId(), mediaContent);	
	}

	private static File getRecipientFolderById(String idFolder) {
		File file = new File();
		file.setId(idFolder);
		return file;
	}
	
	public static String getParentFolderId(String fileId) {
		String folderId = null;
	    try {
	      ParentList parents = service.parents().list(fileId).execute();

	      for (ParentReference parent : parents.getItems()) {
	        //System.out.println("File Id: " + parent.getId());
	        folderId = parent.getId();
	      }
	    } catch (IOException e) {
	      System.out.println("An error occurred: " + e);
	    }
	    return folderId;
	}

	@SuppressWarnings("unused")
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

	private static File doAttachementInsertion(ByteArrayOutputStream baos, String name,
			String description, String mimetypeBody, String mimetypeFile,
			String extension, Drive service, File mciFolder)  {
		File body = createGoogleFile(name, description, mimetypeBody, mciFolder);
				
		ByteArrayContent mediaContent = new ByteArrayContent(mimetypeFile,	baos.toByteArray());

		File file = null;
		try {
			file = service.files().insert(body, mediaContent).execute();
		} catch (IOException e) {
			// TODO LOG IN DB
			e.printStackTrace();
		}

		return file;
	}

	private static File createGoogleFile(String name, String description,
			String mimetypeBody, File mciFolder) {
		// Insert a file
		File body = new File();
		body.setTitle(name);
		body.setDescription(description);
		body.setMimeType(mimetypeBody);

		if (mciFolder.getId() != null && mciFolder.getId().length() > 0) {
			body.setParents(Arrays.asList(new ParentReference().setId(mciFolder
					.getId())));
		}
		return body;
	}


	private static File copyFile(Drive service, String originFileId,
			File copy) {
		    try {
		      return service.files().copy(originFileId, copy).execute();
		    } catch (IOException e) {
		      System.out.println("An error occurred: " + e);
		    }
		    return null;
	 }


	private static File updateFile(Drive service, String fileId, ByteArrayContent mediaContent) {
		    try {
		      // First retrieve the file from the API.
		      File file = service.files().get(fileId).execute();
		      file.getLabels().setViewed(false);
		      
		      // Send the request to the API.
		      File updatedFile = service.files().update(fileId, file, mediaContent).execute();
		      
		      
		      return updatedFile;
		    } catch (IOException e) {
		      System.out.println("An error occurred: " + e);
		      return null;
		    }
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

	public static boolean deleteFiles(Document document) {
		boolean deleteSuccess = true;
		if (document.getdocumentId()!=null) {
			deleteSuccess = deleteSuccess && DriveDAO.deleteFile(document.getdocumentId());
		}
		if (document.getAttachmentIds()!=null) {
			for (String id : document.getAttachmentIds()) {
				deleteSuccess = deleteSuccess && DriveDAO.deleteFile(id);
			}
		}
		return deleteSuccess;
	}

}
