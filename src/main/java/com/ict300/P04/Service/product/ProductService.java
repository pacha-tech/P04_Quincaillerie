package com.ict300.P04.Service.product;

import com.ict300.P04.DTO.price.response.PriceSearchProductDTO;
import com.ict300.P04.DTO.product.request.AddProductDTO;
import com.ict300.P04.DTO.product.request.UpdateProductDTO;
import com.ict300.P04.DTO.product.response.ProductStockDTO;
import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.DTO.product.response.getProductDTO;
import com.ict300.P04.Entite.*;
import com.ict300.P04.Exception.ProductExistException;
import com.ict300.P04.Exception.ProductNotFoundException;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.category.CategoryInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import com.ict300.P04.repository.interfaces.product.ProductInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import com.ict300.P04.repository.interfaces.user.seller.SellerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<SearchProductDTO> SearchProductByName(String name) {
        return productInterface.findByNameContainingIgnoreCase(name).stream().map(product -> new SearchProductDTO(
                product.getIdProduct(),
                product.getCategory().getIdCategory(),
                product.getName(),
                product.getPrices().stream().map(price -> new PriceSearchProductDTO(
                        price.getQuincaillerie().getStoreName(),
                        price.getQuincaillerie().getIdQuincaillerie(),
                        price.getPrice(),
                        price.getStock(),
                        price.getPromotionRating(),
                        price.getQuincaillerie().getLatitude(),
                        price.getQuincaillerie().getLongitude()
                )).toList(),
                product.getDescription()
        )).toList();
    }

    public List<getProductDTO> getAllSuggestions() {

        return productInterface.findAll().stream().map(product -> new getProductDTO(
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

    public void AddProduct(AddProductDTO addProductDTO , String uid , String quincaillerieId) {

        boolean ifExist = priceInterface.ifAlreadyExistProductByQuincaillerie(addProductDTO.getName() , quincaillerieId);
        if(ifExist){
            throw new ProductExistException("Le produit existe deja pour cette quincaillerie");
        }
        Product newProduct = new Product();
        Price newPrice = new Price();


        Category category = categoryInterface.getCategoty(addProductDTO.getCategoryId());
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(quincaillerieId);
        User user = sellerInterface.getByIdUser(uid);


        newProduct.setIdProduct(GenerateID.GenerateProductID());
        newProduct.setName(addProductDTO.getName());
        newProduct.setImageUrl(addProductDTO.getImageUrl());
        newProduct.setCategory(category);
        newProduct.setBrand(addProductDTO.getBrand());
        newProduct.setDescription(addProductDTO.getDescription());
        newProduct.setUnit(addProductDTO.getUnite());

        productInterface.save(newProduct);

        newPrice.setIdPrice(GenerateID.GeneratePriceID());
        newPrice.setProduct(newProduct);
        newPrice.setQuincaillerie(quincaillerie);
        newPrice.setUser(user);
        newPrice.setUpdateDate(LocalDateTime.now());
        newPrice.setPrice(addProductDTO.getSellingPrice());
        newPrice.setPurchasePrice(addProductDTO.getPurchasePrice());
        newPrice.setStock(addProductDTO.getStock());

        priceInterface.save(newPrice);
    }

    public List<ProductStockDTO> getProductByQuincaillerie(String idQuincaillerie) {
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(idQuincaillerie);
        List<Price> prices = productInterface.getProductByQuincaillerie(quincaillerie);
        return prices.stream().map(price -> new ProductStockDTO(
                price.getProduct().getIdProduct(),
                price.getProduct().getName(),
                price.getProduct().getBrand(),
                price.getProduct().getCategory().getName(),
                price.getStock(),
                price.getProduct().getUnit(),
                price.getPrice().toString(),
                price.getProduct().getImageUrl(),
                price.getProduct().getDescription(),
                price.getPurchasePrice().toString()
        )).toList();
    }

    @Transactional
    public void updateProduct(String idProduct , UpdateProductDTO updateProductDTO) {

        Product product = productInterface.getProduct(idProduct);

        Price price = priceInterface.findByProduct(product);

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

        System.out.println("PRICE: "+price);

        if(price == null){
            throw new ProductNotFoundException("Ce Produit n'existe pas");
        }

        priceInterface.deleteById(price.getIdPrice());


        if(productInterface.existsById(idProduct)){
            productInterface.deleteById(idProduct);
        }else {
            throw new ProductNotFoundException("Ce Produit n'existe pas");
        }
    }
}
