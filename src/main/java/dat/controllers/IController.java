package dat.controllers;

import dat.exceptions.ApiException;
import dat.exceptions.ValidationException;
import io.javalin.http.Context;

/**
 * Generic interface for REST controllers
 * Defines basic CRUD operations and validation methods
 * @param <T> The type of the DTO
 * @param <ID> The type of the identifier
 */
public interface IController<T, ID> {
    void read(Context ctx) throws ApiException, ValidationException;;
    void readAll(Context ctx) throws ApiException;
    void create(Context ctx) throws ApiException, ValidationException;;
    void update(Context ctx) throws ApiException, ValidationException;;
    void delete(Context ctx) throws ApiException, ValidationException;;
    boolean validatePrimaryKey(ID id) throws ApiException;;
    T validateEntity(Context ctx) throws ApiException, ValidationException;;
}