package org.skypro.skyshop.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class ShopError {

    @JsonProperty("code")
    private final String code;

    @JsonProperty("message")
    private final String message;

    public ShopError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
