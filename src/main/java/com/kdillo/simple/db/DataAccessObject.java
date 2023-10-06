package com.kdillo.simple.db;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@SuppressWarnings("unused")
public interface DataAccessObject<T> {

    Optional<T> get(UUID uuid);

    List<T> getAll();

    void save(T t);

    void update(T t, Object... objs);

    void delete(T t);

}
