package test.classify;

import org.junit.Assert;
import org.junit.Test;

import classify.Label;
import classify.LabelPair;

public class TestLabelPair {

	@Test
	public void testConstructor() {
		@SuppressWarnings("unused")
		LabelPair p = new LabelPair(new Label("realTage"),new Label("predited tag"));
		//no expceiont for illegal args means success
	}
	
	@Test
	public void testConstructor_empty_realtag() {
		
		boolean error = false;
		try{
			@SuppressWarnings("unused")
			LabelPair p = new LabelPair(new Label(""),new Label("predited tag"));	
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build LabelPair with empty real tag label, but no exception was thrown.");
		}
		
		error = false;
		
		try{
			@SuppressWarnings("unused")
			LabelPair p = new LabelPair(new Label(null),new Label("predited tag"));	
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build LabelPair with empty real tag label, but no exception was thrown.");
		}
	}
	
	@Test
	public void testConstructor_null_realtag() {
		
		boolean error = false;
		try{
			@SuppressWarnings("unused")
			LabelPair p = new LabelPair(null,new Label("predited tag"));	
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build LabelPair with null real tag label, but no exception was thrown.");
		}
		
	}
	
	@Test
	public void testConstructor_empty_prediction() {
		
		boolean error = false;
		try{
			@SuppressWarnings("unused")
			LabelPair p = new LabelPair(new Label("real tag"),new Label(""));	
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build LabelPair with empty prediction label, but no exception was thrown.");
		}
		
		error = false;
		
		try{
			@SuppressWarnings("unused")
			LabelPair p = new LabelPair(new Label("predited tag"),new Label(null));	
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build LabelPair with empty prediction label, but no exception was thrown.");
		}
	}
	
	@Test
	public void testConstructor_null_pridiction() {
		
		boolean error = false;
		try{
			@SuppressWarnings("unused")
			LabelPair p = new LabelPair(new Label("real tag"),null);	
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to build LabelPair with null prediction label, but no exception was thrown.");
		}
		
	}
	
	@Test
	public void testGettersSetters() {
		
		Label rl = new Label("realTag");
		Label pl = new Label("prediction");
		
		LabelPair p = new LabelPair(rl,pl);
		Assert.assertEquals(true, rl.equals(p.getRealLabel()));
		Assert.assertEquals(true, pl.equals(p.getPredictedLabel()));
		
		Label rl2 = new Label("realTag2");
		Label pl2 = new Label("prediction2");
		
		p.setPredictedLabel(pl2);
		p.setRealLabel(rl2);
		
		Assert.assertEquals(true, rl2.equals(p.getRealLabel()));
		Assert.assertEquals(true, pl2.equals(p.getPredictedLabel()));
	}
	

	@Test
	public void testGettersSetters_empty_labels() {
		
		boolean error = false;
		try{
			LabelPair p = new LabelPair(new Label("real tag"),new Label("prediction"));	
			p.setPredictedLabel(new Label(null));
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to set the predicted label with empty prediction label, but no exception was thrown.");
		}
		
		error = false;
		try{
			LabelPair p = new LabelPair(new Label("real tag"),new Label("prediction"));	
			p.setPredictedLabel(new Label(""));
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to set the predicted label with empty prediction label, but no exception was thrown.");
		}
		
		error = false;
		try{
			LabelPair p = new LabelPair(new Label("real tag"),new Label("prediction"));	
			p.setRealLabel(new Label(""));
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to set the real tag label with empty real label, but no exception was thrown.");
		}
		
		error = false;
		try{
			LabelPair p = new LabelPair(new Label("real tag"),new Label("prediction"));	
			p.setRealLabel(new Label(null));
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to set the real tag label with empty real label, but no exception was thrown.");
		}
	}
	

	@Test
	public void testGettersSetters_null_labels() {
		
		boolean error = false;
		try{
			LabelPair p = new LabelPair(new Label("real tag"),new Label("prediction"));	
			p.setPredictedLabel(null);
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to set the predicted label with null prediction label, but no exception was thrown.");
		}
		
		error = false;
		try{
			LabelPair p = new LabelPair(new Label("real tag"),new Label("prediction"));	
			p.setRealLabel(null);
		}catch(IllegalArgumentException e){
			error =true;
		}
		
		if(!error){
			Assert.fail("expected to fail to set the real label with null real label, but no exception was thrown.");
		}
		
	}
}
