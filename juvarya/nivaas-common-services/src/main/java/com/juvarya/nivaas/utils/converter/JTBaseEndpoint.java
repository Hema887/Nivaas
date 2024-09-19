package com.juvarya.nivaas.utils.converter;


public class JTBaseEndpoint <S, T>{
	private AbstractConverter<S, T> converter= new AbstractConverter<>();
	
	public AbstractConverter<S, T> getConverter(Populator<S, T> populator, String target) {
		converter.setPopulator(populator);
		converter.setTarget(target);
		return converter;
	}
}
