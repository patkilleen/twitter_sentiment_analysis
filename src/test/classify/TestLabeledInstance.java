package test.classify;

import org.junit.Assert;
import org.junit.Test;

import classify.Label;
import classify.LabeledInstance;


public class TestLabeledInstance {

	@Test
	public void testGettersSetters_Label() {
		Object o = new Object();
		Label l = new Label("test");
		LabeledInstance i = new LabeledInstance(o,l);
		Assert.assertEquals(true,l== i.getLabel());
		Assert.assertEquals(true,o== i.getInstance());
		
		o = new Object();
		l.setValue("test2");
		i.setInstance(o);
		i.setLabel(l);
		
		Assert.assertEquals(true,l== i.getLabel());
		Assert.assertEquals(true,o== i.getInstance());
	}
	
	@Test
	public void testGettersSetter_null() {
		
		Object o = new Object();
		Label l = null;
		LabeledInstance i = new LabeledInstance(o,null);
		Assert.assertEquals(true,l== i.getLabel());
		Assert.assertEquals(true,o== i.getInstance());
		
		o = null;
		l = new Label("test");
		i.setInstance(o);
		i.setLabel(l);
		
		Assert.assertEquals(true,l== i.getLabel());
		Assert.assertEquals(true,o== i.getInstance());
		
		o = null;
		l = null;
		i.setInstance(o);
		i.setLabel(l);
		
		Assert.assertEquals(true,l== i.getLabel());
		Assert.assertEquals(true,o== i.getInstance());
	}

}
