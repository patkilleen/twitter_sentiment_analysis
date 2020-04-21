package test.classify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import classify.ConfusionMatrix;
import classify.Label;

public class TestConfusionMatrix extends ConfusionMatrix{


	private static final double ERROR_DELTA = 0.001;

	public TestConfusionMatrix(){

	}

	@Test
	public void testConstructor() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));
		labels.add(new Label("test4"));

		//no exception means sucess
		@SuppressWarnings("unused")
		ConfusionMatrix m = new ConfusionMatrix(labels);

		labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));


		//no exception means sucess	
		m = new ConfusionMatrix(labels);
	}

	@Test
	public void testConstructor_label_uniqueness_violation() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test3"));
		labels.add(new Label("test3"));
		labels.add(new Label("test4"));

		boolean error = false;

		try{

			@SuppressWarnings("unused")
			ConfusionMatrix m = new ConfusionMatrix(labels);
		}catch(IllegalArgumentException e){
			error = true;
		}

		if(!error){
			Assert.fail("constructor should have thrown an exception, but it didn't.");
		}
	}

	@Test
	public void testConstructor_null_labels_violation() {

		boolean error = false;

		try{
			@SuppressWarnings("unused")
			ConfusionMatrix m = new ConfusionMatrix(null);
		}catch(IllegalArgumentException e){
			error = true;
		}

		if(!error){
			Assert.fail("constructor should have thrown an exception, but it didn't.");
		}
	}

	@Test
	public void testConstructor_empty_labels_violation() {
		List<Label> labels = new ArrayList<Label>(4);

		boolean error = false;

		try{
			@SuppressWarnings("unused")
			ConfusionMatrix m = new ConfusionMatrix(labels);
		}catch(IllegalArgumentException e){
			error = true;
		}

		if(!error){
			Assert.fail("constructor should have thrown an exception, but it didn't.");
		}
	}


	@Test
	public void testSize() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));
		labels.add(new Label("test4"));

		ConfusionMatrix m = new ConfusionMatrix(labels);
		Assert.assertEquals(4, m.size());
	}


	@Test
	public void test_getFrequency_empty_matrix() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));
		labels.add(new Label("test4"));

		ConfusionMatrix m = new ConfusionMatrix(labels);

		//don't add any predictions, matrix should be 0 in every cell
		for(Label row : labels){
			for(Label col : labels){
				Assert.assertEquals(new Integer(0), m.getFrequency(row,col));
			}	
		}
	}

	@Test
	public void test_getFrequency_label_not_found_violation() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));
		labels.add(new Label("test4"));

		ConfusionMatrix m = new ConfusionMatrix(labels);

		boolean error = false;

		try{
			@SuppressWarnings("unused")
			int f = m.getFrequency(new Label("test4"), new Label("test5"));
		}catch(IllegalArgumentException e){
			error = true;
		}

		if(!error){
			Assert.fail("constructor should have thrown an exception, but it didn't.");
		}

		error = false;

		try{
			@SuppressWarnings("unused")
			int f = m.getFrequency(new Label("test4"), new Label(""));
		}catch(IllegalArgumentException e){
			error = true;
		}

		if(!error){
			Assert.fail("constructor should have thrown an exception, but it didn't.");
		}

		error = false;

		try{
			@SuppressWarnings("unused")
			int f = m.getFrequency(new Label("test10"), new Label("test5"));
		}catch(IllegalArgumentException e){
			error = true;
		}

		if(!error){
			Assert.fail("constructor should have thrown an exception, but it didn't.");
		}

		error = false;

		try{
			@SuppressWarnings("unused")
			int f = m.getFrequency(new Label("test10"), new Label("test4"));
		}catch(IllegalArgumentException e){
			error = true;
		}

		if(!error){
			Assert.fail("constructor should have thrown an exception, but it didn't.");
		}
	}

	@Test
	public void test_getFrequency_empty_label_violation() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));
		labels.add(new Label("test4"));

		ConfusionMatrix m = new ConfusionMatrix(labels);

		boolean error = false;

		try{
			@SuppressWarnings("unused")
			int f = m.getFrequency(new Label(""), new Label(null));
		}catch(IllegalArgumentException e){
			error = true;
		}

		if(!error){
			Assert.fail("getFrequency should have thrown an exception, but it didn't.");
		}

		error = false;

		try{
			@SuppressWarnings("unused")
			int f = m.getFrequency(new Label(""), new Label("test4"));
		}catch(IllegalArgumentException e){
			error = true;
		}

		if(!error){
			Assert.fail("getFrequency should have thrown an exception, but it didn't.");
		}

		error = false;

		try{
			@SuppressWarnings("unused")
			int f = m.getFrequency(new Label(null), new Label("test4"));
		}catch(IllegalArgumentException e){
			error = true;
		}

		if(!error){
			Assert.fail("getFrequency should have thrown an exception, but it didn't.");
		}

		error = false;

		try{
			@SuppressWarnings("unused")
			int f = m.getFrequency(new Label("test4"), new Label(""));
		}catch(IllegalArgumentException e){
			error = true;
		}

		if(!error){
			Assert.fail("getFrequency should have thrown an exception, but it didn't.");
		}

		error = false;

		try{
			@SuppressWarnings("unused")
			int f = m.getFrequency(new Label("test3"), new Label(""));
		}catch(IllegalArgumentException e){
			error = true;
		}

		if(!error){
			Assert.fail("getFrequency should have thrown an exception, but it didn't.");
		}
	}

	@Test
	public void test_getFrequency_null_label_violation() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));
		labels.add(new Label("test4"));

		ConfusionMatrix m = new ConfusionMatrix(labels);

		boolean error = false;

		try{
			@SuppressWarnings("unused")
			int f = m.getFrequency(null, new Label("test4"));
		}catch(NullPointerException e){
			error = true;
		}

		if(!error){
			Assert.fail("getFrequency should have thrown an exception, but it didn't.");
		}

		error = false;

		try{
			@SuppressWarnings("unused")
			int f = m.getFrequency(new Label("test4"),null);
		}catch(NullPointerException e){
			error = true;
		}

		if(!error){
			Assert.fail("getFrequency should have thrown an exception, but it didn't.");
		}

		error = false;

		try{
			@SuppressWarnings("unused")
			int f = m.getFrequency(null, null);
		}catch(NullPointerException e){
			error = true;
		}

		if(!error){
			Assert.fail("getFrequency should have thrown an exception, but it didn't.");
		}

	}


	@Test
	public void test_sumAll() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));
		labels.add(new Label("test4"));

		TestConfusionMatrix m = new TestConfusionMatrix();
		m.init(labels);

		//should have nothing inside matrix
		Assert.assertEquals(0, m.sumAll());

		m.incrementCell(new Label("test1"), new Label("test1"));

		Assert.assertEquals(1, m.sumAll());

		m.incrementCell(new Label("test1"), new Label("test2"));

		Assert.assertEquals(2, m.sumAll());

		m.incrementCell(new Label("test1"), new Label("test2"));

		Assert.assertEquals(3, m.sumAll());

		m.incrementCell(new Label("test3"), new Label("test1"));

		Assert.assertEquals(4, m.sumAll());

		m = new TestConfusionMatrix();
		m.init(labels);
		//make many predictions
		for(int i = 0;i<1000;i++){
			String v1="test"+ ((i%4)+1);
			String v2="test"+ (((i+1)%4)+1);
			m.incrementCell(new Label(v1), new Label(v2));
		}
		Assert.assertEquals(1000, m.sumAll());
	}

	@Test
	public void test_sumColumn() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));

		TestConfusionMatrix m = new TestConfusionMatrix();
		m.init(labels);

		/*
		 * 0		0		0
		 * 0		0		0
		 * 0		0		0
		 * 
		 */
		Assert.assertEquals(0, m.sumColumn(new Label("test1")));
		Assert.assertEquals(0, m.sumColumn(new Label("test2")));
		Assert.assertEquals(0, m.sumColumn(new Label("test3")));

		m.incrementCell(new Label("test1"), new Label("test1"));
		/*
		 * 1		0		0
		 * 0		0		0
		 * 0		0		0
		 * 
		 */
		Assert.assertEquals(1, m.sumColumn(new Label("test1")));
		Assert.assertEquals(0, m.sumColumn(new Label("test2")));
		Assert.assertEquals(0, m.sumColumn(new Label("test3")));

		m.incrementCell(new Label("test1"), new Label("test1"));
		/*
		 * 2		0		0
		 * 0		0		0
		 * 0		0		0
		 * 
		 */
		Assert.assertEquals(2, m.sumColumn(new Label("test1")));
		Assert.assertEquals(0, m.sumColumn(new Label("test2")));
		Assert.assertEquals(0, m.sumColumn(new Label("test3")));

		m.incrementCell(new Label("test2"), new Label("test1"));

		/*
		 * 2		0		0
		 * 1		0		0
		 * 0		0		0
		 * 
		 */
		Assert.assertEquals(3, m.sumColumn(new Label("test1")));
		Assert.assertEquals(0, m.sumColumn(new Label("test2")));
		Assert.assertEquals(0, m.sumColumn(new Label("test3")));

		m.incrementCell(new Label("test2"), new Label("test3"));
		m.incrementCell(new Label("test3"), new Label("test2"));

		/*
		 * 2		0		0
		 * 1		0		1
		 * 0		1		0
		 * 
		 */
		Assert.assertEquals(3, m.sumColumn(new Label("test1")));
		Assert.assertEquals(1, m.sumColumn(new Label("test2")));
		Assert.assertEquals(1, m.sumColumn(new Label("test3")));

		m.incrementCell(new Label("test3"), new Label("test1"));
		m.incrementCell(new Label("test3"), new Label("test1"));
		m.incrementCell(new Label("test3"), new Label("test1"));
		m.incrementCell(new Label("test3"), new Label("test1"));

		m.incrementCell(new Label("test1"), new Label("test2"));
		m.incrementCell(new Label("test1"), new Label("test2"));

		m.incrementCell(new Label("test1"), new Label("test3"));
		m.incrementCell(new Label("test1"), new Label("test3"));
		m.incrementCell(new Label("test1"), new Label("test3"));

		/*
		 * 2		2		3
		 * 1		0		1
		 * 4		1		0
		 * 
		 */
		Assert.assertEquals(7, m.sumColumn(new Label("test1")));
		Assert.assertEquals(3, m.sumColumn(new Label("test2")));
		Assert.assertEquals(4, m.sumColumn(new Label("test3")));
	}

	@Test
	public void test_sumColumn_invalid_args() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));

		TestConfusionMatrix m = new TestConfusionMatrix();
		m.init(labels);

		boolean error = false;

		try{
			m.sumColumn(null);
		}catch(NullPointerException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}

		error = false;

		try{
			m.sumColumn(new Label(""));
		}catch(IllegalArgumentException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}

		error = false;

		try{
			m.sumColumn(new Label(null));
		}catch(IllegalArgumentException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}

		error = false;

		try{
			m.sumColumn(new Label("test65"));
		}catch(IllegalArgumentException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}
	}

	@Test
	public void test_sumRow() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));

		TestConfusionMatrix m = new TestConfusionMatrix();
		m.init(labels);

		/*
		 * 0		0		0
		 * 0		0		0
		 * 0		0		0
		 * 
		 */
		Assert.assertEquals(0, m.sumRow(new Label("test1")));
		Assert.assertEquals(0, m.sumRow(new Label("test2")));
		Assert.assertEquals(0, m.sumRow(new Label("test3")));

		m.incrementCell(new Label("test1"), new Label("test1"));
		/*
		 * 1		0		0
		 * 0		0		0
		 * 0		0		0
		 * 
		 */
		Assert.assertEquals(1, m.sumRow(new Label("test1")));
		Assert.assertEquals(0, m.sumRow(new Label("test2")));
		Assert.assertEquals(0, m.sumRow(new Label("test3")));

		m.incrementCell(new Label("test1"), new Label("test1"));
		/*
		 * 2		0		0
		 * 0		0		0
		 * 0		0		0
		 * 
		 */
		Assert.assertEquals(2, m.sumRow(new Label("test1")));
		Assert.assertEquals(0, m.sumRow(new Label("test2")));
		Assert.assertEquals(0, m.sumRow(new Label("test3")));

		m.incrementCell(new Label("test2"), new Label("test1"));

		/*
		 * 2		0		0
		 * 1		0		0
		 * 0		0		0
		 * 
		 */
		Assert.assertEquals(2, m.sumRow(new Label("test1")));
		Assert.assertEquals(1, m.sumRow(new Label("test2")));
		Assert.assertEquals(0, m.sumRow(new Label("test3")));

		m.incrementCell(new Label("test2"), new Label("test3"));
		m.incrementCell(new Label("test3"), new Label("test2"));

		/*
		 * 2		0		0
		 * 1		0		1
		 * 0		1		0
		 * 
		 */
		Assert.assertEquals(2, m.sumRow(new Label("test1")));
		Assert.assertEquals(2, m.sumRow(new Label("test2")));
		Assert.assertEquals(1, m.sumRow(new Label("test3")));

		m.incrementCell(new Label("test3"), new Label("test1"));
		m.incrementCell(new Label("test3"), new Label("test1"));
		m.incrementCell(new Label("test3"), new Label("test1"));
		m.incrementCell(new Label("test3"), new Label("test1"));

		m.incrementCell(new Label("test1"), new Label("test2"));
		m.incrementCell(new Label("test1"), new Label("test2"));

		m.incrementCell(new Label("test1"), new Label("test3"));
		m.incrementCell(new Label("test1"), new Label("test3"));
		m.incrementCell(new Label("test1"), new Label("test3"));

		/*
		 * 2		2		3
		 * 1		0		1
		 * 4		1		0
		 * 
		 */
		Assert.assertEquals(7, m.sumRow(new Label("test1")));
		Assert.assertEquals(2, m.sumRow(new Label("test2")));
		Assert.assertEquals(5, m.sumRow(new Label("test3")));
	}

	@Test
	public void test_sumRow_invalid_args() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));

		TestConfusionMatrix m = new TestConfusionMatrix();
		m.init(labels);

		boolean error = false;

		try{
			m.sumRow(null);
		}catch(NullPointerException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}

		error = false;

		try{
			m.sumRow(new Label(""));
		}catch(IllegalArgumentException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}

		error = false;

		try{
			m.sumRow(new Label(null));
		}catch(IllegalArgumentException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}

		error = false;

		try{
			m.sumRow(new Label("test65"));
		}catch(IllegalArgumentException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}
	}


	@Test
	public void test_incrementCell_invalid_arg() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));

		TestConfusionMatrix m = new TestConfusionMatrix();
		m.init(labels);

		boolean error = false;

		try{
			m.incrementCell(null, null);
		}catch(NullPointerException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}


		error = false;

		try{
			m.incrementCell(new Label("test1"), null);
		}catch(NullPointerException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}

		error = false;

		try{
			m.incrementCell(null,new Label("test1"));
		}catch(NullPointerException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}

		error = false;

		try{
			m.incrementCell(new Label(""),new Label("test1"));
		}catch(IllegalArgumentException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}
		error = false;

		try{
			m.incrementCell(new Label(""),new Label(""));
		}catch(IllegalArgumentException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}

		error = false;

		try{
			m.incrementCell(new Label("test1"),new Label(""));
		}catch(IllegalArgumentException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}

		error = false;

		try{
			m.incrementCell(new Label("test65"),new Label("test1"));
		}catch(IllegalArgumentException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}

		error = false;

		try{
			m.incrementCell(new Label("test1"),new Label("test65"));
		}catch(IllegalArgumentException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}

		error = false;

		try{
			m.incrementCell(new Label("test65"),new Label("test65"));
		}catch(IllegalArgumentException e){
			error=true;
		}

		if(!error){
			Assert.fail("expected an exception, but there was none.");
		}
	}

	@Test
	public void test_computeTP(){

		TestConfusionMatrix m = buildAndPopulateTestMatrix();
		Assert.assertEquals(4.0, m.computeTP(new Label("test1")), ERROR_DELTA);
		Assert.assertEquals(2.0, m.computeTP(new Label("test2")), ERROR_DELTA);
		Assert.assertEquals(6.0, m.computeTP(new Label("test3")), ERROR_DELTA);

	}

	@Test
	public void test_computeFP(){

		TestConfusionMatrix m = buildAndPopulateTestMatrix();
		Assert.assertEquals(9.0, m.computeFP(new Label("test1")), ERROR_DELTA);
		Assert.assertEquals(1.0, m.computeFP(new Label("test2")), ERROR_DELTA);
		Assert.assertEquals(3.0, m.computeFP(new Label("test3")), ERROR_DELTA);

	}

	@Test
	public void test_computeFN(){

		TestConfusionMatrix m = buildAndPopulateTestMatrix();
		Assert.assertEquals(2.0, m.computeFN(new Label("test1")), ERROR_DELTA);
		Assert.assertEquals(8.0, m.computeFN(new Label("test2")), ERROR_DELTA);
		Assert.assertEquals(3.0, m.computeFN(new Label("test3")), ERROR_DELTA);

	}

	@Test
	public void test_computeTN(){

		TestConfusionMatrix m = buildAndPopulateTestMatrix();
		Assert.assertEquals(10.0, m.computeTN(new Label("test1")), ERROR_DELTA);
		Assert.assertEquals(14.0, m.computeTN(new Label("test2")), ERROR_DELTA);
		Assert.assertEquals(13.0, m.computeTN(new Label("test3")), ERROR_DELTA);

	}

	@Test
	public void test_computePrecision(){

		TestConfusionMatrix m = buildAndPopulateTestMatrix();
		Assert.assertEquals(4.0/13.0, m.computePrecision(new Label("test1")), ERROR_DELTA);
		Assert.assertEquals(2.0/3.0, m.computePrecision(new Label("test2")), ERROR_DELTA);
		Assert.assertEquals(6.0/9.0, m.computePrecision(new Label("test3")), ERROR_DELTA);

	}

	@Test
	public void test_computeRecall(){

		TestConfusionMatrix m = buildAndPopulateTestMatrix();
		Assert.assertEquals(4.0/6.0, m.computeRecall(new Label("test1")), ERROR_DELTA);
		Assert.assertEquals(2.0/10.0, m.computeRecall(new Label("test2")), ERROR_DELTA);
		Assert.assertEquals(6.0/9.0, m.computeRecall(new Label("test3")), ERROR_DELTA);

	}

	@Test
	public void test_computeF1Score(){

		TestConfusionMatrix m = buildAndPopulateTestMatrix();
		Assert.assertEquals(0.421, m.computeF1Score(new Label("test1")), ERROR_DELTA);
		Assert.assertEquals(0.308, m.computeF1Score(new Label("test2")), ERROR_DELTA);
		Assert.assertEquals(0.667, m.computeF1Score(new Label("test3")), ERROR_DELTA);

	}

	@Test
	public void test_computeMacroF1Score(){

		TestConfusionMatrix m = buildAndPopulateTestMatrix();
		Assert.assertEquals(0.465, m.computeMacroF1(), ERROR_DELTA);


	}

	@Test
	public void test_computeMacroRecall(){

		TestConfusionMatrix m = buildAndPopulateTestMatrix();
		Assert.assertEquals(0.511, m.computeMacroRecall(), ERROR_DELTA);


	}


	@Test
	public void test_computeMacroPrecision(){

		TestConfusionMatrix m = buildAndPopulateTestMatrix();
		Assert.assertEquals(0.547, m.computeMacroPrecision(), ERROR_DELTA);


	}

	@Test
	public void test_computeAccuracy(){

		TestConfusionMatrix m = buildAndPopulateTestMatrix();
		Assert.assertEquals(0.480, m.computeAccuracy(), ERROR_DELTA);
	}

	public TestConfusionMatrix buildAndPopulateTestMatrix(){
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));

		TestConfusionMatrix m = new TestConfusionMatrix();
		m.init(labels);

		m.incrementCell(new Label("test1"),new Label("test1"));
		m.incrementCell(new Label("test1"),new Label("test1"));
		m.incrementCell(new Label("test1"),new Label("test1"));
		m.incrementCell(new Label("test1"),new Label("test1"));

		m.incrementCell(new Label("test1"),new Label("test2"));
		m.incrementCell(new Label("test1"),new Label("test2"));
		m.incrementCell(new Label("test1"),new Label("test2"));
		m.incrementCell(new Label("test1"),new Label("test2"));
		m.incrementCell(new Label("test1"),new Label("test2"));
		m.incrementCell(new Label("test1"),new Label("test2"));

		m.incrementCell(new Label("test1"),new Label("test3"));
		m.incrementCell(new Label("test1"),new Label("test3"));
		m.incrementCell(new Label("test1"),new Label("test3"));

		m.incrementCell(new Label("test2"),new Label("test1"));

		m.incrementCell(new Label("test2"),new Label("test2"));
		m.incrementCell(new Label("test2"),new Label("test2"));

		m.incrementCell(new Label("test3"),new Label("test1"));

		m.incrementCell(new Label("test3"),new Label("test2"));
		m.incrementCell(new Label("test3"),new Label("test2"));

		m.incrementCell(new Label("test3"),new Label("test3"));
		m.incrementCell(new Label("test3"),new Label("test3"));
		m.incrementCell(new Label("test3"),new Label("test3"));
		m.incrementCell(new Label("test3"),new Label("test3"));
		m.incrementCell(new Label("test3"),new Label("test3"));
		m.incrementCell(new Label("test3"),new Label("test3"));
		/*
		 * 4		6		3
		 * 1		2		1
		 * 1		2		6
		 * 
		 */
		return m;
	}

	@Test
	public void test_add() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));

		TestConfusionMatrix m = new TestConfusionMatrix();
		m.init(labels);


		m.incrementCell(new Label("test1"), new Label("test1"));


		m.incrementCell(new Label("test1"), new Label("test1"));

		m.incrementCell(new Label("test2"), new Label("test1"));


		m.incrementCell(new Label("test2"), new Label("test3"));
		m.incrementCell(new Label("test3"), new Label("test2"));
		;

		m.incrementCell(new Label("test3"), new Label("test1"));
		m.incrementCell(new Label("test3"), new Label("test1"));
		m.incrementCell(new Label("test3"), new Label("test1"));
		m.incrementCell(new Label("test3"), new Label("test1"));

		m.incrementCell(new Label("test1"), new Label("test2"));
		m.incrementCell(new Label("test1"), new Label("test2"));

		m.incrementCell(new Label("test1"), new Label("test3"));
		m.incrementCell(new Label("test1"), new Label("test3"));
		m.incrementCell(new Label("test1"), new Label("test3"));

		/*
		 * 2		2		3
		 * 1		0		1
		 * 4		1		0
		 * 
		 */



		TestConfusionMatrix other = new TestConfusionMatrix();
		other.init(labels);


		other.incrementCell(new Label("test1"), new Label("test1"));


		other.incrementCell(new Label("test1"), new Label("test1"));

		other.incrementCell(new Label("test2"), new Label("test1"));


		other.incrementCell(new Label("test2"), new Label("test3"));
		other.incrementCell(new Label("test3"), new Label("test2"));


		other.incrementCell(new Label("test3"), new Label("test1"));
		other.incrementCell(new Label("test3"), new Label("test1"));
		other.incrementCell(new Label("test3"), new Label("test1"));
		other.incrementCell(new Label("test3"), new Label("test1"));

		other.incrementCell(new Label("test1"), new Label("test2"));
		other.incrementCell(new Label("test1"), new Label("test2"));

		other.incrementCell(new Label("test1"), new Label("test3"));
		other.incrementCell(new Label("test1"), new Label("test3"));
		other.incrementCell(new Label("test1"), new Label("test3"));

		other.incrementCell(new Label("test1"), new Label("test1"));


		other.incrementCell(new Label("test1"), new Label("test1"));

		other.incrementCell(new Label("test2"), new Label("test1"));


		other.incrementCell(new Label("test2"), new Label("test3"));
		other.incrementCell(new Label("test3"), new Label("test2"));


		other.incrementCell(new Label("test3"), new Label("test1"));
		other.incrementCell(new Label("test3"), new Label("test1"));
		other.incrementCell(new Label("test3"), new Label("test1"));
		other.incrementCell(new Label("test3"), new Label("test1"));

		other.incrementCell(new Label("test1"), new Label("test2"));
		other.incrementCell(new Label("test1"), new Label("test2"));

		other.incrementCell(new Label("test1"), new Label("test3"));
		other.incrementCell(new Label("test1"), new Label("test3"));
		other.incrementCell(new Label("test1"), new Label("test3"));


		/*other
		 * 4		4		6
		 * 2		0		2
		 * 8		2		0
		 * 
		 */

		//ssum
		/*other			
		 * 4		4		6
		 * 2		0		2
		 * 8		2		0
		 * 
		 * 
		 * 
		 * +
		 * 
		 * 
		 * 2		2		3
		 * 1		0		1
		 * 4		1		0
		 *
		 * 
		 * =
		 * 
		 * 6		6		9
		 * 3		0		3
		 * 12		3		0
		 * 
		 */


		m.add(other);


		//row 1
		Assert.assertEquals(new Integer(6),m.getFrequency(new Label("test1"), new Label("test1")));
		Assert.assertEquals(new Integer(6),m.getFrequency(new Label("test1"), new Label("test2")));
		Assert.assertEquals(new Integer(9),m.getFrequency(new Label("test1"), new Label("test3")));

		//row 2
		Assert.assertEquals(new Integer(3),m.getFrequency(new Label("test2"), new Label("test1")));
		Assert.assertEquals(new Integer(0),m.getFrequency(new Label("test2"), new Label("test2")));
		Assert.assertEquals(new Integer(3),m.getFrequency(new Label("test2"), new Label("test3")));

		//row 3
		Assert.assertEquals(new Integer(12),m.getFrequency(new Label("test3"), new Label("test1")));
		Assert.assertEquals(new Integer(3),m.getFrequency(new Label("test3"), new Label("test2")));
		Assert.assertEquals(new Integer(0),m.getFrequency(new Label("test3"), new Label("test3")));
	}

	@Test
	public void test_add_invalid_arg1() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));

		TestConfusionMatrix m = new TestConfusionMatrix();
		m.init(labels);

		labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));
		labels.add(new Label("test4"));
		
		TestConfusionMatrix m2 = new TestConfusionMatrix();
		m2.init(labels);
		
		boolean error = false;
		try{
			m.add(m2);
		}catch(IllegalArgumentException e){
			error=true;
		}
		
		if(!error){
			Assert.fail("expected exception, but non occured.");
		}
		

		error = false;
		try{
			m2.add(m);
		}catch(IllegalArgumentException e){
			error=true;
		}
		
		if(!error){
			Assert.fail("expected exception, but non occured.");
		}
		

	}
	
	@Test
	public void test_add_invalid_arg2() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test5"));
		labels.add(new Label("test6"));
		labels.add(new Label("test7"));

		TestConfusionMatrix m = new TestConfusionMatrix();
		m.init(labels);

		labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));
		
		TestConfusionMatrix m2 = new TestConfusionMatrix();
		m2.init(labels);
		
		boolean error = false;
		try{
			m.add(m2);
		}catch(IllegalArgumentException e){
			error=true;
		}
		
		if(!error){
			Assert.fail("expected exception, but non occured.");
		}
		

		error = false;
		try{
			m2.add(m);
		}catch(IllegalArgumentException e){
			error=true;
		}
		
		if(!error){
			Assert.fail("expected exception, but non occured.");
		}
		

	}
	
	@Test
	public void test_add_invalid_arg3() {
		List<Label> labels = new ArrayList<Label>(4);
		labels.add(new Label("test5"));
		labels.add(new Label("test6"));
		labels.add(new Label("test7"));

		TestConfusionMatrix m = new TestConfusionMatrix();
		m.init(labels);

		labels = new ArrayList<Label>(4);
		labels.add(new Label("test1"));
		labels.add(new Label("test2"));
		labels.add(new Label("test3"));
		labels.add(new Label("test4"));
		
		TestConfusionMatrix m2 = new TestConfusionMatrix();
		m2.init(labels);
		
		boolean error = false;
		try{
			m.add(m2);
		}catch(IllegalArgumentException e){
			error=true;
		}
		
		if(!error){
			Assert.fail("expected exception, but non occured.");
		}
		

		error = false;
		try{
			m2.add(m);
		}catch(IllegalArgumentException e){
			error=true;
		}
		
		if(!error){
			Assert.fail("expected exception, but non occured.");
		}
		

	}
	
	
}
