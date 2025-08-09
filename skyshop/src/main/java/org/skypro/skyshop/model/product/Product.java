package org.skypro.skyshop.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.skypro.skyshop.model.search.Searchable;

import java.util.Objects;
import java.util.UUID;


public abstract class Product implements Searchable {

    private final UUID id;
    private final String title;


    public Product(UUID id, String title) {
        if (id == null) throw new IllegalArgumentException("id не может быть null");
        if (!title.isBlank() && !title.isEmpty() && title != null) {
            this.id = id;
            this.title = title;
        } else {
            throw new IllegalArgumentException("название продукта не может быть пустым");
        }
    }

    @Override
    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public abstract int getPrice();

    public abstract boolean isSpecial();

    @Override
    @JsonIgnore
    public String getSearchTerm() {
        return this.title;
    }

    @Override
    @JsonIgnore
    public String getContentType() {
        return "PRODUCT";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(title, product.title);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title);
    }
}
