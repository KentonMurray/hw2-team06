package edu.cmu.lti.oaqa.openqa.test.team06.keyterm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermUpdater;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class ShuNLPVerbTermUpdater extends AbstractKeytermUpdater {

	public List<Keyterm> updateKeyterms(String question, List<Keyterm> keyterms) {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation document = new Annotation(question);
		pipeline.annotate(document);
		Set<String> stopwordList = new HashSet<String>();
		stopwordList.add("do");
		stopwordList.add("does");
		stopwordList.add("is");
		stopwordList.add("are");
		stopwordList.add("be");
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			List<CoreLabel> candidate = new ArrayList<CoreLabel>();
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String pos = token.get(PartOfSpeechAnnotation.class);
				if (pos.matches("VB") || pos.matches("VBD") || pos.matches("VBZ") || pos.matches("VBP")) {
					if(stopwordList.contains(token.originalText()))
						continue;
					System.out.println("VB::::::" + pos + " " + token.originalText());
					candidate.add(token);
					Keyterm Vterm = new Keyterm(token.originalText());
					Vterm.setProbablity((float)0.2);                                           
					keyterms.add(Vterm);
				}
			}
		}
		    FileWriter fstream = null;
		try {
			fstream = new FileWriter("LastUpdater.txt",true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    BufferedWriter out = new BufferedWriter(fstream);
	    PrintWriter pw = new PrintWriter(out,false);
	    pw.println(keyterms);
	    pw.close();
	
		
		return keyterms;
	}
}
