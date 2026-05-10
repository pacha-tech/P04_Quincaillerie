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
import com.ict300.P04.Service.cloudinary.UploadImage;
import com.ict300.P04.Service.cloudinary.CloudinaryService;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.Utilitaires.MouvementStock;
import com.ict300.P04.repository.interfaces.category.CategoryInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import com.ict300.P04.repository.interfaces.product.ProductInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import com.ict300.P04.repository.interfaces.stock.StockInterface;
import com.ict300.P04.repository.interfaces.user.seller.SellerInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductInterface productInterface;
    private final CategoryInterface categoryInterface;
    private final QuincaillerieInterface quincaillerieInterface;
    private final SellerInterface sellerInterface;
    private final PriceInterface priceInterface;
    private final CloudinaryService cloudinaryService;
    private  final UploadImage uploadImage;
    private final StockInterface stockInterface;



    public List<SearchProductDTO> SearchProductByName(String query) {

        List<Object[]> results = productInterface.findByNameContainingIgnoreCase(query);


        Map<Product, List<PriceSearchProductDTO>> groupedByProduct = new HashMap<>();

        for (Object[] res : results) {
            Price price = (Price) res[0];
            Product product = price.getProduct();
            Double taux = (res[1] != null) ? ((Number) res[1]).doubleValue() : 0.0;

            boolean inPromo = taux > 0;
            Double pricePromo = inPromo ? price.getPrice().doubleValue() * (1 - (taux / 100)) : null;

            PriceSearchProductDTO priceDTO = new PriceSearchProductDTO(
                    price.getQuincaillerie().getStoreName(),
                    price.getQuincaillerie().getIdQuincaillerie(),
                    price.getPrice(),
                    price.getStock(),
                    price.getIdPrice(),
                    price.getQuincaillerie().getLatitude(),
                    price.getQuincaillerie().getLongitude(),
                    pricePromo == null ? 0.0 : pricePromo,
                    inPromo,
                    taux.toString()
            );

            groupedByProduct.computeIfAbsent(product, k -> new ArrayList<>()).add(priceDTO);
        }


        return groupedByProduct.entrySet().stream().map(entry -> {
            Product p = entry.getKey();
            return new SearchProductDTO(
                    p.getIdProduct(),
                    p.getCategory().getIdCategory(),
                    p.getName(),
                    p.getUnit(),
                    p.getImageUrl(),
                    entry.getValue(),
                    p.getDescription()
            );
        }).toList();
    }

    public SearchProductDTO getProductById(String idPrice) {

        priceInterface.findById(idPrice).orElseThrow(() -> new ProductNotFoundException("Le produit n'existe pas"));

        Object[] res = productInterface.findProductById(idPrice);

        Price price = (Price) res[0];
        Product product = price.getProduct();

        Double taux = (res[1] != null) ? ((Number) res[1]).doubleValue() : 0.0;
        boolean inPromo = taux > 0;

        Double pricePromo = inPromo ? price.getPrice().doubleValue() * (1 - (taux / 100)) : 0.0;

        // 4. Création du PriceDTO unique
        PriceSearchProductDTO priceDTO = new PriceSearchProductDTO(
                price.getQuincaillerie().getStoreName(),
                price.getQuincaillerie().getIdQuincaillerie(),
                price.getPrice(),
                price.getStock(),
                price.getIdPrice(),
                price.getQuincaillerie().getLatitude(),
                price.getQuincaillerie().getLongitude(),
                pricePromo,
                inPromo,
                taux.toString()
        );

        // 5. Encapsulation dans une liste (car SearchProductDTO attend List<PriceSearchProductDTO>)
        List<PriceSearchProductDTO> pricesList = new ArrayList<>();
        pricesList.add(priceDTO);

        // 6. Retour du DTO global
        return new SearchProductDTO(
                product.getIdProduct(),
                product.getCategory().getIdCategory(),
                product.getName(),
                product.getUnit(),
                product.getImageUrl(),
                pricesList,
                product.getDescription()
        );
    }

    public List<getProductSuggestionDTO> getAllSuggestions() {
        return productInterface.findAll().stream()
                .map(p -> new getProductSuggestionDTO(
                        p.getIdProduct(), p.getName(), p.getCategory().getIdCategory(),
                        p.getCategory().getName(), p.getDescription(),
                        p.getCategory().getDescription(), p.getBrand(), p.getUnit()
                )).toList();
    }

    public List<ProductStockDTO> getStock(String idQuincaillerie) {
        Quincaillerie quin = quincaillerieInterface.getQuincaillerie(idQuincaillerie).orElse(null);
        if (quin == null) throw new ResourceNotFoundException("La quincaillerie n'existe pas");

        List<ProductStockDTO> stockList = new ArrayList<>();
        List<Object[]> results = productInterface.getProductByQuincailleries(quin);

        for (Object[] res : results) {
            Price priceEntity = (Price) res[0];
            ProductStockDTO dto = mapToStockDTO(priceEntity, res[1]);
            stockList.add(dto);
        }
        return stockList;
    }



    @Transactional
    public void AddProduct(AddProductDTO dto, MultipartFile image, String uid, String qId) {
        if (priceInterface.ifAlreadyExistProductByQuincaillerie(dto.getName(), qId)) {
            throw new ProductExistException("Le produit existe déjà pour cette quincaillerie");
        }
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(qId).orElse(null);
        if(quincaillerie == null ){
            throw new ProductNotFoundException("La quincaillerie n'existe pas");
        }
        String imageUrl = "";

        if (image != null && !image.isEmpty()) {
            imageUrl = uploadImage.uploadImageSafely(image);
        }

        Product product = new Product();
        product.setIdProduct(GenerateID.GenerateProductID());
        product.setName(dto.getName());
        product.setImageUrl(imageUrl);
        product.setCategory(categoryInterface.getCategoty(dto.getCategoryId()));
        product.setBrand(dto.getBrand());
        product.setDescription(dto.getDescription());
        product.setUnit(dto.getUnite());
        productInterface.save(product);

        Price price = new Price();
        price.setIdPrice(GenerateID.GeneratePriceID());
        price.setProduct(product);
        price.setQuincaillerie(quincaillerie);
        price.setUser(sellerInterface.getByIdUser(uid));
        price.setUpdateDate(LocalDateTime.now());
        price.setPrice(dto.getSellingPrice());
        price.setPurchasePrice(dto.getPurchasePrice());
        price.setStock(dto.getStock());
        priceInterface.save(price);

        Stock stock = new Stock();
        stock.setIdStock(GenerateID.GenerateStockID());
        stock.setPrice(price);
        stock.setQuantity(price.getStock());
        stock.setDateMouvement(LocalDateTime.now());
        stock.setTypeMouvement(MouvementStock.ENTREE);
        stock.setComment("Ajout");

        stockInterface.save(stock);
    }

    @Transactional
    public void updateProduct(String idProduct, UpdateProductDTO dto, MultipartFile image, String qId) {
        Product product = productInterface.getProduct(idProduct);
        Price price = priceInterface.findByProductAndQuincaillerie(idProduct, qId).orElse(null);

        if (price == null) throw new ProductNotFoundException("Produit non trouvé pour cette quincaillerie");

        if (image != null && !image.isEmpty()) {

            if (product.getImageUrl() != null) {
                cloudinaryService.deleteImage(product.getImageUrl());
            }

            String newUrl = uploadImage.uploadImageSafely(image);
            if (newUrl != null){
                product.setImageUrl(newUrl);
            }
        }

        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getBrand() != null) product.setBrand(dto.getBrand());
        if (dto.getDescriptionProduit() != null) product.setDescription(dto.getDescriptionProduit());
        if (dto.getUnite() != null) product.setUnit(dto.getUnite());

        if (dto.getPurchasePrice() != null) price.setPurchasePrice(dto.getPurchasePrice());
        if (dto.getSellingPrice() != null) price.setPrice(dto.getSellingPrice());
        if (dto.getQuantite() != null) price.setStock(dto.getQuantite());

        price.setUpdateDate(LocalDateTime.now());

        productInterface.save(product);
        priceInterface.save(price);
    }

    @Transactional
    public void deleteProduct(String idProduct, String qId) {
        Price price = priceInterface.getPriceByProductAndQuincaillerie(idProduct, qId).orElse(null);
        if (price == null) throw new ProductNotFoundException("Ce Produit n'existe pas");

        Product product = productInterface.getProduct(idProduct);

        if (product != null && product.getImageUrl() != null) {
            cloudinaryService.deleteImage(product.getImageUrl());
        }

        priceInterface.deleteById(price.getIdPrice());

        if (productInterface.existsById(idProduct)) {
            productInterface.deleteById(idProduct);
        }
    }


    /*
    private String uploadImageSafely(MultipartFile image) {
        if (image == null || image.isEmpty()) return null;
        try {
            return cloudinaryService.uploadImageProduct(image);
        } catch (IOException e) {
            log.error("Erreur Cloudinary: {}", e.getMessage());
            return null;
        }
    }
    */

    private ProductStockDTO mapToStockDTO(Price entity, Object tauxObj) {
        ProductStockDTO dto = new ProductStockDTO();
        Product p = entity.getProduct();

        dto.setId(p.getIdProduct());
        dto.setName(p.getName());
        dto.setBrand(p.getBrand());
        dto.setCategory(p.getCategory().getName());
        dto.setStock(entity.getStock());
        dto.setUnit(p.getUnit());
        dto.setSellPrice(entity.getPrice().toString());
        dto.setImageUrl(p.getImageUrl());
        dto.setDescription(p.getDescription());
        dto.setPurchasePrice(entity.getPurchasePrice().toString());

        double taux = (tauxObj != null) ? ((Number) tauxObj).doubleValue() : 0.0;
        if (taux > 0) {
            double prixPromo = entity.getPrice().doubleValue() * (1 - (taux / 100));
            dto.setPricePromo(String.valueOf(prixPromo));
            dto.setTaux(String.valueOf(taux));
            dto.setInPromotion(true);
        } else {
            dto.setInPromotion(false);
        }
        return dto;
    }
}