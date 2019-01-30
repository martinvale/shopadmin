package com.ibiscus.shopnchek.application.shopper;

public class EditedShopper extends NewShopper {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "EditedShopper{" +
                "id=" + id +
                "} " + super.toString();
    }
}
