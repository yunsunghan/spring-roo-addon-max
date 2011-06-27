package com.ks.spring.roo.addon.maxweb.metadata;

import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.annotations.populator.AbstractAnnotationValues;
import org.springframework.roo.classpath.details.annotations.populator.AutoPopulate;
import org.springframework.roo.classpath.details.annotations.populator.AutoPopulationUtils;
import org.springframework.roo.model.JavaType;


public class MaxWebAnnotationValues extends AbstractAnnotationValues  {
	
	@AutoPopulate JavaType targetObject = null;
	
	public MaxWebAnnotationValues(PhysicalTypeMetadata governorPhysicalTypeMetadata) {
		super(governorPhysicalTypeMetadata, new JavaType(RooMaxWeb.class.getName()));
		AutoPopulationUtils.populate(this, annotationMetadata);
	}

	public JavaType getTargetObject() {
		return targetObject;
	}

}
