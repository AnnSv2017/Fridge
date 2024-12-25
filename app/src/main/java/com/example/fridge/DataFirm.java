package com.example.fridge;

public class DataFirm {
    private Integer id;
    private String name;

    public DataFirm(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
