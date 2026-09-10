package com.reto.gestionproductos.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias del Modelo Producto")
class ProductoTest {

    @Test
    @DisplayName("Debe crear producto mediante constructor con argumentos y obtener sus valores")
    void debeCrearProductoConConstructorYGetters() {
        Producto producto = new Producto("PROD-001", "Laptop Gamer", 1299.99, 15);

        assertNotNull(producto);
        assertEquals("PROD-001", producto.getCodigo());
        assertEquals("Laptop Gamer", producto.getNombre());
        assertEquals(1299.99, producto.getPrecio(), 0.001);
        assertEquals(15, producto.getStock());
    }

    @Test
    @DisplayName("Debe actualizar valores mediante setters correctamente")
    void debeActualizarValoresConSetters() {
        Producto producto = new Producto();
        producto.setCodigo("PROD-002");
        producto.setNombre("Mouse Inalámbrico");
        producto.setPrecio(29.90);
        producto.setStock(50);

        assertEquals("PROD-002", producto.getCodigo());
        assertEquals("Mouse Inalámbrico", producto.getNombre());
        assertEquals(29.90, producto.getPrecio(), 0.001);
        assertEquals(50, producto.getStock());
    }

    @Test
    @DisplayName("Debe verificar igualdad con equals y hashCode para objetos con mismos atributos")
    void debeVerificarEqualsYHashCode() {
        Producto prod1 = new Producto("PROD-001", "Teclado", 45.0, 10);
        Producto prod2 = new Producto("PROD-001", "Teclado", 45.0, 10);
        Producto prod3 = new Producto("PROD-002", "Monitor", 200.0, 5);

        assertEquals(prod1, prod2);
        assertEquals(prod1.hashCode(), prod2.hashCode());
        assertNotEquals(prod1, prod3);
        assertNotEquals(prod1, null);
        assertNotEquals(prod1, "Otro Objeto");
        assertEquals(prod1, prod1);
    }

    @Test
    @DisplayName("Debe generar representación toString correcta")
    void debeGenerarToStringCorrecto() {
        Producto producto = new Producto("PROD-001", "Auriculares", 55.0, 20);
        String resultado = producto.toString();

        assertTrue(resultado.contains("PROD-001"));
        assertTrue(resultado.contains("Auriculares"));
        assertTrue(resultado.contains("55.0"));
        assertTrue(resultado.contains("20"));
    }
}
