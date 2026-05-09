package com.ict300.P04.Service.user.seller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.ict300.P04.DTO.user.customer.response.AuthResponseDTO;
import com.ict300.P04.DTO.user.seller.request.RegisterSellerDTO;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Service.quincaillerie.QuincaillerieService;
import com.ict300.P04.Service.user.customer.AuthCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthSellerService {

    @Autowired
    private AuthCustomerService authCustomerService;

    @Autowired
    private QuincaillerieService quincaillerieService;

    @Transactional
    public Object[] registerSeller(RegisterSellerDTO registerSellerDTO) throws Exception {
        User user = null;
        Quincaillerie quincaillerie = null;

        try {
            // 1. Création de l'utilisateur (le AuthCustomerService s'occupe de Firebase et de l'entité User)
            user = authCustomerService.register(registerSellerDTO.getUser());

            // 2. Création de la quincaillerie liée au vendeur
            quincaillerie = quincaillerieService.registerQuincaillerie(registerSellerDTO.getQuincaillerie(), user.getIdUser());

            // 3. Ajout des rôles et de l'ID quincaillerie aux claims Firebase
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", registerSellerDTO.getUser().getRole());
            claims.put("quincaillerieId", quincaillerie.getIdQuincaillerie());

            FirebaseAuth.getInstance().setCustomUserClaims(user.getIdUser(), claims);
            System.out.println("✅ Rôle " + registerSellerDTO.getUser().getRole() + " et QuincaillerieId " + quincaillerie.getIdQuincaillerie() + " ajoutés au Token Firebase " + user.getIdUser());

            // 4. GÉNÉRATION DU CUSTOM TOKEN POUR L'AUTO-LOGIN FRONTEND DU VENDEUR
            String customToken = FirebaseAuth.getInstance().createCustomToken(user.getIdUser());
            AuthResponseDTO authResponseDTO = new AuthResponseDTO(customToken, "Inscription Vendeur Reussie");

            // 5. On retourne l'utilisateur, la quincaillerie et le DTO contenant le token
            return new Object[]{user, quincaillerie, authResponseDTO};

        } catch (Exception e) {
            if (user != null) {
                try {
                    FirebaseAuth.getInstance().deleteUser(user.getIdUser());
                    System.err.println("🧹 Nettoyage : Utilisateur Firebase " + user.getIdUser() + " supprimé suite à une erreur.");
                } catch (FirebaseAuthException fe) {
                    System.err.println("🚨 ERREUR CRITIQUE : Impossible de supprimer le vendeur sur Firebase : " + fe.getMessage());
                }
            }
            throw e;
        }
    }
}