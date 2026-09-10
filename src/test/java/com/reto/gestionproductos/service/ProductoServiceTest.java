package com.reto.gestionproductos.service;

import com.reto.gestionproductos.exception.ProductoInvalidoException;
import com.reto.gestionproductos.exception.ProductoNoEncontradoException;
import com.reto.gestionproductos.exception.StockInsuficienteException;
import com.reto.gestionproductos.model.Producto;
import com.reto.gestionproductos.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de ProductoService con Mockito")
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoEjemplo;

    @BeforeEach
    void setUp() {
        productoEjemplo = new Producto("P001", "Monitor 24 Pulgadas", 150.0, 20);
    }

    // =========================================================================
    // 1. Pruebas de Registro y Validaciones
    // =========================================================================

    @Test
    @DisplayName("1. Debe registrar un producto exitosamente cuando todos los datos son válidos")
    void debeRegistrarProductoExitosamente() {
        // Arrange
        when(productoRepository.existePorCodigo("P001")).thenReturn(false);
        when(productoRepository.guardar(any(Producto.class))).thenReturn(productoEjemplo);

        // Act
        Producto resultado = productoService.registrarProducto(productoEjemplo);

        // Assert
        assertNotNull(resultado);
        assertEquals("P001", resultado.getCodigo());
        assertEquals("Monitor 24 Pulgadas", resultado.getNombre());
        assertEquals(150.0, resultado.getPrecio());
        assertEquals(20, resultado.getStock());

        verify(productoRepository, times(1)).existePorCodigo("P001");
        verify(productoRepository, times(1)).guardar(productoEjemplo);
        verify(notificacionService, times(1)).notificarRegistroProducto(productoEjemplo);
    }

    @Test
    @DisplayName("2. Debe lanzar excepción al registrar un producto nulo")
    void debeLanzarExcepcionAlRegistrarProductoNulo() {
        ProductoInvalidoException excepcion = assertThrows(
                ProductoInvalidoException.class,
                () -> productoService.registrarProducto(null)
        );

        assertEquals("El producto no puede ser nulo.", excepcion.getMessage());
        verifyNoInteractions(productoRepository);
        verifyNoInteractions(notificacionService);
    }

    @Test
    @DisplayName("3. Debe lanzar excepción al registrar producto con código vacío o corto")
    void debeLanzarExcepcionAlRegistrarProductoConCodigoInvalido() {
        productoEjemplo.setCodigo("  ");
        assertThrows(ProductoInvalidoException.class, () -> productoService.registrarProducto(productoEjemplo));

        productoEjemplo.setCodigo("AB");
        assertThrows(ProductoInvalidoException.class, () -> productoService.registrarProducto(productoEjemplo));

        productoEjemplo.setCodigo(null);
        assertThrows(ProductoInvalidoException.class, () -> productoService.registrarProducto(productoEjemplo));
    }

    @Test
    @DisplayName("4. Debe lanzar excepción al registrar producto con nombre inválido")
    void debeLanzarExcepcionAlRegistrarProductoConNombreInvalido() {
        productoEjemplo.setNombre("");
        assertThrows(ProductoInvalidoException.class, () -> productoService.registrarProducto(productoEjemplo));

        productoEjemplo.setNombre("TV"); // Menos de 3 caracteres
        assertThrows(ProductoInvalidoException.class, () -> productoService.registrarProducto(productoEjemplo));

        productoEjemplo.setNombre(null);
        assertThrows(ProductoInvalidoException.class, () -> productoService.registrarProducto(productoEjemplo));
    }

    @Test
    @DisplayName("5. Debe lanzar excepción al registrar producto con precio <= 0 o inválido")
    void debeLanzarExcepcionAlRegistrarProductoConPrecioInvalido() {
        productoEjemplo.setPrecio(0.0);
        assertThrows(ProductoInvalidoException.class, () -> productoService.registrarProducto(productoEjemplo));

        productoEjemplo.setPrecio(-50.0);
        assertThrows(ProductoInvalidoException.class, () -> productoService.registrarProducto(productoEjemplo));

        productoEjemplo.setPrecio(Double.NaN);
        assertThrows(ProductoInvalidoException.class, () -> productoService.registrarProducto(productoEjemplo));
    }

    @Test
    @DisplayName("6. Debe lanzar excepción al registrar producto con stock negativo")
    void debeLanzarExcepcionAlRegistrarProductoConStockNegativo() {
        productoEjemplo.setStock(-1);
        assertThrows(ProductoInvalidoException.class, () -> productoService.registrarProducto(productoEjemplo));
    }

    @Test
    @DisplayName("7. Debe lanzar excepción al registrar producto con código ya existente")
    void debeLanzarExcepcionAlRegistrarProductoConCodigoDuplicado() {
        when(productoRepository.existePorCodigo("P001")).thenReturn(true);

        ProductoInvalidoException excepcion = assertThrows(
                ProductoInvalidoException.class,
                () -> productoService.registrarProducto(productoEjemplo)
        );

        assertTrue(excepcion.getMessage().contains("Ya existe un producto registrado"));
        verify(productoRepository, times(1)).existePorCodigo("P001");
        verify(productoRepository, never()).guardar(any());
        verifyNoInteractions(notificacionService);
    }

    // =========================================================================
    // 2. Pruebas de Búsqueda de Productos
    // =========================================================================

    @Test
    @DisplayName("8. Debe buscar producto por código exitosamente cuando existe")
    void debeBuscarProductoPorCodigoExitosamente() {
        when(productoRepository.buscarPorCodigo("P001")).thenReturn(Optional.of(productoEjemplo));

        Producto resultado = productoService.buscarProductoPorCodigo("P001");

        assertNotNull(resultado);
        assertEquals("P001", resultado.getCodigo());
        assertEquals("Monitor 24 Pulgadas", resultado.getNombre());
        verify(productoRepository, times(1)).buscarPorCodigo("P001");
    }

    @Test
    @DisplayName("9. Debe lanzar ProductoNoEncontradoException cuando el producto no existe")
    void debeLanzarExcepcionCuandoProductoNoExiste() {
        when(productoRepository.buscarPorCodigo("P999")).thenReturn(Optional.empty());

        assertThrows(
                ProductoNoEncontradoException.class,
                () -> productoService.buscarProductoPorCodigo("P999")
        );

        verify(productoRepository, times(1)).buscarPorCodigo("P999");
    }

    // =========================================================================
    // 3. Pruebas de Cálculo de Descuento
    // =========================================================================

    @Test
    @DisplayName("10. Debe calcular precio con descuento válido correctamente")
    void debeCalcularPrecioConDescuentoValido() {
        Producto producto = new Producto("P002", "Teclado Mecánico", 100.0, 10);

        double resultado = productoService.calcularPrecioConDescuento(producto, 20.0);

        assertEquals(80.0, resultado, 0.001);
    }

    @Test
    @DisplayName("11. Debe manejar casos límite de descuento: 0% y 100%")
    void debeManejarCasosLimiteDeDescuento() {
        Producto producto = new Producto("P003", "Mouse Pad", 50.0, 15);

        double conCeroDescuento = productoService.calcularPrecioConDescuento(producto, 0.0);
        assertEquals(50.0, conCeroDescuento, 0.001);

        double conCienDescuento = productoService.calcularPrecioConDescuento(producto, 100.0);
        assertEquals(0.0, conCienDescuento, 0.001);
    }

    @Test
    @DisplayName("12. Debe lanzar excepción para porcentajes de descuento fuera del rango [0, 100]")
    void debeLanzarExcepcionParaDescuentoInvalido() {
        Producto producto = new Producto("P004", "Webcam HD", 80.0, 5);

        assertThrows(
                ProductoInvalidoException.class,
                () -> productoService.calcularPrecioConDescuento(producto, -5.0)
        );

        assertThrows(
                ProductoInvalidoException.class,
                () -> productoService.calcularPrecioConDescuento(producto, 105.0)
        );
    }

    @Test
    @DisplayName("13. Debe lanzar excepción al calcular descuento con producto nulo")
    void debeLanzarExcepcionAlCalcularDescuentoConProductoNulo() {
        assertThrows(
                ProductoInvalidoException.class,
                () -> productoService.calcularPrecioConDescuento(null, 10.0)
        );
    }

    // =========================================================================
    // 4. Pruebas de Disponibilidad de Stock y Actualización
    // =========================================================================

    @Test
    @DisplayName("14. Debe verificar stock disponible cuando la cantidad solicitada es menor o igual al stock")
    void debeVerificarStockDisponibleCorrectamente() {
        when(productoRepository.buscarPorCodigo("P001")).thenReturn(Optional.of(productoEjemplo)); // stock = 20

        boolean disponible1 = productoService.hayStockDisponible("P001", 10);
        boolean disponible2 = productoService.hayStockDisponible("P001", 20); // Caso límite exacto

        assertTrue(disponible1);
        assertTrue(disponible2);
        verify(productoRepository, times(2)).buscarPorCodigo("P001");
    }

    @Test
    @DisplayName("15. Debe indicar stock no disponible cuando la cantidad solicitada supera el stock")
    void debeIndicarStockNoDisponibleCuandoSuperaStock() {
        when(productoRepository.buscarPorCodigo("P001")).thenReturn(Optional.of(productoEjemplo)); // stock = 20

        boolean disponible = productoService.hayStockDisponible("P001", 21);

        assertFalse(disponible);
        verify(productoRepository, times(1)).buscarPorCodigo("P001");
    }

    @Test
    @DisplayName("16. Debe lanzar excepción al consultar stock con cantidad solicitada <= 0")
    void debeLanzarExcepcionConCantidadSolicitadaInvalida() {
        assertThrows(
                ProductoInvalidoException.class,
                () -> productoService.hayStockDisponible("P001", 0)
        );

        assertThrows(
                ProductoInvalidoException.class,
                () -> productoService.hayStockDisponible("P001", -5)
        );
    }

    @Test
    @DisplayName("17. Debe actualizar stock por venta exitosamente sin alerta si stock > 5")
    void debeActualizarStockPorVentaExitosamente() {
        when(productoRepository.buscarPorCodigo("P001")).thenReturn(Optional.of(productoEjemplo)); // stock inicial 20

        int stockRestante = productoService.actualizarStockPorVenta("P001", 10);

        assertEquals(10, stockRestante);
        assertEquals(10, productoEjemplo.getStock());
        verify(productoRepository, times(1)).actualizar(productoEjemplo);
        verify(notificacionService, never()).enviarAlertaStockBajo(anyString(), anyInt());
    }

    @Test
    @DisplayName("18. Debe actualizar stock y emitir alerta de stock bajo cuando stock restante <= 5")
    void debeActualizarStockYEmitirAlertaStockBajo() {
        when(productoRepository.buscarPorCodigo("P001")).thenReturn(Optional.of(productoEjemplo)); // stock inicial 20

        int stockRestante = productoService.actualizarStockPorVenta("P001", 17); // queda 3

        assertEquals(3, stockRestante);
        assertEquals(3, productoEjemplo.getStock());
        verify(productoRepository, times(1)).actualizar(productoEjemplo);
        verify(notificacionService, times(1)).enviarAlertaStockBajo("P001", 3);
    }

    @Test
    @DisplayName("19. Debe lanzar StockInsuficienteException cuando la cantidad vendida supera el stock")
    void debeLanzarExcepcionAlVenderMasQueElStockDisponible() {
        when(productoRepository.buscarPorCodigo("P001")).thenReturn(Optional.of(productoEjemplo)); // stock = 20

        StockInsuficienteException excepcion = assertThrows(
                StockInsuficienteException.class,
                () -> productoService.actualizarStockPorVenta("P001", 25)
        );

        assertTrue(excepcion.getMessage().contains("Stock insuficiente"));
        verify(productoRepository, never()).actualizar(any());
        verifyNoInteractions(notificacionService);
    }

    @Test
    @DisplayName("20. Debe lanzar excepción al actualizar stock con cantidad vendida <= 0")
    void debeLanzarExcepcionAlActualizarStockConCantidadInvalida() {
        assertThrows(
                ProductoInvalidoException.class,
                () -> productoService.actualizarStockPorVenta("P001", 0)
        );

        assertThrows(
                ProductoInvalidoException.class,
                () -> productoService.actualizarStockPorVenta("P001", -2)
        );
    }
}
