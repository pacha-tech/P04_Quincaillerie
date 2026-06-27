package com.ict300.P04.modele.category;

import com.ict300.P04.DTO.category.request.HistoriqueNavigationRequestDTO;
import com.ict300.P04.Entite.Category;
import com.ict300.P04.Entite.HistoriqueNavigation;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.Service.category.HistoriqueNavigationService;
import com.ict300.P04.repository.interfaces.category.CategoryInterface;
import com.ict300.P04.repository.interfaces.historiqueNavigation.HistoriqueNavigationInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HistoriqueNavigationServiceTest {
    @Mock
    private HistoriqueNavigationInterface historiqueNavigationInterface;

    @Mock
    private CustomerInterface customerInterface;

    @Mock
    private CategoryInterface categoryInterface;

    @InjectMocks
    private HistoriqueNavigationService historiqueNavigationService;

    @Test
    void saveHistoriqueNavigtion_ShouldReturnNoCategoryExist(){
        HistoriqueNavigationRequestDTO historiqueNavigationRequestDTO = new HistoriqueNavigationRequestDTO();
        historiqueNavigationRequestDTO.setIdCategory("cat1");

        when(categoryInterface.findById(historiqueNavigationRequestDTO.getIdCategory())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class , () -> historiqueNavigationService.saveHistoriqueNavigation(historiqueNavigationRequestDTO , "moi"));
        assertEquals("La categorie n'existe pas" , ex.getMessage());
        verify(customerInterface , never()).getByIdUser(anyString());
        verify(historiqueNavigationInterface , never()).save(any());
    }

    @Test
    void saveHistoriqueNavigation_ShouldReturnUserNoExist() {
        Category category = new Category();
        category.setIdCategory("CAT001");
        category.setName("Ciment");

        HistoriqueNavigationRequestDTO historiqueNavigationRequestDTO = new HistoriqueNavigationRequestDTO();
        historiqueNavigationRequestDTO.setIdCategory("CAT001");

        when(categoryInterface.findById(historiqueNavigationRequestDTO.getIdCategory())).thenReturn(Optional.of(category));
        when(customerInterface.getByIdUser("INCONNU")).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows( UserNotFoundException.class , () -> historiqueNavigationService.saveHistoriqueNavigation(historiqueNavigationRequestDTO , "INCONNU"));
        assertEquals("L'utilisateur n'existe pas" , ex.getMessage());
        verify(categoryInterface).findById(anyString());
        verify(historiqueNavigationInterface , never()).save(any());
    }

    @Test
    void saveHistoriqueNavigation_ShouldReturnOk() {
        Category category = new Category();
        category.setIdCategory("CAT001");
        category.setName("Ciment");

        User user = new User();
        user.setIdUser("User001");

        HistoriqueNavigation historiqueNavigation = new HistoriqueNavigation();
        historiqueNavigation.setIdHistoriqueNavigation("idHistorique");
        historiqueNavigation.setCategory(category);
        historiqueNavigation.setUser(user);

        HistoriqueNavigationRequestDTO historiqueNavigationRequestDTO = new HistoriqueNavigationRequestDTO();
        historiqueNavigationRequestDTO.setIdCategory("CAT001");

        when(categoryInterface.findById(historiqueNavigationRequestDTO.getIdCategory())).thenReturn(Optional.of(category));
        when(customerInterface.getByIdUser("User001")).thenReturn(Optional.of(user));

        historiqueNavigationService.saveHistoriqueNavigation(historiqueNavigationRequestDTO , "User001");

        verify(historiqueNavigationInterface).save(any());
    }
}
