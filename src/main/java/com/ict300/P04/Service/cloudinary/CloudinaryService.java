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
        return uploadToFolder(file, "products");
    }

    public String uploadPhoto(MultipartFile file) throws IOException {
        return uploadToFolder(file, "photos");
    }

    public String uploadFacturePdf(byte[] pdfBytes, String fileName) throws IOException {
        return uploadFactureToFolder(pdfBytes, "factures", fileName);
    }


    private String uploadToFolder(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            log.info("Téléchargement vers Cloudinary (Dossier: {})...", folder);
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "auto"
                    ));

            String url = uploadResult.get("secure_url").toString();
            log.info("Téléchargement réussi: {}", url);
            return url;
        } catch (IOException e) {
            log.error("Erreur Cloudinary dans le dossier {}: {}", folder, e.getMessage());
            throw new IOException("Erreur lors du téléchargement vers Cloudinary : " + e.getMessage());
        }
    }

    private String uploadFactureToFolder(byte[] data, String folder, String fileName) throws IOException {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            log.info("Téléchargement du PDF vers Cloudinary (Dossier: {})...", folder);
            Map<?, ?> uploadResult = cloudinary.uploader().upload(data,
                    ObjectUtils.asMap(
                            "folder", folder,
                            "public_id", fileName,
                            "resource_type", "raw" // CRUCIAL : "raw" pour les fichiers PDF/Non-image
                    ));

            String url = uploadResult.get("secure_url").toString();
            log.info("PDF téléchargé avec succès: {}", url);
            return url;
        } catch (IOException e) {
            log.error("Erreur Cloudinary lors de l'upload du PDF {}: {}", fileName, e.getMessage());
            throw new IOException("Erreur lors de l'upload du PDF : " + e.getMessage());
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return;

        try {

            String publicId = extractPublicId(imageUrl);

            log.info("Suppression de l'image sur Cloudinary, Public ID: {}", publicId);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        } catch (Exception e) {
            log.error("Erreur lors de la suppression sur Cloudinary: {}", e.getMessage());
        }
    }

    private String extractPublicId(String url) {

        String split = url.split("/upload/")[1];

        if (split.contains("/")) {
            split = split.substring(split.indexOf("/") + 1);
        }

        return split.substring(0, split.lastIndexOf("."));
    }
}