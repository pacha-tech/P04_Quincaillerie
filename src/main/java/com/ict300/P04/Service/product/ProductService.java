package com.ict300.P04.Service.product;

import com.ict300.P04.DTO.price.response.PriceSearchProductDTO;
import com.ict300.P04.DTO.product.request.AddProductDTO;
import com.ict300.P04.DTO.product.request.UpdateProductDTO;
import com.ict300.P04.DTO.product.response.ProductStockDTO;
import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.DTO.product.response.getProductSuggestionDTO;
import com.ict300.P04.Entite.*;
import com.ict300.P04.Exception.ProductExistException;
import com.ict300.P04.Exception.ProductNotFoundException;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Service.cloudinary.CloudinaryService;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.category.CategoryInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import com.ict300.P04.repository.interfaces.product.ProductInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import com.ict300.P04.repository.interfaces.user.seller.SellerInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class ProductService {
    @Autowired
    private ProductInterface productInterface;

    @Autowired
    private CategoryInterface categoryInterface;

    @Autowired
    private QuincaillerieInterface quincaillerieInterface;

    @Autowired
    private SellerInterface sellerInterface;

    @Autowired
    private PriceInterface priceInterface;

    @Autowired
    private CloudinaryService cloudinaryService;

    public List<SearchProductDTO> SearchProductByName(String name) {
        return productInterface.findByNameContainingIgnoreCase(name).stream().map(product -> new SearchProductDTO(
                product.getIdProduct(),
                product.getCategory().getIdCategory(),
                product.getName(),
                product.getUnit(),
                product.getPrices().stream().map(price -> new PriceSearchProductDTO(
                        price.getQuincaillerie().getStoreName(),
                        price.getQuincaillerie().getIdQuincaillerie(),
                        price.getPrice(),
                        price.getStock(),
                        price.getIdPrice(),
                        price.getQuincaillerie().getLatitude(),
                        price.getQuincaillerie().getLongitude()
                )).toList(),
                product.getDescription()
        )).toList();
    }

    public List<getProductSuggestionDTO> getAllSuggestions() {

        return productInterface.findAll().stream().map(product -> new getProductSuggestionDTO(
                product.getIdProduct(),
                product.getName(),
                product.getCategory().getIdCategory(),
                product.getCategory().getName(),
                product.getDescription(),
                product.getCategory().getDescription(),
                product.getBrand(),
                product.getUnit()
        )).toList();
    }


    @Transactional
    public void AddProduct(AddProductDTO addProductDTO, MultipartFile image, String uid, String quincaillerieId) {


        boolean ifExist = priceInterface.ifAlreadyExistProductByQuincaillerie(addProductDTO.getName(), quincaillerieId);
        if (ifExist) {
            throw new ProductExistException("Le produit existe déjà pour cette quincaillerie");
        }



        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = cloudinaryService.uploadImageProduct(image);
            } catch (IOException e) {
                log.error("Échec de l'upload de l'image, création du produit sans image", e);
            }
        }


        Category category = categoryInterface.getCategoty(addProductDTO.getCategoryId());
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(quincaillerieId);
        User user = sellerInterface.getByIdUser(uid);


        Product newProduct = new Product();
        newProduct.setIdProduct(GenerateID.GenerateProductID());
        newProduct.setName(addProductDTO.getName());
        newProduct.setImageUrl(imageUrl);
        newProduct.setCategory(category);
        newProduct.setBrand(addProductDTO.getBrand());
        newProduct.setDescription(addProductDTO.getDescription());
        newProduct.setUnit(addProductDTO.getUnite());

        productInterface.save(newProduct);


        Price newPrice = new Price();
        newPrice.setIdPrice(GenerateID.GeneratePriceID());
        newPrice.setProduct(newProduct);
        newPrice.setQuincaillerie(quincaillerie);
        newPrice.setUser(user);
        newPrice.setUpdateDate(LocalDateTime.now());
        newPrice.setPrice(addProductDTO.getSellingPrice());
        newPrice.setPurchasePrice(addProductDTO.getPurchasePrice());
        newPrice.setStock(addProductDTO.getStock());

        priceInterface.save(newPrice);

        log.info("Produit '{}' ajouté avec succès (ID: {})", newProduct.getName(), newProduct.getIdProduct());
    }


    public List<ProductStockDTO> getStock(String idQuincaillerie) {
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(idQuincaillerie);

        if(quincaillerie == null ) {
            throw new ResourceNotFoundException("La quincaillerie n'existe pas");
        }

        List<ProductStockDTO> productStockDTOList = new ArrayList<>();


        List<Object[]> prices = productInterface.getProductByQuincailleries(quincaillerie);

        for (Object[] price : prices) {
            ProductStockDTO productStockDTO = new ProductStockDTO();

            Price priceEntity = (Price) price[0];

            // Remplissage des infos de base
            productStockDTO.setId(priceEntity.getProduct().getIdProduct());
            productStockDTO.setName(priceEntity.getProduct().getName());
            productStockDTO.setBrand(priceEntity.getProduct().getBrand());
            productStockDTO.setCategory(priceEntity.getProduct().getCategory().getName());
            productStockDTO.setStock(priceEntity.getStock());
            productStockDTO.setUnit(priceEntity.getProduct().getUnit());
            productStockDTO.setSellPrice(priceEntity.getPrice().toString());
            productStockDTO.setImageUrl(priceEntity.getProduct().getImageUrl());
            productStockDTO.setDescription(priceEntity.getProduct().getDescription());
            productStockDTO.setPurchasePrice(priceEntity.getPurchasePrice().toString());

            Object tauxObj = price[1];
            Double taux = 0.0;

            if (tauxObj != null) {
                taux = ((Number) tauxObj).doubleValue();
            }

            if (taux > 0) {
                double prixInitial = priceEntity.getPrice().doubleValue();
                double prixPromo = prixInitial * (1 - (taux / 100));

                productStockDTO.setPricePromo(String.valueOf(prixPromo));
                productStockDTO.setTaux(String.valueOf(taux));
                productStockDTO.setInPromotion(true);
            } else {
                productStockDTO.setPricePromo(null);
                productStockDTO.setTaux(null);
                productStockDTO.setInPromotion(false);
            }

            productStockDTOList.add(productStockDTO);
        }

        return productStockDTOList;
    }

    @Transactional
    public void updateProduct(String idProduct , UpdateProductDTO updateProductDTO , String idQuincaillerie) {

        Product product = productInterface.getProduct(idProduct);

        Price price = priceInterface.findByProductAndQuincaillerie( idProduct , idQuincaillerie);

        if(price == null){
            throw new ProductNotFoundException("Le produit "+updateProductDTO.getName()+" n'existe pas");
        }

        if(updateProductDTO.getName() != null ){
            product.setName(updateProductDTO.getName());
        }
        if (updateProductDTO.getBrand() != null){
            product.setBrand(updateProductDTO.getBrand());
        }
        if(updateProductDTO.getImageUrl() != null){
            product.setImageUrl(updateProductDTO.getImageUrl());
        }
        if(updateProductDTO.getDescriptionProduit() != null ){
            product.setDescription(updateProductDTO.getDescriptionProduit());
        }
        if(updateProductDTO.getPurchasePrice() != null){
            price.setPurchasePrice(updateProductDTO.getPurchasePrice());
        }
        if(updateProductDTO.getSellingPrice() != null){
            price.setPrice(updateProductDTO.getSellingPrice());
        }
        if(updateProductDTO.getQuantite() != null){
            price.setStock(updateProductDTO.getQuantite());
        }
        if(updateProductDTO.getUnite() != null){
            product.setUnit(updateProductDTO.getUnite());
        }

        productInterface.save(product);
        priceInterface.save(price);
    }

    @Transactional
    public void deleteProduct(String idProduct , String quincaillerieId){
        Price price = priceInterface.getPriceByProductAndQuincaillerie(idProduct , quincaillerieId);


        if(price == null){
            throw new ProductNotFoundException("Ce Produit n'existe pas");
        }

        System.out.println("PRICE: "+price.getIdPrice());


        priceInterface.deleteById(price.getIdPrice());


        if(productInterface.existsById(idProduct)){
            productInterface.deleteById(idProduct);
        }else {
            throw new ProductNotFoundException("Ce Produit n'existe pas");
        }
    }
}
