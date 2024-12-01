package com.example.materialsmanagement;

import java.util.List;

public class Classe {
    private String id;
    private String name;
    private List<String> materialIds;

    public Classe() {
    }

    public Classe(String id, String name, List<String> materialIds) {
        this.id = id;
        this.name = name;
        this.materialIds = materialIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMaterialIds() {
        return materialIds;
    }

    public void setMaterialIds(List<String> materialIds) {
        this.materialIds = materialIds;
    }
}