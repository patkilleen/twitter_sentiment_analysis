package io;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

/**
 * All the configuration file keys are defined here as constants. It is
 * an interface to the for read values from the configration xml file. 
 * The ConfigFactory implements privately an implementation of IConfig.
 * @author Patrick Killeen
 *
 */
public interface IConfig {
	
	public static final String PROPERTY_LOG_LEVEL = "common.log.Logger.level";
	public static final String PROPERTY_ROOT_PATH = "common.root.path";
	public static final String PROPERTY_CORE_STANFORD_NLP_JAR_PATH = "classify.Classifier.stanford-NLP-jar-path";
	public static final String PROPERTY_CLASSIFIER_PROP_FILE = "classify.Classifier.prop-file-path";	
	public static final String PROPERTY_FUSION_TRAINING_DATA_FILE = "output.fusion-training-output-file-path";
	public static final String PROPERTY_FUSION_TESTING_DATA_FILE = "output.fusion-testing-output-file-path";
	public static final String PROPERTY_CLEANED_DEV_DATA_FILE = "input.dev-data-file-path";
	public static final String PROPERTY_CLEANED_TRAINING_DATA_FILE = "input.train-data-file-path";
	//public static final String PROPERTY_CLEANED_NLP_BASED_LABELED_TRAINING_DATA_FILE = "input.nlp-based-labels.train-data-file-path";
	//public static final String PROPERTY_CLEANED_NLP_BASED_LABELED_TESTING_DATA_FILE = "input.nlp-based-labels.testing-data-file-path";
	//public static final String PROPERTY_CLEANED_NLP_BASED_LABELED_DEV_DATA_FILE = "input.nlp-based-labels.dev-data-file-path";
	public static final String PROPERTY_CLEANED_TESTING_DATA_FILE = "input.test-data-file-path";
	public static final String PROPERTY_RAW_DEV_DATA_INPUT_FILE = "input.raw.dev-data-file-path";
	public static final String PROPERTY_RAW_TRAIN_DATA_INPUT_FILE = "input.raw.train-data-file-path";
	public static final String PROPERTY_RAW_TESTING_DATA_INPUT_FILE = "input.raw.test-data-file-path";
	public static final String PROPERTY_Q_DIVERSITY_THRESHOLD = "classify.diversity.q-threshold";
	public static final String PROPERTY_R_SCRIPT_EXECUTABLE_PATH = "fusion.r-script-executable-path";
	public static final String PROPERTY_R_SCRIPT_FUSION_MODEL_PATH = "fusion.r-script-fusion-model-path";
	public static final String PROPERTY_NLP_BASED_MODEL_XML_OUTPUT_PATH = "nlp-based.xml-output-file-path";
	public static final String PROPERTY_DATASET_PARSING_FORMAT_ID = "dataset.id";
	public static final String PROPERTY_STOP_WORD_INPUT_FILE = "tweet.stop-word-input-file";
	public static final String PROPERTY_POSITIVE_EMOTICON_LIST = "tweet.positive-emoticon-list.";
	public static final String PROPERTY_NEGATIVE_EMOTICON_LIST = "tweet.negative-emoticon-list.";
	public static final String PROPERTY_HLMS_ALPHA = "fusion.hlms.alpha";
	public static final String PROPERTY_LABEL_SET = "label.set.";
	public static final String PROPERTY_INCLUDING_NEUTRAL_TWEETS = "dataset.labels.neutral-tweets-included";
	public static final String PROPERTY_TEST_DATA_CROSS_FOLD_VALIDATION_K_VALUE = "dataset.test.k-fold-cross-validation.k-value";
	public static final String PROPERTY_DEV_DATA_CROSS_FOLD_VALIDATION_K_VALUE = "dataset.dev.k-fold-cross-validation.k-value";
	public static final String PROPERTY_TEST_DATASET_USING_K_FOLD_CROSS_VALIDATION_FLAG = "dataset.test.using-k-fold-cross-validation-flag";
	public static final String PROPERTY_DEV_DATASET_USING_K_FOLD_CROSS_VALIDATION_FLAG = "dataset.dev.using-k-fold-cross-validation-flag";
	public static final String PROPERTY_TRAINING_DATASET_RANDOM_SORT_FLAG = "dataset.traing.random-sort-flag";
	public static final String PROPERTY_REMOVING_SYMBOLES_FLAG = "dataset.normalization.remove-symboles-flag";
	
	
	
	//public static final String 	PROPERTY_DEVELOPMENT_DATASET_INPUT_FILE = "nlp-based.xml-output-file-path";
	//public static final String PROPERTY_SVM_PROP_FILE = "classify.Classifier.SVM.prop-file-path";
	///public static final String PROPERTY_NB_PROP_FILE = "classify.Classifier.MaxEnt.prop-file-path";
	//public static final String PROPERTY_MAX_ENT_PROP_FILE = "classify.Classifier.NB.prop-file-path";
	
	public Properties getEntries();

	public void setEntries(Properties properties);

	public String getConfigFilePath();

	public void setConfigFilePath(String configFilePath);
	
	/**
	 * Returns the value of the entry with a given key. If the entry doesn't exist
	 * an {@code IllegalArgumentException} is thrown.
	 * @param key The key the entry has in the configuration File.
	 * @return The value of the specified entry.
	 */
	public String getPropertyAndValidate(String key);
	
	/**
	 * Returns the value of the entry with a given key.
	 * @param key The key the entry has in the configuration File.
	 * @return The value of the specified entry.
	 */
	public String getProperty(String key);
	
	public void setProperty(String key,String value);
	public void setProperties(String setKey,List<String> values);

	/**
	 * Returns a variable number of entries.
	 * @param key The key that all entries start with. Example {@code "many.values."}.
	 * @return List of values.
	 */
	public List<String> getProperties(String key);
	
	/**
	 * prases a double from the configuration xml properties file of a specific entry.
	 * @param key the key to property to convert to double
	 * @return double representation of property's value in configuration file. 
	 */
	public double getDoubleProperty(String key);
		
	
	/**
	 * prases a boolean from the configuration xml properties file of a specific entry.
	 * @param key the key to property to convert to boolean
	 * @return boolean representation of property's value in configuration file. 
	 */
	public boolean getBooleanProperty(String key);
	
	/**
	 * Reads all the double string entries and converts to doubles
	 * @param key the key to property to convert to doubles (see getProperties)
	 * @return doubles of property's value in configuration file. 
	 */
	public List<Double> getDoubleProperties(String key);
	
	
	/**
	 * prases an int from the configuration xml properties file of a specific entry.
	 * @param key the key to property to convert to int
	 * @return integer representation of property's value in configuration file. 
	 */
	public int getIntProperty(String key);
	
	/**
	 * Reads all the int string entries and converts to integers
	 * @param key the key to property to convert to ints (see getProperties)
	 * @return integers of property's value in configuration file. 
	 */
	public List<Integer> getIntProperties(String key);
	
	/**
	 * Loads a configuration object by retrieving the target config file 
	 * path found in this config properties. Then loads the configuration 
	 * file from the path and returns the config object.
	 * @param key the key to target config file path found in this config's properties 
	 * @return target configuration object
	 */
	public IConfig loadTargetConfigFile(String key);
	
	
	/**
	 * Reads all the config file path entries and converts to configuration object
	 * @param key the key to property to convert to ints (see getProperties)
	 * @return configuration files
	 */
	public List<IConfig> loadTargetConfigFiles(String key);
	
	
	/**
	 * Creates a list of configuration objects by loading all xml files in a directory
	 * @param directory the directory to search for xml files
	 * @return list of configuration objects of xml files
	 * @throws InvalidPropertiesFormatException
	 * @throws IOException
	 */
	public  List<IConfig> loadConfigFilesInDirectory(String directory) throws InvalidPropertiesFormatException, IOException;

	
}
