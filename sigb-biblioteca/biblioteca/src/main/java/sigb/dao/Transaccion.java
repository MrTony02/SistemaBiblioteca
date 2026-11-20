package sigb.dao;

import java.util.function.Supplier;

/**
 * Ejecuta un conjunto de operaciones de DAO como una unidad atómica (ACID):
 * si algo falla se revierte todo, de modo que un ejemplar nunca quede "Prestado"
 * sin su registro de préstamo.
 */
public interface Transaccion {

    <T> T ejecutar(Supplier<T> trabajo);
}
