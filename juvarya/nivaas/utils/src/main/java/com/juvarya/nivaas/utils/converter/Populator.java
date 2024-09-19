package com.juvarya.nivaas.utils.converter;

public interface Populator<S , T> {
	void populate(S source, T target);
}
