package com.gluton.glutech.util;

/**
 * @author Gluton
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {

	R accept(T t, U u, V v);
}
