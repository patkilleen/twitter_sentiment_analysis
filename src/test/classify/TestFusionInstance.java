package test.classify;

import org.junit.Assert;
import org.junit.Test;

import classify.FusionInstance;
import classify.Label;


public class TestFusionInstance {

	@Test
	public void testConstructor() {
		Label l = new Label("test");
		@SuppressWarnings("unused")
		FusionInstance i = new FusionInstance(l);
		//no exception throw means success 
	}
	
	@Test
	public void testConstructor_empty_label() {
		boolean error = false;
		try{
			Label l = new Label("");
			@SuppressWarnings("unused")
			FusionInstance i = new FusionInstance(l);
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build FusionInstance with empty label, but no exception was thrown.");
		}
		
		error = false;
		try{
			Label l = new Label(null);
			@SuppressWarnings("unused")
			FusionInstance i = new FusionInstance(l);
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build FusionInstance with empty label, but no exception was thrown.");
		}
		 
	}
	
	@Test
	public void testConstructor_null_label() {
		boolean error = false;
		try{
			@SuppressWarnings("unused")
			FusionInstance i = new FusionInstance(null);
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build FusionInstance with empty label, but no exception was thrown.");
		}
		 
	}
	
	@Test
	public void testAddPrediction() {
		
		FusionInstance i = new FusionInstance(new Label("test"));
		Label p = new Label("p1");
		i.addPrediction(p);
		p = new Label("p2");
		i.addPrediction(p);
		p = new Label("p2");//add same valued-label
		i.addPrediction(p);
		i.addPrediction(p);//add same prediction label instance twice
		 
	}
	

	@Test
	public void testAddPrediction_empty_prediction() {
		
		FusionInstance i = new FusionInstance(new Label("test"));
		Label p = new Label("");
		
		
		boolean error = false;
		try{
			i.addPrediction(p);	
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build FusionInstance with empty label, but no exception was thrown.");
		}
		
		//now try breaking addPrediction with empty but after we add a legal prediction
		i = new FusionInstance(new Label("test"));
		i.addPrediction(new Label("not empty"));
		error = false;
		try{
			
			i.addPrediction(new Label(""));
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build FusionInstance with empty label, but no exception was thrown.");
		}
		
		//now try breaking addPrediction with empty but after we add a legal prediction
		i = new FusionInstance(new Label("test"));
		
		error = false;
		try{
			
			i.addPrediction(new Label(null));
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build FusionInstance with empty label, but no exception was thrown.");
		}
		
		i = new FusionInstance(new Label("test"));
		
		error = false;
		try{
			
			i.addPrediction(new Label("not empty"));
			i.addPrediction(new Label(null));
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build FusionInstance with empty label, but no exception was thrown.");
		}
	}

	@Test
	public void testAddPrediction_null_prediction() {
		
		FusionInstance i = new FusionInstance(new Label("test"));
		
		boolean error = false;
		try{
			i.addPrediction(null);	
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build FusionInstance with empty label, but no exception was thrown.");
		}
		
		//now try breaking addPrediction with empty but after we add a legal prediction
		i = new FusionInstance(new Label("test"));
		i.addPrediction(new Label("not empty"));
		error = false;
		try{
			
			i.addPrediction(null);
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build FusionInstance with empty label, but no exception was thrown.");
		}
	}
	
	@Test
	public void testToString() {
		
		FusionInstance i = new FusionInstance(new Label("test"));
		String actual = i.toString();
		Assert.assertEquals(true, "test".equals(actual));
		
		i.addPrediction(new Label("test2"));
		actual = i.toString();
		Assert.assertEquals(true, "test2,test".equals(actual));
		
		i.addPrediction(new Label("test3"));
		actual = i.toString();
		Assert.assertEquals(true, "test2,test3,test".equals(actual));
		
		
		i.addPrediction(new Label("test3"));
		actual = i.toString();
		Assert.assertEquals(true, "test2,test3,test3,test".equals(actual));
		
	}
}
