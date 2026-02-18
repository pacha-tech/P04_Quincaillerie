package com.ict300.P04;

import com.ict300.P04.DTO.user.request.LoginUserDTO;
import com.ict300.P04.Entite.Price;
import com.ict300.P04.Service.user.AuthUserService;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.favoriteQuincaillerie.FavoriteQuincaillerieInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import com.ict300.P04.repository.interfaces.product.ProductInterface;
import com.ict300.P04.repository.interfaces.user.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class P04Application implements CommandLineRunner {

	@Autowired
	private PriceInterface priceInterface;

	@Autowired
	private FavoriteQuincaillerieInterface favoriteQuincaillerieInterface;

	@Autowired
	private UserInterface userInterface;

	@Autowired
	private ProductInterface productInterface;

	@Autowired
	private AuthUserService authUserService;

	@Autowired
	private GenerateID generateID;

	LoginUserDTO loginUserDTO = new LoginUserDTO("pacha","pacha1225i");


	public static void main(String[] args) {
		SpringApplication.run(P04Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("=== Test de la recherche avancée ===");

		BigDecimal maxPrice = BigDecimal.valueOf(5000);  // 5000 FCFA max

		try {
			List<Price> prices = priceInterface.searchAdvanced("ciment", null, null, maxPrice);
			long countFavorite = favoriteQuincaillerieInterface.count();
			boolean ifExist = authUserService.login(loginUserDTO);

			if (prices.isEmpty()) {
				System.out.println("Aucun résultat trouvé avec ces critères.");
			} else {
				System.out.println("Résultats trouvés : " + prices.size());
				System.out.println("Résultats trouvés dans favorie : " + countFavorite);
				System.out.println("l'Utilisateur avec le nom pacha existe ? : " + ifExist);
				System.out.println("le nombre de ciment est : "+productInterface.findByNameContainingIgnoreCase("ciment").size());

				for(int i =0 ; i<10 ; i++) {
					System.out.println("ID"+i+":"+generateID.GenerateUserID());
				}

				for (Price p : prices) {
					System.out.println("Prix : " + p.getPrice() +
							" | Stock : " + p.getStock() +
							" | Quincaillerie : " + p.getQuincaillerie().getStoreName() +
							" | Produit : " + p.getProduct().getName());
				}
			}
		} catch (Exception e) {
			System.out.println("Erreur lors de la recherche : " + e.getMessage());
			e.printStackTrace();
		}

	}
}