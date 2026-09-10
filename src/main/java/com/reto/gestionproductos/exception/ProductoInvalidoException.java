package com.reto.gestionproductos.exception;

/**
 * Excepción lanzada cuando los datos de un producto no cumplen con las reglas de validación de negocio.
 */
public class ProductoInvalidoException extends RuntimeException {
    public ProductoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
