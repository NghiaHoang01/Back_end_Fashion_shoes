package com.example.Entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class Size {
    private int name;
    private int quantity;

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Size() {
    }

    public Size(int name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
}
