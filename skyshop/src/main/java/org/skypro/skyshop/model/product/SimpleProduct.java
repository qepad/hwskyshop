package org.skypro.skyshop.model.product;

import java.util.UUID;

public class SimpleProduct extends Product {

    private int price;


    public SimpleProduct(UUID id, String title, int price) {
        super(id, title);
        if (price <= 0) throw new IllegalArgumentException("цена должна быть больше 0");
        this.price = price;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public String toString() {
        return getTitle() +
                ": " +
                this.price +
                " руб.";
    }


}
