package com.sogeti.mci.eventmanager.dao;

import java.io.IOException;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.sogeti.mci.eventmanager.authentication.CredentialLoader;

public class DriveDAO {
	
	static Drive service = CredentialLoader.getDriveService();
	
	
	public static File getFile(String id) {
		File file = null;
		try {
			file = service.files().get(id).execute();
		} catch (IOException e) {
			// TODO LOG IN DB
			e.printStackTrace();
		}
		return file;
	}
	
	
	public static boolean deleteFile(String id) {
		boolean deleteOk = false;
		try {
			service.files().delete(id).execute();
			deleteOk = true;
		} catch (IOException e) {
			// TODO LOG IN DB
			e.printStackTrace();
		}
		return deleteOk;
	}

}
