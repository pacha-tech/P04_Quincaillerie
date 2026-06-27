package com.ict300.P04.modele.product;

import com.ict300.P04.DTO.localisation.LocalisationDTO;
import com.ict300.P04.DTO.product.response.ProductStockDTO;
import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.DTO.product.response.getProductSuggestionDTO;
import com.ict300.P04.Entite.Category;
import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Product;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Service.localisation.LocalisationService;
import com.ict300.P04.Service.product.ProductService;
import com.ict300.P04.repository.interfaces.category.CategoryInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import com.ict300.P04.repository.interfaces.product.ProductInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import com.ict300.P04.repository.interfaces.user.seller.SellerInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductInterface productInterface;

    @Mock
    private CategoryInterface categoryInterface;

    @Mock
    private QuincaillerieInterface quincaillerieInterface;

    @Mock
    private SellerInterface sellerInterface;

    @Mock
    private PriceInterface priceInterface;

    @Mock
    private LocalisationService localisationService;

    @InjectMocks
    private ProductService productService;

    // 1. Déclaration des variables globales
    private Category defaultCategory;
    private Product defaultProduct;
    private Quincaillerie defaultQuincaillerie;
    private Price defaultPrice;
    private final Double userLat = 3.8117871;
    private final Double userLon = 11.52122880;

    // 2. Initialisation avant chaque test
    @BeforeEach
    void setUp() {
        defaultCategory = new Category();
        defaultCategory.setIdCategory("Cat001");
        defaultCategory.setName("Construction");

        defaultProduct = new Product();
        defaultProduct.setIdProduct("Prod001");
        defaultProduct.setName("Ciment"); // Utilisé dans tous les tests
        defaultProduct.setBrand("Cimencam");
        defaultProduct.setCategory(defaultCategory);

        defaultQuincaillerie = new Quincaillerie();
        defaultQuincaillerie.setIdQuincaillerie("Quin001");
        defaultQuincaillerie.setCity("Yaounde");
        defaultQuincaillerie.setLatitude(BigDecimal.valueOf(3.1158795));
        defaultQuincaillerie.setLongitude(BigDecimal.valueOf(11.8452135));

        defaultPrice = new Price();
        defaultPrice.setIdPrice("Price001");
        defaultPrice.setQuincaillerie(defaultQuincaillerie);
        defaultPrice.setProduct(defaultProduct);
        defaultPrice.setPurchasePrice(BigDecimal.valueOf(200));
        defaultPrice.setPrice(BigDecimal.valueOf(150));
    }

    // 3. Tes tests, désormais beaucoup plus courts !

    @Test
    void getAllSuggestion() {
        when(productInterface.findOnlyName()).thenReturn(List.of(defaultProduct));

        List<getProductSuggestionDTO> result = productService.getAllSuggestions();

        assertEquals(1, result.size());
        assertEquals("Ciment", result.get(0).getNom());
        assertEquals("Cimencam", result.get(0).getBrand());
    }

    @Test
    void getStock_ShouldReturnQuincaillerieNoExist() {
        when(quincaillerieInterface.getQuincaillerie(defaultQuincaillerie.getIdQuincaillerie())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getStock(defaultQuincaillerie.getIdQuincaillerie()));

        assertEquals("La quincaillerie n'existe pas", exception.getMessage());
        // J'ai corrigé "getProductByQuincaillerie" en "getProductByQuincailleries" au cas où
        verify(productInterface, never()).getProductByQuincailleries(any());
    }

    @Test
    void getStock_ShouldReturnListWithOutPromotion() {
        Object[] resultLigne = new Object[]{defaultPrice, null};
        List<Object[]> list = new ArrayList<>();
        list.add(resultLigne);

        when(quincaillerieInterface.getQuincaillerie(defaultQuincaillerie.getIdQuincaillerie())).thenReturn(Optional.of(defaultQuincaillerie));
        when(productInterface.getProductByQuincailleries(defaultQuincaillerie)).thenReturn(list);

        List<ProductStockDTO> productStockDTOS = productService.getStock(defaultQuincaillerie.getIdQuincaillerie());

        assertNotNull(productStockDTOS);
        assertEquals("Ciment", productStockDTOS.get(0).getName());
        assertFalse(productStockDTOS.get(0).isInPromotion());
        assertNull(productStockDTOS.get(0).getPricePromo());
    }

    @Test
    void getStock_ShouldReturnListWithPromotion() {
        Object[] resultLigne = new Object[]{defaultPrice, 10.0};
        List<Object[]> list = new ArrayList<>();
        list.add(resultLigne);

        when(quincaillerieInterface.getQuincaillerie(defaultQuincaillerie.getIdQuincaillerie())).thenReturn(Optional.of(defaultQuincaillerie));
        when(productInterface.getProductByQuincailleries(defaultQuincaillerie)).thenReturn(list);

        List<ProductStockDTO> productStockDTOS = productService.getStock(defaultQuincaillerie.getIdQuincaillerie());

        assertNotNull(productStockDTOS);
        assertEquals("Ciment", productStockDTOS.get(0).getName());
        assertTrue(productStockDTOS.get(0).isInPromotion());
        assertNotNull(productStockDTOS.get(0).getPricePromo());
    }

    @Test
    void SearchProductWithOutCoordonneeAndScope() {
        Object[] resultLigne = new Object[]{defaultPrice, null};
        List<Object[]> list = new ArrayList<>();
        list.add(resultLigne);

        when(productInterface.findByNameContainingIgnoreCase("query")).thenReturn(list);

        List<SearchProductDTO> searchProductDTOList = productService.SearchProductByName("query", null, null, "aucun");

        assertNotNull(searchProductDTOList);
        assertEquals(1, searchProductDTOList.size());
        assertEquals("Ciment", searchProductDTOList.get(0).getName());
    }

    @Test
    void SearchProductWithCoordonneeAndScope() {
        Object[] resultLigne = new Object[]{defaultPrice, 10.0};
        List<Object[]> list = new ArrayList<>();
        list.add(resultLigne);

        LocalisationDTO loc = new LocalisationDTO();
        loc.setVille("Yaounde");

        when(productInterface.findByNameContainingIgnoreCase("query")).thenReturn(list);
        when(localisationService.getAddress(userLat , userLon)).thenReturn(loc);

        List<SearchProductDTO> searchProductDTOList = productService.SearchProductByName("query", userLon, userLat, "ville");

        assertNotNull(searchProductDTOList);
        assertEquals(1, searchProductDTOList.size());
        assertEquals("Ciment", searchProductDTOList.get(0).getName());
        assertTrue(searchProductDTOList.get(0).getPriceSearchProductsDTO().get(0).isInPromotion());
    }
}