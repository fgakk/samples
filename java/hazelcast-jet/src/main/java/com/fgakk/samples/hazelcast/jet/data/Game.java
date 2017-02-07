package com.fgakk.samples.hazelcast.jet.data;

import java.io.Serializable;

/**
 * A POJO for game output data.
 * Created by comsysto on 07/02/17.
 */
public class Game implements Serializable {

    private String name;

    private long price;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
