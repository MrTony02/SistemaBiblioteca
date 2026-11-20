# SIGB – Sistema de Información de Gestión Bibliotecaria

Proyecto Integrador POO – Grupo 3. Aplicación de escritorio en **Java Swing** con persistencia en base de
datos relacional vía **JDBC**, organizada en tres capas (RNF-01, RNF-02, RNF-03).

## Estructura

```
src/main/java/sigb
├── modelo/     Entidad (abstracta), Usuario, Material, Ejemplar, Prestamo, Multa, EstadoEjemplar
├── dao/        Interfaces DAO + Transaccion + PersistenciaException
│   └── jdbc/   Implementaciones JDBC (ConexionBD, *DAOJdbc, JdbcTransaccion)
├── servicio/   PrestamoService, DevolucionService, CatalogoService, MultaService (reglas de negocio)
├── vista/      MenuPrincipal y diálogos Swing (préstamo, devolución, catálogo, multas)
├── config/     Configuracion (db.properties) y Servicios (ensamblado de capas)
└── Main.java
sql/schema_mysql.sql   Tablas, restricciones y datos de ejemplo
src/test/java          Pruebas de reglas de negocio con DAO en memoria (sin BD)
```

Flujo (igual al diagrama de secuencia): Formulario Swing → Servicio (valida reglas) → DAO → JDBC → BD.

## Requisitos

- JDK 17 o superior
- MySQL 8+ (o PostgreSQL / SQL Server, ver abajo)
- Maven 3.8+ (o agregar el `.jar` del driver al classpath a mano)

## Puesta en marcha

1. Crear la base de datos y cargar datos de ejemplo:
   ```
   mysql -u root -p < sql/schema_mysql.sql
   ```
2. Ajustar usuario y contraseña en `src/main/resources/db.properties`
   (o crear un `db.properties` junto al programa, que tiene prioridad).
3. Compilar y ejecutar:
   ```
   mvn package
   java -jar target/sigb.jar
   ```

## Datos de ejemplo para probar

| Qué probar | Cómo |
|---|---|
| Préstamo exitoso | Documento `1020304050`, ejemplar `EJ-1001` |
| RN-01 (prestado) | Ejemplar `EJ-1003` |
| RN-01 (extraviado) | Ejemplar `EJ-1094` |
| Multa (RF-05) | Registrar devolución de `EJ-1003` (vencido hace 3 días → multa de $3.000) |
| RN-03 | Después de la multa, intentar un préstamo a `1098765432` (Carlos Ruiz) |
| Pagar multa | Gestión de Multas → documento `1098765432` → seleccionar → Registrar pago |

## Reglas de negocio y dónde viven

| Regla | Dónde |
|---|---|
| RF-04 / RN-02 fecha límite automática y obligatoria | `Prestamo.registrarPrestamo(...)` y su constructor |
| RN-01 no prestar "Prestado"/"Extraviado" | `PrestamoService.registrarPrestamo` |
| RN-03 sin multas pendientes | `PrestamoService` + `MultaDAO.tieneMultasPendientes` |
| RF-05 multa por retraso | `DevolucionService` + `Multa.calcularMulta` |
| Atomicidad (ACID) | `JdbcTransaccion`: insertar préstamo y marcar ejemplar "Prestado" ocurren juntos o no ocurren |

Plazo y tarifa se configuran en `db.properties` (`prestamo.dias=7`, `multa.valor_por_dia=1000`).

## Ajustes respecto al documento de la Fase 1

Para que el sistema funcione fue necesario completar el modelo:

- `PRESTAMO.fecha_devolucion` (nullable): sin ella no se sabe qué préstamos siguen activos ni si hubo retraso.
- `MULTA.pagada`: necesaria para RN-03 (multas "activas sin pagar").
- `EJEMPLAR.codigo` (único, p. ej. `EJ-1094`): el prototipo 2 pide un código, no un número.
- `MATERIAL.autor`: dato básico del catálogo.
- El catálogo muestra el estado por material ("Disponible"/"No disponible" y "n de m" ejemplares), porque
  la disponibilidad real está en cada ejemplar.

## Otros motores de base de datos

Cambiar la dependencia en `pom.xml` y `db.url` en `db.properties`:

- PostgreSQL: `org.postgresql:postgresql`, `jdbc:postgresql://localhost:5432/biblioteca`.
  El script SQL requiere ajustes menores (`SERIAL` en lugar de `AUTO_INCREMENT`, quitar `ENGINE`, `DATE_SUB`).
- SQL Server: `com.microsoft.sqlserver:mssql-jdbc`. Además de `IDENTITY`, la consulta
  `SELECT ... FOR UPDATE` de `EjemplarDAOJdbc` debe cambiarse por `WITH (UPDLOCK)` y `LIMIT 1` en
  `PrestamoDAOJdbc` por `TOP 1`.

## Pruebas de reglas de negocio (sin base de datos)

```
mkdir out && javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out sigb.PruebasReglas
```
