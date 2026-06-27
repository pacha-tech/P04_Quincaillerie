package com.ict300.P04.modele.category;

import com.ict300.P04.DTO.category.request.CategoryDTO;
import com.ict300.P04.DTO.category.response.AddCategoryDTO;
import com.ict300.P04.DTO.category.response.ProductInCategoryDTO;
import com.ict300.P04.DTO.localisation.LocalisationDTO;
import com.ict300.P04.Entite.Category;
import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Product;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Service.category.CategoryService;
import com.ict300.P04.Service.localisation.LocalisationService;
import com.ict300.P04.repository.interfaces.category.CategoryInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryInterface categoryInterface;

    @Mock
    private PriceInterface priceInterface;

    @Mock
    private LocalisationService localisationService;

    @InjectMocks
    private CategoryService categoryService;

    private Category defaultCategory;
    private Price defaultPrice;
    private Quincaillerie defaultQuincaillerie;
    private final Double userLat = 3.8117871;
    private final Double userLon = 11.52122880;

    @BeforeEach
    void setUp() {
        defaultCategory = new Category();
        defaultCategory.setIdCategory("CAT-123");

        Product product = new Product();
        product.setIdProduct("Pro-001");
        product.setName("Marteau");
        product.setBrand("Total");
        product.setCategory(defaultCategory);
        product.setUnit("Pieces");

        defaultQuincaillerie = new Quincaillerie();
        defaultQuincaillerie.setStoreName("Quincaillerie Centrale");
        defaultQuincaillerie.setIdQuincaillerie("QUIN-99");
        defaultQuincaillerie.setCity("Yaoundé");
        defaultQuincaillerie.setRegion("Centre");
        defaultQuincaillerie.setLatitude(BigDecimal.valueOf(3.86007040));
        defaultQuincaillerie.setLongitude(BigDecimal.valueOf(11.52122880));

        defaultPrice = new Price();
        defaultPrice.setIdPrice("PRICE-001");
        defaultPrice.setProduct(product);
        defaultPrice.setQuincaillerie(defaultQuincaillerie);
        defaultPrice.setPrice(BigDecimal.valueOf(4500));
        defaultPrice.setStock(15);
    }

    private void mockDatabaseResult(Double promotionTaux) {
        Object[] ligneSqlSimulee = new Object[] { defaultPrice, promotionTaux };
        List<Object[]> listeSqlSimulee = new ArrayList<>();
        listeSqlSimulee.add(ligneSqlSimulee);
        when(priceInterface.findPricesByCategory(defaultCategory.getIdCategory())).thenReturn(listeSqlSimulee);
    }


    @Test
    void getAllCategory_ShouldReturnListCategory() {
        Category c1 = new Category();
        c1.setIdCategory("CAT-001");
        c1.setName("Electricite");
        c1.setDescription("Materiel d'electricite");

        when(categoryInterface.findAll()).thenReturn(List.of(c1));

        List<CategoryDTO> result = categoryService.getAllCategory();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electricite", result.get(0).getName());
    }

    @Test
    void addCategory_ShouldReturn() {
        AddCategoryDTO add = new AddCategoryDTO();
        add.setName("Ciment");
        add.setDescription("Differents type de ciment");

        categoryService.AddCategory(add);
        verify(categoryInterface, times(1)).save(any(Category.class));
    }

    // ==========================================
    // TESTS : SCOPE AUCUN / SANS GÉO
    // ==========================================

    @Test
    void getAllProductByCategory_ShouldReturnListProducts_WithOutFiltrageGeographique() {
        mockDatabaseResult(null);

        List<ProductInCategoryDTO> result = categoryService.getAllProductByCategory(defaultCategory.getIdCategory(), null, null, "aucun");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isInPromotion());
    }

    // ==========================================
    // TESTS : SCOPE VILLE (4 branches du IF)
    // ==========================================

    @Test
    void getAllProductByCategory_ScopeVille_SuccessMatch() {
        mockDatabaseResult(10.0);
        LocalisationDTO loc = new LocalisationDTO();
        loc.setVille("Yaoundé");
        when(localisationService.getAddress(userLat, userLon)).thenReturn(loc);

        List<ProductInCategoryDTO> result = categoryService.getAllProductByCategory(defaultCategory.getIdCategory(), userLon, userLat, "ville");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isInPromotion());
    }

    @Test
    void getAllProductByCategory_ScopeVille_FailBecauseCityNull() {
        mockDatabaseResult(10.0);
        LocalisationDTO loc = new LocalisationDTO();
        loc.setVille(null);
        when(localisationService.getAddress(userLat, userLon)).thenReturn(loc);

        List<ProductInCategoryDTO> result = categoryService.getAllProductByCategory(defaultCategory.getIdCategory(), userLon, userLat, "ville");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void getAllProductByCategory_ScopeVille_FailBecauseCityNonDefini() {
        mockDatabaseResult(10.0);
        LocalisationDTO loc = new LocalisationDTO();
        loc.setVille("Non défini");
        when(localisationService.getAddress(userLat, userLon)).thenReturn(loc);

        List<ProductInCategoryDTO> result = categoryService.getAllProductByCategory(defaultCategory.getIdCategory(), userLon, userLat, "ville");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void getAllProductByCategory_ScopeVille_FailBecauseCityMismatch() {
        mockDatabaseResult(10.0);
        LocalisationDTO loc = new LocalisationDTO();
        loc.setVille("Douala");
        when(localisationService.getAddress(userLat, userLon)).thenReturn(loc);

        List<ProductInCategoryDTO> result = categoryService.getAllProductByCategory(defaultCategory.getIdCategory(), userLon, userLat, "ville");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ==========================================
    // TESTS : SCOPE RÉGION (4 branches du IF)
    // ==========================================

    @Test
    void getAllProductByCategory_ScopeRegion_SuccessMatch() {
        mockDatabaseResult(0.0);
        LocalisationDTO loc = new LocalisationDTO();
        loc.setRegion("Centre");
        when(localisationService.getAddress(userLat, userLon)).thenReturn(loc);

        List<ProductInCategoryDTO> result = categoryService.getAllProductByCategory(defaultCategory.getIdCategory(), userLon, userLat, "region");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllProductByCategory_ScopeRegion_FailBecauseRegionNull() {
        mockDatabaseResult(10.0);
        LocalisationDTO loc = new LocalisationDTO();
        loc.setRegion(null);
        when(localisationService.getAddress(userLat, userLon)).thenReturn(loc);

        List<ProductInCategoryDTO> result = categoryService.getAllProductByCategory(defaultCategory.getIdCategory(), userLon, userLat, "region");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void getAllProductByCategory_ScopeRegion_FailBecauseRegionNonDefini() {
        mockDatabaseResult(10.0);
        LocalisationDTO loc = new LocalisationDTO();
        loc.setRegion("Non défini");
        when(localisationService.getAddress(userLat, userLon)).thenReturn(loc);

        List<ProductInCategoryDTO> result = categoryService.getAllProductByCategory(defaultCategory.getIdCategory(), userLon, userLat, "region");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void getAllProductByCategory_ScopeRegion_FailBecauseRegionMismatch() {
        mockDatabaseResult(10.0);
        LocalisationDTO loc = new LocalisationDTO();
        loc.setRegion("Littoral");
        when(localisationService.getAddress(userLat, userLon)).thenReturn(loc);

        List<ProductInCategoryDTO> result = categoryService.getAllProductByCategory(defaultCategory.getIdCategory(), userLon, userLat, "region");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ==========================================
    // TESTS : SCOPE DISTANCE (KILOMÈTRES)
    // ==========================================

    @Test
    void getAllProductByCategory_ScopeDistance_SuccessInsideRange() {
        mockDatabaseResult(0.0);
        // Le magasin par défaut (3.86, 11.52) est à environ 5-6 km du client (3.81, 11.52)
        // On demande un grand scope (ex: "50km") pour être sûr qu'il soit accepté
        List<ProductInCategoryDTO> result = categoryService.getAllProductByCategory(defaultCategory.getIdCategory(), userLon, userLat, "10km");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllProductByCategory_ScopeDistance_FailOutsideRange() {
        mockDatabaseResult(0.0);
        // On demande un tout petit rayon de 1km, le magasin (étant à ~5km) doit être rejeté
        List<ProductInCategoryDTO> result = categoryService.getAllProductByCategory(defaultCategory.getIdCategory(), userLon, userLat, "1km");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void getAllProductByCategory_ScopeDistance_StoreCoordinatesNull() {
        mockDatabaseResult(0.0);
        // Si le magasin n'a pas de coordonnées, le code saute le filtrage de distance et l'accepte
        defaultQuincaillerie.setLatitude(null);
        defaultQuincaillerie.setLongitude(null);

        List<ProductInCategoryDTO> result = categoryService.getAllProductByCategory(defaultCategory.getIdCategory(), userLon, userLat, "5km");

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}