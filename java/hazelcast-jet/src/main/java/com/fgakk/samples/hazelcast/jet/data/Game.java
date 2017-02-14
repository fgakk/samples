package com.fgakk.samples.hazelcast.jet.data;

import java.io.Serializable;

/**
 * A POJO for game output data.
 * Created by comsysto on 07/02/17.
 */
public class Game implements Serializable {

    private String name;

    private String price;


    public Game() {
    }

    public Game(final String name, final String price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Game{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
