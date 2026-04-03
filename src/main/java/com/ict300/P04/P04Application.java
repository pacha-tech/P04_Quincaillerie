package com.ict300.P04;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@EnableScheduling
public class P04Application {

	public static void main(String[] args) {

		try {
			if (FirebaseApp.getApps().isEmpty()) {
				InputStream serviceAccount;


				String firebaseConfig = System.getenv("FIREBASE_CONFIG");

				if (firebaseConfig != null && !firebaseConfig.isEmpty()) {

					serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));
					System.out.println("✅ LOG : Firebase initialisé via Variable d'Environnement (Render)");
				} else {

					ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");
					serviceAccount = resource.getInputStream();
					System.out.println("🏠 LOG : Firebase initialisé via fichier local (serviceAccountKey.json)");
				}

				FirebaseOptions options = FirebaseOptions.builder()
						.setCredentials(GoogleCredentials.fromStream(serviceAccount))
						.build();

				FirebaseApp.initializeApp(options);
			}
		} catch (Exception e) {
			System.err.println("❌ ERREUR CRITIQUE : Échec de l'allumage de Firebase : " + e.getMessage());
		}

		// 2. DÉMARRAGE DE SPRING BOOT
		SpringApplication.run(P04Application.class, args);
	}
}
