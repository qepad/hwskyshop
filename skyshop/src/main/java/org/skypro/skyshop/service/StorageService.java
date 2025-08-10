package org.skypro.skyshop.service;

import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.DiscountedProduct;
import org.skypro.skyshop.model.product.FixPriceProduct;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.skypro.skyshop.model.search.Searchable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StorageService {

    private final Map<UUID, Product> productStorage;
    private final Map<UUID, Article> articleStorage;


    public StorageService() {
        this.productStorage = new HashMap<>();
        this.articleStorage = new HashMap<>();
        initTestData();
    }

    // метод для заполнения мап тестовыми данными
    private void initTestData() {
        Article article1 = new Article(UUID.randomUUID(), "статья о картошке", "картошка лежит под землей, но не очень глубоко");
        Article article2 = new Article(UUID.randomUUID(), "статья о яблоках", "яблоки можно подавать с белым вином или в составе фруктового салата");
        Article article3 = new Article(UUID.randomUUID(), "статья о помидорах", "помидоры растут в огородах, из них делают сок, соус или салат");
        articleStorage.put(article1.getId(), article1);
        articleStorage.put(article2.getId(), article2);

        Product product1 = new SimpleProduct(UUID.randomUUID(), "Картошка", 50);
        Product product2 = new DiscountedProduct(UUID.randomUUID(), "Морковь", 100, 10);
        Product product3 = new FixPriceProduct(UUID.randomUUID(), "Яблоко");
        productStorage.put(product1.getId(), product1);
        productStorage.put(product2.getId(), product2);
        productStorage.put(product3.getId(), product3);
    }

    // метод для получения всех статей
    public Collection<Article> getAllArticles() {
        return articleStorage.values();
    }

    // метод для получения всех продуктов
    public Collection<Product> getAllProducts() {
        return productStorage.values();
    }

    // метод для получения всех searchable - статьи и продукты
    public Collection<Searchable> getAllSearchables() {
        List<Searchable> searchables = new ArrayList<>();
        searchables.addAll(articleStorage.values());
        searchables.addAll(productStorage.values());
        return searchables;
    }

    public Optional<Product> getProductById(UUID id) {
        return Optional.ofNullable(productStorage.get(id));
    }




}
