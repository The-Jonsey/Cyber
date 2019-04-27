package com.thejonsey.cyber.Model;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Embeddable
@Table(name="File")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@ManyToOne
    //@JoinColumn
    private Integer id;
    private String filename;
    private Date uploaded;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fileid")
    private Set<Log> logs = new HashSet<>();

    public File() {

    }

    public File(String filename, Date uploaded) {
        this.filename = filename;
        this.uploaded = uploaded;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getUploaded() {
        return uploaded;
    }

    public void setUploaded(Date uploaded) {
        this.uploaded = uploaded;
    }

    public Set<Log> getLogs() {
        return logs;
    }

    public void setLogs(Set<Log> logs) {
        this.logs = logs;
    }
}
