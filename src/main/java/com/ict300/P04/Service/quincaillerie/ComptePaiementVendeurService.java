package com.ict300.P04.Service.quincaillerie;

import com.ict300.P04.DTO.quincaillerie.response.ComptePaiementDTO;
import com.ict300.P04.Entite.ComptePaiementVendeur;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Service.paiement.aangaraPayService.AangaraPayService;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.ComptePaiementVendeur.ComptePaiementVendeurInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ComptePaiementVendeurService {

    @Autowired
    private ComptePaiementVendeurInterface comptePaiementRepository;

    @Autowired
    private QuincaillerieInterface quincaillerieRepository;

    @Autowired
    private AangaraPayService aangaraPayService;


    // On garde Transactional mais pour la lecture de la quincaillerie
    @Transactional
    public ComptePaiementDTO getConfigurationPaiement(String idQuincaillerie) {
        Quincaillerie quincaillerie = quincaillerieRepository.findById(idQuincaillerie)
                .orElseThrow(() -> new ResourceNotFoundException("Quincaillerie introuvable avec l'ID : " + idQuincaillerie));

        ComptePaiementVendeur compte = quincaillerie.getComptePaiementVendeur();

        if (compte == null) {
            log.info("Première consultation : Génération des infos par défaut (en mémoire) pour la quincaillerie #{}", idQuincaillerie);
            compte = initialiserCompteParDefaut(quincaillerie);
        }

        return mapperEnDTO(compte);
    }


    @Transactional
    public ComptePaiementDTO modifierConfigurationPaiement(String idQuincaillerie, ComptePaiementDTO dto) {
        Quincaillerie quincaillerie = quincaillerieRepository.findById(idQuincaillerie)
                .orElseThrow(() -> new ResourceNotFoundException("Quincaillerie introuvable avec l'ID : " + idQuincaillerie));

        ComptePaiementVendeur compte = quincaillerie.getComptePaiementVendeur();

        if (compte == null) {
            compte = new ComptePaiementVendeur();
            // AJOUT : on s'assure de générer l'ID lors de la vraie création
            compte.setIdComptePaiementVendeur(GenerateID.GenerateInfoPaiementID());
            compte.setQuincaillerie(quincaillerie);
            compte.setDateCreation(LocalDateTime.now());
        }

        compte.setNomCompte(dto.getNomCompte().trim());
        compte.setNumeroTelephone(dto.getNumeroTelephone().trim());
        compte.setOperateur(dto.getOperateur());
        compte.setDateDerniereModification(LocalDateTime.now());

        ComptePaiementVendeur compteSauvegarde = comptePaiementRepository.save(compte);
        log.info("Configuration de paiement mise à jour pour la quincaillerie #{}. Opérateur : {}", idQuincaillerie, dto.getOperateur());

        return mapperEnDTO(compteSauvegarde);
    }


    private ComptePaiementVendeur initialiserCompteParDefaut(Quincaillerie quincaillerie) {
        ComptePaiementVendeur defaultCompte = new ComptePaiementVendeur();
        defaultCompte.setIdComptePaiementVendeur(GenerateID.GenerateInfoPaiementID());
        defaultCompte.setQuincaillerie(quincaillerie);
        defaultCompte.setNomCompte(quincaillerie.getStoreName());
        String telephoneInscription = quincaillerie.getAdmin().getPhone();
        defaultCompte.setNumeroTelephone(telephoneInscription);

        try {
            String operateur = aangaraPayService.detecterProviderMobileMoney(telephoneInscription);
            defaultCompte.setOperateur(operateur);
        } catch (Exception e) {
            defaultCompte.setOperateur("A DEFINIR");
        }

        defaultCompte.setDateCreation(LocalDateTime.now());
        defaultCompte.setDateDerniereModification(LocalDateTime.now());

        return defaultCompte;
    }


    private ComptePaiementDTO mapperEnDTO(ComptePaiementVendeur compte) {
        ComptePaiementDTO dto = new ComptePaiementDTO();
        dto.setNomCompte(compte.getNomCompte());
        dto.setNumeroTelephone(compte.getNumeroTelephone());
        dto.setOperateur(compte.getOperateur());
        return dto;
    }
}