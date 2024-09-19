package com.juvarya.nivaas.utils.converter;

import java.util.ArrayList;
import java.util.List;


public class AbstractConverter<S , T> {
	private Populator<S, T> populator;
	private String target;
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public T convert(S source) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if(null == populator) {
			throw new ClassNotFoundException("Populator must set before converting");
		}
		if(null == target) {
			throw new ClassNotFoundException("Target must set before converting");
		}
		T object = (T) Class.forName(target).newInstance();
		if(null != source) {
			populator.populate(source, object);
			return object;
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<T> convertAll(List<S> sourceList) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		if(null == populator) {
			throw new ClassNotFoundException("Populator must set before converting");
		}
		if(null == target) {
			throw new ClassNotFoundException("Target must set before converting");
		}
		List<T> targetList= new ArrayList<>();
		if(null == sourceList) {
			return targetList;
		}
		for(S source: sourceList) {
			T object = (T) Class.forName(target).newInstance();
			populator.populate(source, object);
			targetList.add(object);
		}
		return targetList;
	}

	public Populator<S, T> getPopulator() {
		return populator;
	}

	public void setPopulator(Populator<S, T> populator) {
		this.populator = populator;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
