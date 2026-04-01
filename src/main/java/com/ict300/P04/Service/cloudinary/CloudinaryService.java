package com.ict300.P04.Service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(@Value("${cloudinary.cloud_name}") String cloudName,
                             @Value("${cloudinary.api_key}") String apiKey,
                             @Value("${cloudinary.api_secret}") String apiSecret) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String uploadImageProduct(MultipartFile file) throws IOException {
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

    public String uploadPgoto(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            log.info("Téléchargement de la photo vers Cloudinary...");
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "photos",
                            "resource_type", "auto"
                    ));

            String url = uploadResult.get("secure_url").toString();
            log.info("Photo téléchargée avec succès: {}", url);
            return url;
        } catch (IOException e) {
            log.error("Erreur lors de l'upload vers Cloudinary", e);
            throw new IOException("Impossible de télécharger la photo : " + e.getMessage());
        }
    }
}