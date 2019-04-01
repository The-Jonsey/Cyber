package com.thejonsey.cyber;

import javax.persistence.*;

@Entity
@Embeddable
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String hash;
    private String row;
    private Integer count;

    public Integer getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Log() {

    }

    public Log(String hash, String row, Integer count) {
        this.hash = hash;
        this.row = row;
        this.count = count;
    }
}
