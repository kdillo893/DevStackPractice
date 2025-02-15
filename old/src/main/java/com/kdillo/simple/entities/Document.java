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
    @Column(name="docid", nullable = false)
    public long id;

    @Column(name="uid")
    public UUID uid;

    @Column(name="doc_url", nullable = false)
    public String doc_url;

    @Column(name="parent_id", nullable = true)
    public Long parent_id;

    public Document(String doc_url) {
        this.doc_url = doc_url;
    }

    public Document(String doc_url, Long parent_id) {
        this.doc_url = doc_url;
        this.parent_id = parent_id;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", uid=" + uid +
                ", doc_url='" + doc_url + '\'' +
                ", parent_id=" + parent_id +
                '}';
    }
}
