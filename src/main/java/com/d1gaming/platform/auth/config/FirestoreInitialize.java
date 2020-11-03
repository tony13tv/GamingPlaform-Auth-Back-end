package com.d1gaming.platform.auth.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;

@Service
public class FirestoreInitialize {
	
	@PostConstruct
	public void initialize() {
		try {
			FileInputStream serviceAccount =
			new FileInputStream("./firestore-test-ea74b51732de.json");
			FirebaseOptions options  = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setDatabaseUrl("https://firestore-test-3d7eb.firebaseio.com")
					.build();
			FirebaseApp.initializeApp(options);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
