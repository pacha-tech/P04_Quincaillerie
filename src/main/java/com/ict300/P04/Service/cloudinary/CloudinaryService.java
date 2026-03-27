package com.ict300.P04.Service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService() {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dkoc01x1b",
                "api_key", "771337426176371", // Remplacez par votre clé réelle
                "api_secret", "votre_secret_reel" // Remplacez par votre secret réel
        ));
    }

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            log.info("Téléchargement de l'image vers Cloudinary...");
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "products",
                            "resource_type", "auto"
                    ));

            String url = uploadResult.get("secure_url").toString();
            log.info("Image téléchargée avec succès: {}", url);
            return url;
        } catch (IOException e) {
            log.error("Erreur lors de l'upload vers Cloudinary", e);
            throw new IOException("Impossible de télécharger l'image : " + e.getMessage());
        }
    }
}