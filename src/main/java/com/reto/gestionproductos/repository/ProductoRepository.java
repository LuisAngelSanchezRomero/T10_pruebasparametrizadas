package com.reto.gestionproductos.repository;

import com.reto.gestionproductos.model.Producto;
import java.util.Optional;

/**
 * Interfaz de repositorio para persistencia y consulta de productos.
 * En las pruebas unitarias, sus comportamientos serán simulados utilizando Mockito (@Mock).
 */
public interface ProductoRepository {

    Producto guardar(Producto producto);

    Optional<Producto> buscarPorCodigo(String codigo);

    boolean existePorCodigo(String codigo);

    void actualizar(Producto producto);

    boolean eliminar(String codigo);
}
