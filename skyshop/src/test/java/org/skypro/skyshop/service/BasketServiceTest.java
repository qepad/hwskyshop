package org.skypro.skyshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.skyshop.exception.NoSuchProductException;
import org.skypro.skyshop.model.basket.ProductBasket;
import org.skypro.skyshop.model.basket.UserBasket;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.skypro.skyshop.model.product.Product;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasketServiceTest {

    @Mock
    private ProductBasket productBasket;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private BasketService basketService;

    // 1. Добавление несуществующего товара приводит к исключению
    @Test
    void whenAddingNonExistentProduct_thenThrowsNoSuchProductException() {
        UUID nonExistentProductId = UUID.randomUUID();
        when(storageService.getProductById(nonExistentProductId))
                .thenReturn(Optional.empty());

        NoSuchProductException exception = assertThrows(
                NoSuchProductException.class,
                () -> basketService.addProduct(nonExistentProductId)
        );

        assertEquals("Продукт с id " + nonExistentProductId + " не найден",
                exception.getMessage());

        // проверяем, что метод StorageService был вызван
        verify(storageService, times(1)).getProductById(nonExistentProductId);
        // проверяем, что addProduct не был вызван
        verify(productBasket, never()).addProduct(any(UUID.class));
    }

    // 2. Добавление существующего товара вызывает addProduct у ProductBasket
    @Test
    void whenAddingExistentProduct_thenCallsProductBasketAddProduct() {
        UUID existentProductId = UUID.randomUUID();
        SimpleProduct product = new SimpleProduct(existentProductId, "Молоко", 100);

        when(storageService.getProductById(existentProductId))
                .thenReturn(Optional.of(product));

        basketService.addProduct(existentProductId);

        verify(storageService, times(1)).getProductById(existentProductId);
        verify(productBasket, times(1)).addProduct(existentProductId);
    }

    // 3. Метод getUserBasket возвращает пустую корзину, если ProductBasket пуст
    @Test
    void whenProductBasketIsEmpty_thenReturnsEmptyUserBasket() {
        Map<UUID, Integer> emptyProducts = Collections.emptyMap();
        when(productBasket.getProducts()).thenReturn(emptyProducts);

        UserBasket userBasket = basketService.getUserBasket();

        assertNotNull(userBasket);
        assertTrue(userBasket.getItems().isEmpty());
        assertEquals(0, userBasket.getTotal());

        verify(productBasket, times(1)).getProducts();
        // StorageService не должен вызываться для пустой корзины
        verify(storageService, never()).getProductById(any(UUID.class));
    }

    // 4. Метод getUserBasket возвращает подходящую корзину, если в ProductBasket есть товары
    @Test
    void whenProductBasketHasProducts_thenReturnsUserBasketWithProducts() {
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        SimpleProduct product1 = new SimpleProduct(productId1, "Молоко", 100);
        SimpleProduct product2 = new SimpleProduct(productId2, "Хлеб", 50);

        Map<UUID, Integer> basketProducts = new HashMap<>();
        basketProducts.put(productId1, 2); // 2 молока по 100 = 200
        basketProducts.put(productId2, 1); // 1 хлеб по 50 = 50
        // Итого: 250

        when(productBasket.getProducts()).thenReturn(basketProducts);
        when(storageService.getProductById(productId1)).thenReturn(Optional.of(product1));
        when(storageService.getProductById(productId2)).thenReturn(Optional.of(product2));

        UserBasket userBasket = basketService.getUserBasket();

        assertNotNull(userBasket);
        assertEquals(2, userBasket.getItems().size());
        assertEquals(250, userBasket.getTotal()); // 2*100 + 1*50 = 250

        verify(productBasket, times(1)).getProducts();
        verify(storageService, times(2)).getProductById(productId1);
        verify(storageService, times(2)).getProductById(productId2);
    }

    // 5. Метод getUserBasket выбрасывает исключение если товар исчез из корзины
    @Test
    void whenProductInBasketNoLongerExists_thenThrowsNoSuchProductException() {
        UUID missingProductId = UUID.randomUUID();
        Map<UUID, Integer> basketProducts = new HashMap<>();
        basketProducts.put(missingProductId, 1);

        when(productBasket.getProducts()).thenReturn(basketProducts);
        when(storageService.getProductById(missingProductId)).thenReturn(Optional.empty());

        NoSuchProductException exception = assertThrows(
                NoSuchProductException.class,
                () -> basketService.getUserBasket()
        );

        assertEquals("Продукт с id " + missingProductId + " не найден",
                exception.getMessage());

        verify(productBasket, times(1)).getProducts();
        verify(storageService, times(1)).getProductById(missingProductId);
    }

    // 6. Добавление с null id вызывает исключение
    @Test
    void whenAddingNullProductId_thenThrowsNoSuchProductException() {
        when(storageService.getProductById(null)).thenReturn(Optional.empty());

        NoSuchProductException exception = assertThrows(
                NoSuchProductException.class,
                () -> basketService.addProduct(null)
        );

        assertEquals("Продукт с id null не найден", exception.getMessage());
        verify(storageService, times(1)).getProductById(null);
        verify(productBasket, never()).addProduct(any());
    }

    // 7. Множественное добавление одного товара
    @Test
    void whenAddingSameProductMultipleTimes_thenCallsAddProductEachTime() {
        UUID productId = UUID.randomUUID();
        SimpleProduct product = new SimpleProduct(productId, "тестовый товар", 200);

        when(storageService.getProductById(productId)).thenReturn(Optional.of(product));

        basketService.addProduct(productId);
        basketService.addProduct(productId);
        basketService.addProduct(productId);

        verify(storageService, times(3)).getProductById(productId);
        verify(productBasket, times(3)).addProduct(productId);
    }

    // 8. Добавление большого количества товаров
    @Test
    void whenBasketHasManyProducts_thenProcessesAllCorrectly() {
        // Arrange
        Map<UUID, Integer> manyProducts = new HashMap<>();
        int expectedTotal = 0;

        // Создаем 10 товаров с разными ценами и количествами
        for (int i = 0; i < 10; i++) {
            UUID productId = UUID.randomUUID();
            int price = (i + 1) * 10; // Цены: 10, 20, 30, ..., 100
            int quantity = i + 1;     // Количества: 1, 2, 3, ..., 10

            Product product = mock(Product.class);
            when(product.getPrice()).thenReturn(price);

            manyProducts.put(productId, quantity);
            when(storageService.getProductById(productId)).thenReturn(Optional.of(product));

            expectedTotal += price * quantity;
        }

        when(productBasket.getProducts()).thenReturn(manyProducts);

        UserBasket userBasket = basketService.getUserBasket();

        assertNotNull(userBasket);
        assertEquals(10, userBasket.getItems().size());
        assertEquals(expectedTotal, userBasket.getTotal());

        // проверка, что StorageService был вызван для каждого товара
        verify(storageService, times(20)).getProductById(any(UUID.class));
    }



}