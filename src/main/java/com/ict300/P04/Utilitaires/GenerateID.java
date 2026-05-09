package com.ict300.P04.Utilitaires;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Component
public class GenerateID {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String GenerateUserID() {
        StringBuilder sb = new StringBuilder("USER");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateQuincaillerieID() {
        StringBuilder sb = new StringBuilder("QUIN");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateProductID() {
        StringBuilder sb = new StringBuilder("PROD");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateCategoryID() {
        StringBuilder sb = new StringBuilder("CATE");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GeneratePriceID() {
        StringBuilder sb = new StringBuilder("PRICE");
        for (int i = 0; i < 5; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateReportID() {
        StringBuilder sb = new StringBuilder("REPO");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateFavoriteQuincaillerieID() {
        StringBuilder sb = new StringBuilder("FAQU");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateFavoriteProductID() {
        StringBuilder sb = new StringBuilder("FAPR");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GeneratePanierID() {
        StringBuilder sb = new StringBuilder("PANI");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateCampagneID() {
        StringBuilder sb = new StringBuilder("CAMP");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GeneratePromotionID() {
        StringBuilder sb = new StringBuilder("PROMO");
        for (int i = 0; i < 5; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateMessageID() {
        StringBuilder sb = new StringBuilder("MESS");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateConversationID() {
        StringBuilder sb = new StringBuilder("CONV");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateLignePanierID() {
        StringBuilder sb = new StringBuilder("LIPA");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateLigneCommandeID() {
        StringBuilder sb = new StringBuilder("LICO");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateDetailCommandeID() {
        StringBuilder sb = new StringBuilder("DECO");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateRetraitCodeID() {
        StringBuilder sb = new StringBuilder("RECO");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateDetailRetraitID() {
        StringBuilder sb = new StringBuilder("DERE");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateCommandeID() {
        String dateDuJour = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        StringBuilder sb = new StringBuilder("CMD-");
        sb.append(dateDuJour).append("-");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String GenerateFactureID() {
        String dateDuJour = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        StringBuilder sb = new StringBuilder("FAC-");
        sb.append(dateDuJour).append("-");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
