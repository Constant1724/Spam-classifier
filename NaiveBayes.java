// Do not submit with package statements if you are using eclipse.
// Only use what is provided in the standard libraries.

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class NaiveBayes {
    /*
     * Train your Naive Bayes Classifier based on the given training
     * ham and spam emails.
     *
     * Params:
     *      hams - email files labeled as 'ham'
     *      spams - email files labeled as 'spam'
     */
	
	Map<String, Double> prSpam;
	Map<String, Double> prHam;
	double spam;
	double ham;
	
    public void train(File[] hams, File[] spams) throws IOException {
    	Map<String, Integer> spamDict = countWords(spams);
    	Map<String, Integer> hamDict = countWords(hams);
    	prSpam = computeConditionalProbability(spamDict, hamDict, spams.length);
    	prHam = computeConditionalProbability(hamDict, spamDict, hams.length);
    	spam = (double) spams.length / (hams.length + spams.length);
    	ham = (double) hams.length / (hams.length + spams.length);
    }
    
    private Map<String, Integer> countWords(File[] files ) throws IOException {
    	Map<String, Integer> dict = new HashMap<String, Integer>(files.length);
    	for(File file : files) {
    		Set<String> tokens = tokenSet(file);
    		for(String token: tokens) {
    			if(!dict.containsKey(token))
    				dict.put(token, 0);
    			dict.put(token, dict.get(token) + 1);
    		}
    	}
    	return dict;
    }
    
    private Map<String, Double> computeConditionalProbability(Map<String, Integer> majorDict, Map<String, Integer> minorDict, int number) {
    	Map<String, Double> probability = new HashMap<String, Double>(majorDict.size());
    	for(String word : majorDict.keySet()) {
    		probability.put(word, ((double) majorDict.get(word) + 1) / (number + 2));
    	}
    	for(String word : minorDict.keySet()) {
    		if(!probability.containsKey(word)) {
    			probability.put(word, 1.0 / (number + 2));
    		}
    	}
    	return probability;
    }
    
    
    /*
     * Classify the given unlabeled set of emails. Follow the format in
     * example_output.txt and output your result to stdout. Note the order
     * of the emails in the output does NOT matter.
     * 
     * Do NOT directly process the file paths, to get the names of the
     * email files, check out File's getName() function.
     *
     * Params:
     *      emails - unlabeled email files to be classified
     */
    public void classify(File[] emails) throws IOException {
    	for(File file : emails) {
    		Set<String> tokens = tokenSet(file);
    		double sPart = Math.log(spam);
    		double hPart = Math.log(ham);
    		for(String word : tokens) {
    			sPart += prSpam.containsKey(word) ? Math.log(prSpam.get(word)) : 0;
    			hPart += prHam.containsKey(word) ? Math.log(prHam.get(word)) : 0;
    		}
    		System.out.println(file.getName() + " " + (sPart > hPart ? "spam" : "ham"));
    	}
    }


    /*
     *  Helper Function:
     *  This function reads in a file and returns a set of all the tokens. 
     *  It ignores "Subject:" in the subject line.
     *  
     *  If the email had the following content:
     *  
     *  Subject: Get rid of your student loans
     *  Hi there ,
     *  If you work for us , we will give you money
     *  to repay your student loans . You will be 
     *  debt free !
     *  FakePerson_22393
     *  
     *  This function would return to you
     *  ['be', 'student', 'for', 'your', 'rid', 'we', 'of', 'free', 'you', 
     *   'us', 'Hi', 'give', '!', 'repay', 'will', 'loans', 'work', 
     *   'FakePerson_22393', ',', '.', 'money', 'Get', 'there', 'to', 'If', 
     *   'debt', 'You']
     */
    public HashSet<String> tokenSet(File filename) throws IOException {
        HashSet<String> tokens = new HashSet<String>();
        Scanner filescan = new Scanner(filename);
        filescan.next(); // Ignoring "Subject"
        while(filescan.hasNextLine() && filescan.hasNext()) {
            tokens.add(filescan.next());
        }
        filescan.close();
        return tokens;
    }
}
