package com.ict300.P04.Service.user.customer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.ict300.P04.DTO.user.customer.request.RegisterCustomerDTO;
import com.ict300.P04.DTO.user.customer.response.AuthResponseDTO;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Service.cloudinary.UploadImage;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthCustomerService {
    @Autowired
    private CustomerInterface userInterface;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UploadImage uploadImage;

    @Transactional
    public  User register(RegisterCustomerDTO registerCustomerDTO) throws Exception {
        Object[] result = register(registerCustomerDTO , null);
        return (User) result[0];
    }

    @Transactional
    public Object[] register(RegisterCustomerDTO registerUserDTO , MultipartFile photo) throws Exception {

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(registerUserDTO.getEmail())
                .setPassword(registerUserDTO.getPassword());

        UserRecord userRecord;

        String photoUrl = "";

        if(photo != null && !photo.isEmpty()){
            photoUrl = uploadImage.uploadPhotoSafely(photo);
        }

        // 1. Création dans Firebase
        try {
            userRecord = FirebaseAuth.getInstance().createUser(request);
        } catch (FirebaseAuthException e) {
            throw new Exception("Erreur Firebase : " + e.getMessage());
        }

        User newUser = new User();

        newUser.setIdUser(userRecord.getUid());
        newUser.setName(registerUserDTO.getName());
        newUser.setPhone(registerUserDTO.getPhone());
        newUser.setEmail(registerUserDTO.getEmail());
        newUser.setRegistrationDate(LocalDateTime.now());
        newUser.setLastLogin(LocalDateTime.now());
        newUser.setPhotoUrl(photoUrl);
        String roleSet = registerUserDTO.getRole();
        newUser.setRole(roleSet);
        newUser.setStatus("ACTIF");

        try {
            if(roleSet.equals("CLIENT")){
                Map<String, Object> claims = new HashMap<>();
                claims.put("role", roleSet);

                FirebaseAuth.getInstance().setCustomUserClaims(userRecord.getUid(), claims);
                System.out.println("✅ Rôle " + registerUserDTO.getRole() + " ajouté au Token Firebase");

            }
            userInterface.save(newUser);

            // 2. GÉNÉRATION DU CUSTOM TOKEN POUR L'AUTO-LOGIN FRONTEND
            String customToken = FirebaseAuth.getInstance().createCustomToken(userRecord.getUid());

            // 3. On retourne l'utilisateur ET le token
            AuthResponseDTO authResponseDTO = new AuthResponseDTO(customToken , "Inscription Reussis");

            return new Object[]{newUser, authResponseDTO};

        } catch (Exception e) {
            FirebaseAuth.getInstance().deleteUser(userRecord.getUid());
            throw new Exception("Erreur lors de l'enregistrement local. Compte Firebase annulé : " + e.getMessage());
        }
    }
}
