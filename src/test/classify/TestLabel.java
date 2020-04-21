package test.classify;

import org.junit.Assert;
import org.junit.Test;

import classify.Label;

public class TestLabel {

	@Test
	public void testSettersAndGetters() {
		Label l = new Label("test");
		Assert.assertSame("test", l.getValue());
		l.setValue("test2");
		Assert.assertEquals(true,"test2".equals(l.getValue()));
	}

	@Test
	public void testSettersAndGettersEmpty() {
		Label l = new Label("");
		Assert.assertEquals(true,"".equals(l.getValue()));
		l.setValue("");
		Assert.assertEquals(true,"".equals(l.getValue()));
	}
	
	@Test
	public void testSettersAndGettersNull() {
		Label l = new Label(null);
		Assert.assertSame(null, l.getValue());
		l.setValue(null);
		Assert.assertSame(null, l.getValue());
	}
	
	@Test
	public void testEquals() {
		Label l = new Label(null);
		Assert.assertEquals(true, l.equals(new Label(null)));
		l = new Label("");
		Assert.assertEquals(true, l.equals(new Label("")));
		l = new Label("test");
		Assert.assertEquals(true, l.equals(new Label("test")));
		l = new Label("test2");
		Assert.assertEquals(false, l.equals(new Label("test")));
		l = new Label("test");
		Assert.assertEquals(true, l.equals(l));
	}
	
	@Test
	public void testisEmpty() {
		Label l = new Label(null);
		Assert.assertEquals(true, l.isEmpty());
		l = new Label("");
		Assert.assertEquals(true, l.isEmpty());
		l = new Label("not empty");
		Assert.assertEquals(false, l.isEmpty());
		l = new Label("!");
		Assert.assertEquals(false, l.isEmpty());
	}
}
