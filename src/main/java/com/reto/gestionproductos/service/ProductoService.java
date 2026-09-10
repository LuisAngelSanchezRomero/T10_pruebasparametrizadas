package com.reto.gestionproductos.service;

import com.reto.gestionproductos.exception.ProductoInvalidoException;
import com.reto.gestionproductos.exception.ProductoNoEncontradoException;
import com.reto.gestionproductos.exception.StockInsuficienteException;
import com.reto.gestionproductos.model.Producto;
import com.reto.gestionproductos.repository.ProductoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Servicio de lógica de negocio para la gestión de productos.
 * Cumple con los requerimientos mínimos del Reto 3:
 * 1. Crear o registrar producto
 * 2. Validar nombre del producto
 * 3. Validar precio del producto
 * 4. Validar stock del producto
 * 5. Calcular precio final con descuento
 * 6. Determinar disponibilidad de stock
 */
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final NotificacionService notificacionService;

    public ProductoService(ProductoRepository productoRepository, NotificacionService notificacionService) {
        this.productoRepository = productoRepository;
        this.notificacionService = notificacionService;
    }

    /**
     * 1. Registra un nuevo producto tras validar todos sus atributos.
     *
     * @param producto Producto a registrar
     * @return Producto registrado
     * @throws ProductoInvalidoException si algún campo no cumple las reglas de negocio
     */
    public Producto registrarProducto(Producto producto) {
        if (producto == null) {
            throw new ProductoInvalidoException("El producto no puede ser nulo.");
        }

        validarCodigo(producto.getCodigo());
        validarNombre(producto.getNombre());
        validarPrecio(producto.getPrecio());
        validarStock(producto.getStock());

        if (productoRepository.existePorCodigo(producto.getCodigo())) {
            throw new ProductoInvalidoException("Ya existe un producto registrado con el código: " + producto.getCodigo());
        }

        Producto guardado = productoRepository.guardar(producto);
        if (notificacionService != null) {
            notificacionService.notificarRegistroProducto(guardado);
        }
        return guardado;
    }

    /**
     * Valida que el código del producto sea válido (no nulo, no vacío, longitud >= 3).
     *
     * @param codigo Código a validar
     */
    public void validarCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ProductoInvalidoException("El código del producto no puede estar vacío.");
        }
        if (codigo.trim().length() < 3) {
            throw new ProductoInvalidoException("El código debe contener al menos 3 caracteres.");
        }
    }

    /**
     * 2. Valida el nombre del producto: no nulo, no vacío y mínimo 3 caracteres.
     *
     * @param nombre Nombre a validar
     * @return true si es válido
     * @throws ProductoInvalidoException si no cumple las reglas
     */
    public boolean validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ProductoInvalidoException("El nombre del producto no puede ser nulo ni estar vacío.");
        }
        if (nombre.trim().length() < 3) {
            throw new ProductoInvalidoException("El nombre del producto debe tener al menos 3 caracteres.");
        }
        return true;
    }

    /**
     * 3. Valida el precio del producto: debe ser estrictamente mayor a 0.
     *
     * @param precio Precio a validar
     * @return true si es válido
     * @throws ProductoInvalidoException si precio <= 0
     */
    public boolean validarPrecio(double precio) {
        if (Double.isNaN(precio) || Double.isInfinite(precio) || precio <= 0.0) {
            throw new ProductoInvalidoException("El precio del producto debe ser un número positivo mayor que cero.");
        }
        return true;
    }

    /**
     * 4. Valida el stock del producto: debe ser un número entero mayor o igual a cero.
     *
     * @param stock Stock a validar
     * @return true si es válido
     * @throws ProductoInvalidoException si stock < 0
     */
    public boolean validarStock(int stock) {
        if (stock < 0) {
            throw new ProductoInvalidoException("El stock no puede ser un número negativo.");
        }
        return true;
    }

    /**
     * 5. Calcula el precio final de un producto aplicando un porcentaje de descuento (0% a 100%).
     * Redondea el resultado a 2 decimales.
     *
     * @param producto           Producto sobre el cual aplicar el descuento
     * @param porcentajeDescuento Porcentaje entre 0.0 y 100.0
     * @return Precio con descuento aplicado
     * @throws ProductoInvalidoException si el producto es nulo o el descuento está fuera de rango
     */
    public double calcularPrecioConDescuento(Producto producto, double porcentajeDescuento) {
        if (producto == null) {
            throw new ProductoInvalidoException("El producto no puede ser nulo para calcular el descuento.");
        }
        validarPrecio(producto.getPrecio());

        if (porcentajeDescuento < 0.0 || porcentajeDescuento > 100.0) {
            throw new ProductoInvalidoException("El porcentaje de descuento debe estar comprendido entre 0 y 100.");
        }

        BigDecimal precioBase = BigDecimal.valueOf(producto.getPrecio());
        BigDecimal factorDescuento = BigDecimal.valueOf(1.0 - (porcentajeDescuento / 100.0));
        BigDecimal precioFinal = precioBase.multiply(factorDescuento).setScale(2, RoundingMode.HALF_UP);

        return precioFinal.doubleValue();
    }

    /**
     * 6. Determina si un producto tiene stock disponible para satisfacer una cantidad solicitada.
     *
     * @param codigo             Código del producto
     * @param cantidadSolicitada Cantidad requerida
     * @return true si hay stock suficiente, false si no
     * @throws ProductoInvalidoException si la cantidad es <= 0
     * @throws ProductoNoEncontradoException si el producto no existe
     */
    public boolean hayStockDisponible(String codigo, int cantidadSolicitada) {
        if (cantidadSolicitada <= 0) {
            throw new ProductoInvalidoException("La cantidad solicitada debe ser mayor a cero.");
        }

        Producto producto = buscarProductoPorCodigo(codigo);
        return producto.getStock() >= cantidadSolicitada;
    }

    /**
     * Busca un producto por su código en el repositorio.
     *
     * @param codigo Código del producto
     * @return Producto encontrado
     * @throws ProductoNoEncontradoException si no existe
     */
    public Producto buscarProductoPorCodigo(String codigo) {
        validarCodigo(codigo);
        return productoRepository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con código: " + codigo));
    }

    /**
     * Actualiza el stock de un producto tras una venta y emite alerta si el stock restante es bajo (<= 5).
     *
     * @param codigo           Código del producto
     * @param cantidadVendida  Cantidad a descontar del stock
     * @return Stock restante
     * @throws StockInsuficienteException si el stock actual es menor a la cantidad solicitada
     */
    public int actualizarStockPorVenta(String codigo, int cantidadVendida) {
        if (cantidadVendida <= 0) {
            throw new ProductoInvalidoException("La cantidad vendida debe ser mayor que cero.");
        }

        Producto producto = buscarProductoPorCodigo(codigo);

        if (producto.getStock() < cantidadVendida) {
            throw new StockInsuficienteException("Stock insuficiente. Stock actual: " + producto.getStock() +
                    ", cantidad solicitada: " + cantidadVendida);
        }

        int nuevoStock = producto.getStock() - cantidadVendida;
        producto.setStock(nuevoStock);
        productoRepository.actualizar(producto);

        if (nuevoStock <= 5 && notificacionService != null) {
            notificacionService.enviarAlertaStockBajo(codigo, nuevoStock);
        }

        return nuevoStock;
    }
}
