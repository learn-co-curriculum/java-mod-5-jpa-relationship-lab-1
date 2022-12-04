package org.example.model;

import javax.persistence.*;

@Entity
public class Country {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    private Capital capital;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Capital getCapital() {
        return capital;
    }

    public void setCapital(Capital capital) {
        this.capital = capital;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name  + '\'' +
                '}';
    }
}
