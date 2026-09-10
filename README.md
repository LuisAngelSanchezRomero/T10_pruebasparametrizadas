# Gestión de Productos - Reto 3 (S08 | AP3)

Proyecto Java implementado con **JUnit 5**, **Mockito**, pruebas parametrizadas con `@ParameterizedTest`, análisis de cobertura con **JaCoCo** y automatización continua mediante **Jenkins**.

---

## 🚀 Tecnologías y Herramientas

- **Lenguaje:** Java 17 (OpenJDK 17)
- **Gestor de Dependencias:** Apache Maven 3.9+
- **Framework de Pruebas:** JUnit 5 Jupiter (`junit-jupiter-engine`, `junit-jupiter-params`)
- **Framework de Simulación (Mocks):** Mockito 5 (`mockito-core`, `mockito-junit-jupiter`)
- **Métricas de Cobertura:** JaCoCo Maven Plugin 0.8.11 (Meta alcanzada: **100% de líneas**, **89% de ramas**)
- **Integración Continua:** Jenkins Pipeline Declarativo (`Jenkinsfile`)

---

## 📂 Estructura del Proyecto

```
pruebasunitarias/
├── pom.xml                                               # Configuración Maven, JUnit 5, Mockito y JaCoCo
├── Jenkinsfile                                           # Pipeline declarativo CI/CD para Jenkins
├── README.md                                             # Documentación técnica del proyecto
├── prompt_para_chatgpt_generar_docx.txt                  # Prompt para generación del documento Word formal
└── src/
    ├── main/java/com/reto/gestionproductos/
    │   ├── model/
    │   │   └── Producto.java                             # Entidad con código, nombre, precio y stock
    │   ├── repository/
    │   │   └── ProductoRepository.java                   # Interfaz de persistencia (simulada con @Mock)
    │   ├── service/
    │   │   ├── NotificacionService.java                  # Interfaz de alertas externas (simulada con @Mock)
    │   │   └── ProductoService.java                      # Lógica de negocio (6+ métodos clave)
    │   └── exception/
    │       ├── ProductoInvalidoException.java            # Excepción para validaciones de negocio
    │       ├── StockInsuficienteException.java           # Excepción para operaciones sin stock suficiente
    │       └── ProductoNoEncontradoException.java        # Excepción para productos no existentes
    └── test/java/com/reto/gestionproductos/
        ├── model/
        │   └── ProductoTest.java                         # 4 pruebas unitarias de la entidad
        └── service/
            ├── ProductoServiceTest.java                  # 20 pruebas unitarias con JUnit 5 y Mockito
            └── ProductoServiceParameterizedTest.java     # 31 ejecuciones parametrizadas (@CsvSource/@ValueSource)
```

---

## ⚙️ Funcionalidades de Negocio Implementadas

1. **`registrarProducto(Producto producto)`**: Registro validando código, nombre, precio, stock, duplicados y notificación.
2. **`validarNombre(String nombre)`**: Validación de no nulidad, no vacío y longitud mínima de 3 caracteres.
3. **`validarPrecio(double precio)`**: Validación de precio estrictamente positivo (> 0).
4. **`validarStock(int stock)`**: Validación de stock entero no negativo (>= 0).
5. **`calcularPrecioConDescuento(Producto p, double pct)`**: Cálculo de precio final con porcentaje de descuento (0% a 100%) con redondeo a 2 decimales.
6. **`hayStockDisponible(String codigo, int cantidad)`**: Comprobación de disponibilidad consultando el repositorio simulado.
7. **`buscarProductoPorCodigo(String codigo)`**: Búsqueda en repositorio con manejo de excepción `ProductoNoEncontradoException`.
8. **`actualizarStockPorVenta(String codigo, int cant)`**: Descuento de inventario y emisión de alerta por stock bajo (<= 5).

---

## 🧪 Ejecución de Pruebas y Cobertura

### 1. Ejecutar Pruebas y Generar Reporte JaCoCo:
```bash
mvn clean test jacoco:report
```

### 2. Validar Quality Gate de Cobertura (Mínimo 80%):
```bash
mvn jacoco:check
```

### 3. Resultados Obtenidos:
- **Pruebas ejecutadas:** 55 (0 fallos, 0 errores, 0 omitidos).
- **Cobertura de Líneas:** **100%** (93/93 líneas cubiertas).
- **Cobertura de Ramas:** **89%** (52/58 ramas cubiertas).
- **Cobertura de Instrucciones:** **100%** (402/402 instrucciones cubiertas).
- **Reporte HTML:** `target/site/jacoco/index.html`.

---

## 🔄 Pipeline de Jenkins (`Jenkinsfile`)

El pipeline consta de las siguientes etapas:
1. **Checkout SCM:** Clonado y verificación del repositorio Git.
2. **Compilación:** `mvn clean compile`.
3. **Pruebas Unitarias y Parametrizadas:** `mvn test`.
4. **Reporte y Validación de Cobertura:** `mvn jacoco:report jacoco:check`.
5. **Empaquetado:** `mvn package -DskipTests`.
6. **Post-Actions:** Publicación de reportes JUnit XML y visualización del reporte HTML de JaCoCo.
