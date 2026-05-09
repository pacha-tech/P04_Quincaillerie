package com.ict300.P04.Service.cloudinary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class UploadImage {
    @Autowired
    private CloudinaryService cloudinaryService;

    public String uploadImageSafely(MultipartFile image) {
        if (image == null || image.isEmpty()) return null;
        try {
            return cloudinaryService.uploadImageProduct(image);
        } catch (IOException e) {
            log.error("Erreur Cloudinary: {}", e.getMessage());
            return null;
        }
    }

    public String uploadPhotoSafely(MultipartFile image) {
        if (image == null || image.isEmpty()) return null;
        try {
            return cloudinaryService.uploadPhoto(image);
        } catch (IOException e) {
            log.error("Erreur Cloudinary: {}", e.getMessage());
            return null;
        }
    }

    public String uploadPdfSafely(byte[] pdfBytes, String fileName) {
        if (pdfBytes == null || pdfBytes.length == 0) {
            log.warn("Tentative d'upload d'un PDF vide ou nul.");
            return null;
        }

        try {
            log.info("Début de l'upload du PDF : {}", fileName);
            return cloudinaryService.uploadFacturePdf(pdfBytes, fileName);
        } catch (IOException e) {
            log.error("Erreur lors de l'upload du PDF {} vers Cloudinary: {}", fileName, e.getMessage());
            return null;
        }
    }
}
