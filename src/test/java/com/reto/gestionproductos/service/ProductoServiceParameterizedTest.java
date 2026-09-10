package com.reto.gestionproductos.service;

import com.reto.gestionproductos.exception.ProductoInvalidoException;
import com.reto.gestionproductos.model.Producto;
import com.reto.gestionproductos.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Parametrizadas de ProductoService con JUnit 5")
class ProductoServiceParameterizedTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private ProductoService productoService;

    // =========================================================================
    // 1. Prueba Parametrizada: Validación de Precios Válidos e Inválidos
    // =========================================================================

    @ParameterizedTest(name = "Iteración {index} => Precio {0} es válido: {1}")
    @CsvSource({
            "0.01, true",
            "10.0, true",
            "50.0, true",
            "999.99, true",
            "0.0, false",
            "-0.01, false",
            "-10.0, false",
            "-100.0, false"
    })
    @DisplayName("Parametrizada 1: Validación de diversos valores de precios (casos válidos, límites e inválidos)")
    void debeValidarPreciosParametrizados(double precio, boolean esperadoValido) {
        if (esperadoValido) {
            assertTrue(productoService.validarPrecio(precio));
        } else {
            assertThrows(ProductoInvalidoException.class, () -> productoService.validarPrecio(precio));
        }
    }

    // =========================================================================
    // 2. Prueba Parametrizada: Cálculo de Precios con Diferentes Descuentos
    // =========================================================================

    @ParameterizedTest(name = "Iteración {index} => Precio base: {0}, Descuento: {1}% => Precio Final: {2}")
    @CsvSource({
            "100.0, 0.0, 100.00",
            "100.0, 10.0, 90.00",
            "200.0, 25.0, 150.00",
            "50.0,  50.0, 25.00",
            "80.0,  75.0, 20.00",
            "150.0, 100.0, 0.00",
            "99.90, 15.0, 84.92"
    })
    @DisplayName("Parametrizada 2: Cálculo de precio final con diferentes porcentajes de descuento")
    void debeCalcularPrecioConVariosDescuentos(double precioBase, double porcentajeDescuento, double precioEsperado) {
        Producto producto = new Producto("P-PARAM", "Producto Test", precioBase, 10);

        double resultado = productoService.calcularPrecioConDescuento(producto, porcentajeDescuento);

        assertEquals(precioEsperado, resultado, 0.01);
    }

    // =========================================================================
    // 3. Prueba Parametrizada: Disponibilidad de Stock con Simulación Mockito
    // =========================================================================

    @ParameterizedTest(name = "Iteración {index} => Stock en Repo: {0}, Solicitado: {1} => Disponible: {2}")
    @CsvSource({
            "50, 10, true",
            "10, 10, true",   // Caso límite exacto
            "10, 11, false",  // Límite superior inmediato
            "5,  20, false",
            "1,  1,  true",   // Caso mínimo unitario
            "0,  1,  false"   // Sin stock
    })
    @DisplayName("Parametrizada 3: Verificación de disponibilidad de stock con datos simulados vía Mockito")
    void debeDeterminarDisponibilidadStockParametrizada(int stockEnRepo, int cantidadSolicitada, boolean esperadoDisponible) {
        Producto productoSimulado = new Producto("PROD-STOCK", "Memoria RAM", 60.0, stockEnRepo);
        when(productoRepository.buscarPorCodigo("PROD-STOCK")).thenReturn(Optional.of(productoSimulado));

        boolean resultado = productoService.hayStockDisponible("PROD-STOCK", cantidadSolicitada);

        assertEquals(esperadoDisponible, resultado);
    }

    // =========================================================================
    // 4. Prueba Parametrizada: Validación de Nombres de Producto
    // =========================================================================

    @ParameterizedTest(name = "Iteración {index} => Nombre: ''{0}'' => Es válido: {1}")
    @CsvSource({
            "Laptop Gamer, true",
            "CPU, true",            // Límite inferior exacto (3 caracteres)
            "A, false",             // Inválido por corto (1 caracter)
            "AB, false",            // Inválido por corto (2 caracteres)
            "'   ', false",         // Solo espacios
            "'', false"             // Cadena vacía
    })
    @DisplayName("Parametrizada 4: Validación de longitud y formato de nombres de producto")
    void debeValidarNombresDeProductoParametrizados(String nombre, boolean esValido) {
        if (esValido) {
            assertTrue(productoService.validarNombre(nombre));
        } else {
            assertThrows(ProductoInvalidoException.class, () -> productoService.validarNombre(nombre));
        }
    }

    // =========================================================================
    // 5. Prueba Parametrizada: Validación de Stock No Negativo
    // =========================================================================

    @ParameterizedTest(name = "Iteración {index} => Stock inválido: {0}")
    @ValueSource(ints = {-1, -5, -100, -9999})
    @DisplayName("Parametrizada 5: Rechazo de stocks negativos con @ValueSource")
    void debeRechazarStocksNegativosParametrizados(int stockInvalido) {
        assertThrows(ProductoInvalidoException.class, () -> productoService.validarStock(stockInvalido));
    }
}
