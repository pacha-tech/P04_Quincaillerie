package com.ict300.P04.Service.category;

import com.ict300.P04.DTO.category.request.HistoriqueNavigationRequestDTO;
import com.ict300.P04.Entite.Category;
import com.ict300.P04.Entite.HistoriqueNavigation;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.category.CategoryInterface;
import com.ict300.P04.repository.interfaces.historiqueNavigation.HistoriqueNavigationInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HistoriqueNavigationService {
    @Autowired
    private HistoriqueNavigationInterface historiqueNavigationInterface;

    @Autowired
    private CustomerInterface customerInterface;

    @Autowired
    private CategoryInterface categoryInterface;

    public void saveHistoriqueNavigation(HistoriqueNavigationRequestDTO historiqueNavigationRequestDTO , String idUser) {
        Category category = categoryInterface.findById(historiqueNavigationRequestDTO.getIdCategory())
                .orElseThrow(() -> new ResourceNotFoundException("La categorie n'existe pas"));

        User user = customerInterface.getByIdUser(idUser)
                .orElseThrow(() -> new UserNotFoundException("L'utilisateur n'existe pas"));

        HistoriqueNavigation historiqueNavigation = new HistoriqueNavigation();

        historiqueNavigation.setIdHistoriqueNavigation(GenerateID.GenerateHistoriquenavigationID());
        historiqueNavigation.setCategory(category);
        historiqueNavigation.setUser(user);
        historiqueNavigation.setActionType(historiqueNavigationRequestDTO.getActionType());
        historiqueNavigation.setDateAction(LocalDateTime.now());

        historiqueNavigationInterface.save(historiqueNavigation);
    }
}
