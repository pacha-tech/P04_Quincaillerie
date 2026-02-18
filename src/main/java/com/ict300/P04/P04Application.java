package com.ict300.P04;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
public class P04Application {

	public static void main(String[] args) {
		// 1. INITIALISATION DE FIREBASE (AVANT TOUT LE RESTE)
		try {
			if (FirebaseApp.getApps().isEmpty()) {
				ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");
				FirebaseOptions options = FirebaseOptions.builder()
						.setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
						.build();

				FirebaseApp.initializeApp(options);
				System.out.println("✅ LOG : Firebase Admin SDK est prêt et allumé !");
			}
		} catch (Exception e) {
			System.err.println("❌ ERREUR : Échec de l'allumage de Firebase : " + e.getMessage());
			// Optionnel : arrêter l'app si Firebase est indispensable
			// System.exit(1);
		}

		// 2. DÉMARRAGE DE SPRING BOOT
		SpringApplication.run(P04Application.class, args);
	}
}
