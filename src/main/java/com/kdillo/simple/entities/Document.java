package com.kdillo.simple.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "documents", schema = "webapp")
public class Document {

    @Id
    @Column(nullable = false)
    private long id;

    @Column
    private UUID uid;

    @Column(nullable = false)
    private String doc_url;

    @Column
    private long parent_id;

    public Document(String doc_url) {
        this.doc_url = doc_url;
    }

    public Document(String doc_url, long parent_id) {
        this.doc_url = doc_url;
        this.parent_id = parent_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public String getDoc_url() {
        return doc_url;
    }

    public void setDoc_url(String doc_url) {
        this.doc_url = doc_url;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }
}
