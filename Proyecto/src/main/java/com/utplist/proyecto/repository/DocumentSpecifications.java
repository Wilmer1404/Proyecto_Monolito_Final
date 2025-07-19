package com.utplist.proyecto.repository;

import com.utplist.proyecto.model.Document;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public final class DocumentSpecifications {

    private DocumentSpecifications() { /* util class */ }

    public static Specification<Document> tituloLike(String titulo) {
        if (titulo == null || titulo.isBlank()) return null;
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("titulo")), "%" + titulo.toLowerCase() + "%");
    }

    public static Specification<Document> categoriaEq(String categoria) {
        if (categoria == null || categoria.isBlank()) return null;
        return (root, query, cb) -> cb.equal(root.get("categoria"), categoria);
    }

    public static Specification<Document> autorEq(String autor) {
        if (autor == null || autor.isBlank()) return null;
        return (root, query, cb) -> cb.equal(root.get("autorCorreo"), autor);
    }
} 