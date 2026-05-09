package com.ict300.P04.Service.facture;

import com.ict300.P04.Entite.Commande;
import com.ict300.P04.Entite.Facture;
import com.ict300.P04.Entite.LigneCommande;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Service.cloudinary.UploadImage;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.facture.FactureInterface;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FactureService {

    @Autowired
    private FactureInterface factureInterface;

    @Autowired
    private UploadImage uploadImage;

    public String generateFacture(Commande commande, User user, String transactionId, String modePaiement) {

        String fileName = "Facture_" + commande.getIdCommande() + "_" + transactionId;

        BigDecimal tauxTva = new BigDecimal("0.1925");
        BigDecimal diviseurTtc = new BigDecimal("1.1925");

        BigDecimal totalTTC = commande.getMontantTotal();

        BigDecimal totalHT = totalTTC.divide(diviseurTtc, 2, RoundingMode.HALF_UP);

        BigDecimal totalTVA = totalTTC.subtract(totalHT);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            // 2. CONFIGURATION DES POLICES
            Font fontTitreDoc = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(44, 62, 80));
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font fontNormalGris = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font fontTableauHead = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

            // 3. EN-TÊTE DU DOCUMENT (Design Pro avec deux colonnes)
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1f, 1f});

            // Info Quincaillerie (Gauche)
            PdfPCell cellFournisseur = new PdfPCell();
            cellFournisseur.setBorder(Rectangle.NO_BORDER);
            String nomStore = (commande.getQuincaillerie() != null) ? commande.getQuincaillerie().getStoreName() : "Quincaillerie Pro";
            cellFournisseur.addElement(new Paragraph(nomStore.toUpperCase(), fontHeader));
            cellFournisseur.addElement(new Paragraph("NIU : M000000000000X", fontNormalGris)); // À remplacer par les vraies infos
            cellFournisseur.addElement(new Paragraph("RCCM : RC/YAO/202X/B/000", fontNormalGris));
            cellFournisseur.addElement(new Paragraph("Adresse : Douala/Yaoundé, Cameroun", fontNormalGris));
            headerTable.addCell(cellFournisseur);

            // Info Facture (Droite)
            PdfPCell cellFactureInfo = new PdfPCell();
            cellFactureInfo.setBorder(Rectangle.NO_BORDER);
            cellFactureInfo.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph titre = new Paragraph("FACTURE", fontTitreDoc);
            titre.setAlignment(Element.ALIGN_RIGHT);
            cellFactureInfo.addElement(titre);

            Paragraph infoFact = new Paragraph(
                    "N° Facture : " + commande.getIdCommande() + "\n" +
                            "Date : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                            "Paiement : " + modePaiement, fontNormal
            );
            infoFact.setAlignment(Element.ALIGN_RIGHT);
            cellFactureInfo.addElement(infoFact);
            headerTable.addCell(cellFactureInfo);

            document.add(headerTable);
            document.add(new Paragraph("\n"));

            // 4. INFO CLIENT
            PdfPTable clientTable = new PdfPTable(1);
            clientTable.setWidthPercentage(100);
            PdfPCell cellClient = new PdfPCell(new Paragraph("Facturé de Mr : " + user.getName(), fontNormal));
            cellClient.setBorderColor(Color.LIGHT_GRAY);
            cellClient.setPadding(10);
            cellClient.setBackgroundColor(new Color(245, 245, 245));
            clientTable.addCell(cellClient);
            document.add(clientTable);
            document.add(new Paragraph("\n"));

            // 5. TABLEAU DES LIGNES DE COMMANDE
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.5f, 1f, 1.5f, 1.5f}); // Largeur des colonnes

            // En-têtes du tableau (Fond sombre, texte blanc)
            String[] headers = {"Désignation", "Qté", "Prix Unitaire TTC", "Montant TTC"};
            for (String title : headers) {
                PdfPCell header = new PdfPCell(new Phrase(title, fontTableauHead));
                header.setBackgroundColor(new Color(44, 62, 80));
                header.setPadding(8);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(header);
            }

            // Contenu du tableau
            for (LigneCommande ligne : commande.getLigneCommandes()) {
                table.addCell(createCell(ligne.getPrice().getProduct().getName(), fontNormal, Element.ALIGN_LEFT));
                table.addCell(createCell(String.valueOf(ligne.getQuantity()), fontNormal, Element.ALIGN_CENTER));
                table.addCell(createCell(ligne.getHistoricalPrice().toString() + " FCFA", fontNormal, Element.ALIGN_RIGHT));

                BigDecimal sousTotal = ligne.getHistoricalPrice().multiply(BigDecimal.valueOf(ligne.getQuantity()));
                table.addCell(createCell(sousTotal.toString() + " FCFA", fontNormal, Element.ALIGN_RIGHT));
            }
            document.add(table);

            // 6. BLOC DES TOTAUX (Aligné à droite)
            PdfPTable tableTotaux = new PdfPTable(2);
            tableTotaux.setWidthPercentage(40);
            tableTotaux.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotaux.setSpacingBefore(10f);
            tableTotaux.setWidths(new float[]{1f, 1f});

            addTotalRow(tableTotaux, "Total HT :", totalHT.toString() + " FCFA", fontNormal);
            addTotalRow(tableTotaux, "TVA (19,25%) :", totalTVA.toString() + " FCFA", fontNormal);

            // Total Net à Payer mis en gras
            PdfPCell cellNetLibelle = new PdfPCell(new Phrase("NET À PAYER :", fontHeader));
            cellNetLibelle.setBorder(Rectangle.NO_BORDER);
            cellNetLibelle.setHorizontalAlignment(Element.ALIGN_RIGHT);

            PdfPCell cellNetValeur = new PdfPCell(new Phrase(totalTTC.toString() + " FCFA", fontHeader));
            cellNetValeur.setBorder(Rectangle.NO_BORDER);
            cellNetValeur.setHorizontalAlignment(Element.ALIGN_RIGHT);

            tableTotaux.addCell(cellNetLibelle);
            tableTotaux.addCell(cellNetValeur);

            document.add(tableTotaux);

            // Fermeture
            document.close();

            // 7. UPLOAD CLOUDINARY
            byte[] pdfBytes = baos.toByteArray();
            String urlCloudinary = uploadImage.uploadPdfSafely(pdfBytes, fileName);

            // 8. ENREGISTREMENT DANS LA BASE DE DONNÉES
            Facture facture = new Facture();
            facture.setIdFacture(GenerateID.GenerateFactureID());
            facture.setDateFacturation(LocalDateTime.now());

            // On sauvegarde maintenant les vraies valeurs fiscales calculées
            facture.setTotalHT(totalHT);
            facture.setTotalTVA(totalTVA);
            facture.setTotalTTC(totalTTC);

            facture.setModePaiement(modePaiement);
            facture.setUrlFacture(urlCloudinary);
            facture.setCommande(commande);

            factureInterface.save(facture);

            return urlCloudinary;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de la facture PDF: " + e.getMessage());
        }
    }

    // --- Méthodes utilitaires pour styliser les cellules du PDF ---

    private PdfPCell createCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }

    private void addTotalRow(PdfPTable table, String libelle, String valeur, Font font) {
        PdfPCell cellLibelle = new PdfPCell(new Phrase(libelle, font));
        cellLibelle.setBorder(Rectangle.NO_BORDER);
        cellLibelle.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellLibelle.setPaddingBottom(5);

        PdfPCell cellValeur = new PdfPCell(new Phrase(valeur, font));
        cellValeur.setBorder(Rectangle.NO_BORDER);
        cellValeur.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellValeur.setPaddingBottom(5);

        table.addCell(cellLibelle);
        table.addCell(cellValeur);
    }
}