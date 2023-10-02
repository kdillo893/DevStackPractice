package com.kdillo.simple.entities;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

import java.util.UUID;

@Entity
@Table(name = "documents", schema = "webapp")
public class Document implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private long id;

    @Column(name="uid")
    private UUID uid;

    @Column(name="doc_url", nullable = false)
    private String doc_url;

    @Column(name="parent_id", nullable = true)
    private Long parent_id;

    public Document(String doc_url) {
        this.doc_url = doc_url;
    }

    public Document(String doc_url, Long parent_id) {
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

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }
}
