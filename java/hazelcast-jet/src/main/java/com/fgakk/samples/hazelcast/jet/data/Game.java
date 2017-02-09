package com.fgakk.samples.hazelcast.jet.data;

import java.io.Serializable;

/**
 * A POJO for game output data.
 * Created by comsysto on 07/02/17.
 */
public class Game implements Serializable {

    private String name;

    private String price;

    public Game(final String name, final String price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {

        return name;
    }


    public String getPrice() {
        return price;
    }
}
