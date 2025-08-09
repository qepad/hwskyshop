package org.skypro.skyshop.model.product;

import java.util.UUID;

public class DiscountedProduct extends Product {

    private int basicPrice;
    private int discount;


    public DiscountedProduct(UUID id, String title, int basicPrice, int discount) {
        super(id, title);
        if (basicPrice <= 0) {
            throw new IllegalArgumentException("цена должна быть больше 0");
        }
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("процент скидки должен быть от 0 до 100");
        }
        this.basicPrice = basicPrice;
        this.discount = discount;
    }


    public int getBasicPrice () {
        return this.basicPrice;
    }

    @Override
    public int getPrice() {
        return basicPrice - (basicPrice * discount / 100);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public String toString() {
        return getTitle() +
                ": " +
                getPrice() +
                " руб. (скидка " +
                this.discount +
                "%, цена без скидки - " +
                getBasicPrice() +
                " руб.)";
    }
}
