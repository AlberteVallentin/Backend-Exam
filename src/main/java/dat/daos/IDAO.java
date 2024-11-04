package dat.daos;

import dat.exceptions.ApiException;
import java.util.List;

/**
 * Generic DAO interface for basic CRUD operations
 * @param <T> The type of the entity/DTO
 * @param <ID> The type of the identifier
 */
public interface IDAO<T, ID> {
    // Basic CRUD operations
    T getById(ID id) throws ApiException;
    List<T> getAll() throws ApiException;
    T create(T t) throws ApiException;
    T update(ID id, T t) throws ApiException;
    void delete(ID id) throws ApiException;


}