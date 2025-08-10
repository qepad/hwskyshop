package org.skypro.skyshop.service;

import org.skypro.skyshop.model.basket.BasketItem;
import org.skypro.skyshop.model.basket.ProductBasket;
import org.skypro.skyshop.model.basket.UserBasket;
import org.skypro.skyshop.model.product.Product;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BasketService {

    private final ProductBasket productBasket;
    private final StorageService storageService;


    public BasketService(ProductBasket productBasket, StorageService storageService) {
        this.productBasket = productBasket;
        this.storageService = storageService;
    }

    public void addProduct(UUID id) {
        if (!storageService.getProductById(id).isPresent()) {
            throw new IllegalArgumentException();
        }
        productBasket.addProduct(id);
    }

    public UserBasket getUserBasket() {
        return productBasket.getProducts().entrySet().stream()
                .map(entry -> {
                    UUID productId = entry.getKey();
                    Integer quantity = entry.getValue();
                    Product product = storageService.getProductById(productId).get();
                    return new BasketItem(product, quantity);
                })
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        UserBasket::new
                ));
    }


}