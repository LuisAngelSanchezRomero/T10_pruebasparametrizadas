package com.reto.gestionproductos.exception;

/**
 * Excepción lanzada cuando no se encuentra un producto por su código identificador.
 */
public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
