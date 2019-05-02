package com.thejonsey.cyber.Model;

import javax.persistence.*;

@Entity
@Embeddable
@Table(name="Log")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String row;
    private Integer count;

    //@OneToMany(mappedBy = "File", cascade = CascadeType.ALL)
    //@JoinColumn(table = "File", name = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fileid")
    private File fileid;

    public Integer getId() {
        return id;
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

    public Log(String row, Integer count, File fileid) {
        this.row = row;
        this.count = count;
        this.fileid = fileid;
    }

    public File getFileid() {
        return fileid;
    }

    public void setFileid(File fileid) {
        this.fileid = fileid;
    }
}
