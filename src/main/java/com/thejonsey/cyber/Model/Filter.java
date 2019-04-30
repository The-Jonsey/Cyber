package com.thejonsey.cyber.Model;


import javax.persistence.*;

@Entity
@Embeddable
@Table(name="Filter")
public class Filter {

    public Filter() {

    }

    public Filter(String filter, File fileid) {
        this.filter = filter;
        this.fileid = fileid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public File getFileid() {
        return fileid;
    }

    public void setFileid(File fileid) {
        this.fileid = fileid;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String filter;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fileid")
    private File fileid;
}
