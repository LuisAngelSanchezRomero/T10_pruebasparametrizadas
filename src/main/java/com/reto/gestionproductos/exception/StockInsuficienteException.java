package com.reto.gestionproductos.exception;

/**
 * Excepción lanzada cuando el stock solicitado es mayor que el disponible o cuando se intenta operar con stock insuficiente.
 */
public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
