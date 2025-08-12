package org.skypro.skyshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.skypro.skyshop.model.search.SearchResult;
import org.skypro.skyshop.model.search.Searchable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private SearchService searchService;

    // Тест 1. Поиск в случае отсутствия объектов в StorageService
    @Test
    void whenStorageIsEmpty_thenReturnEmptyCollection() {
        // настройка мок-объекта
        when(storageService.getAllSearchables()).thenReturn(Collections.emptyList());

        // выполнение тестируемого метода
        Collection<SearchResult> results = searchService.search("запрос");

        // проверка результатов
        assertNotNull(results);
        assertTrue(results.isEmpty());

        // проверка, что метод getAllSearchables был вызван ровно один раз
        verify(storageService, times(1)).getAllSearchables();
    }

    // Тест 2. Поиск в случае, если объекты в StorageService есть, но нет подходящего
    @Test
    void whenObjectsExistButNoneMatch_thenReturnEmptyCollection() {
        // создание тестовых объектов
        List<Searchable> searchables = createTestSearchables();
        when(storageService.getAllSearchables()).thenReturn(searchables);

        // поиск того, чего нет
        Collection<SearchResult> results = searchService.search("несуществующий_запрос");

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(storageService, times(1)).getAllSearchables();
    }

    // Тест 3. Поиск, когда есть подходящий объект в StorageService
    @Test
    void whenSearchTermMatches_thenReturnMatchingResults() {
        List<Searchable> searchables = createTestSearchables();
        when(storageService.getAllSearchables()).thenReturn(searchables);

        // должно найти SimpleProduct
        Collection<SearchResult> results = searchService.search("молоко");

        assertNotNull(results);
        assertEquals(1, results.size());

        SearchResult result = results.iterator().next();
        assertEquals("молоко", result.getName());
        assertEquals("PRODUCT", result.getContentType());
        assertNotNull(result.getId());

        verify(storageService, times(1)).getAllSearchables();
    }

    // Тест 4. Поиск независимо от регистра
    @Test
    void whenSearchIsCaseInsensitive_thenReturnMatchingResults() {
        List<Searchable> searchables = createTestSearchables();
        when(storageService.getAllSearchables()).thenReturn(searchables);

        Collection<SearchResult> results = searchService.search("МОЛОКО");

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(storageService, times(1)).getAllSearchables();
    }

    // Тест 5. Поиск подстроки внутри названия
    @Test
    void whenSearchByPartialMatch_thenReturnMatchingResults() {
        List<Searchable> searchables = createTestSearchables();
        when(storageService.getAllSearchables()).thenReturn(searchables);

        // должно найти молоко
        Collection<SearchResult> results = searchService.search("лок");

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(storageService, times(1)).getAllSearchables();
    }

    // Тест 6. Когда найдено несколько подходящих объектов
    @Test
    void whenMultipleObjectsMatch_thenReturnAllMatching() {
        // добавляем объекты с одинаковой подстрокой
        List<Searchable> searchables = createTestSearchablesWithCommonTerm();
        when(storageService.getAllSearchables()).thenReturn(searchables);

        // ищем общий паттерн
        Collection<SearchResult> results = searchService.search("статья");

        assertNotNull(results);
        assertEquals(2, results.size()); // должно найти 2 статьи

        // проверяем, что все результаты имеют тип ARTICLE
        for (SearchResult result : results) {
            assertEquals("ARTICLE", result.getContentType());
        }

        verify(storageService, times(1)).getAllSearchables();
    }

    // Тест 7. Поиск с пустой строкой
    @Test
    void whenSearchWithEmptyString_thenReturnAllObjects() {
        List<Searchable> searchables = createTestSearchables();
        when(storageService.getAllSearchables()).thenReturn(searchables);

        Collection<SearchResult> results = searchService.search("");

        // должны вернуться все объекты
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(storageService, times(1)).getAllSearchables();
    }

    // Тест 8. Поиск null
    @Test
    void whenSearchWithNull_thenHandleGracefully() {
        List<Searchable> searchables = createTestSearchables();
        when(storageService.getAllSearchables()).thenReturn(searchables);

        assertThrows(NullPointerException.class, () -> {
            searchService.search(null);
        });

        verify(storageService, times(1)).getAllSearchables();
    }


    // методы для заполнения данных

    // создает набор тестовых объектов для поиска
    private List<Searchable> createTestSearchables() {
        List<Searchable> searchables = new ArrayList<>();

        SimpleProduct product = new SimpleProduct(
                UUID.randomUUID(),
                "молоко",
                100
        );

        Article article = new Article(
                UUID.randomUUID(),
                "Статья о хлебе",
                "С хлебом едят суп и остальное, или делают из него бутерброды"
        );

        searchables.add(product);
        searchables.add(article);

        return searchables;
    }

    // создание объектов с общим паттерном
    private List<Searchable> createTestSearchablesWithCommonTerm() {
        List<Searchable> searchables = new ArrayList<>();

        Article article1 = new Article(
                UUID.randomUUID(),
                "Первая статья",
                "Содержимое первой статьи"
        );

        Article article2 = new Article(
                UUID.randomUUID(),
                "Вторая статья",
                "Содержимое второй статьи"
        );

        searchables.add(article1);
        searchables.add(article2);

        return searchables;
    }


}
