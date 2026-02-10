package parking.domain.repository;

import java.util.List;

public interface Repository<T> {
    T findById(int id);
    List<T> findAll();
    T save(T entity);
    boolean delete(int id);
}
