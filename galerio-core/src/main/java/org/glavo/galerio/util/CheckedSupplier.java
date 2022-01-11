package org.glavo.galerio.util;

@FunctionalInterface
public interface CheckedSupplier<T, Ex extends Throwable> {
    T get() throws Ex;
}
