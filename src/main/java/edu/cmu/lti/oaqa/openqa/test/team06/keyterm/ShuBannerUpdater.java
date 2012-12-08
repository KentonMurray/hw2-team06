package edu.cmu.lti.oaqa.openqa.test.team06.keyterm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import banner.BannerProperties;
import banner.Sentence;
import banner.processing.LocalAbbreviationPostProcessor;
import banner.processing.PostProcessor;
import banner.tagging.CRFTagger;
import banner.tagging.Mention;
import banner.tokenization.Tokenizer;

import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermUpdater;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.umass.cs.mallet.base.fst.CRF;
import edu.umass.cs.mallet.base.util.MalletLogger;

public class ShuBannerUpdater extends AbstractKeytermUpdater{
  @Override
  protected List<Keyterm> updateKeyterms(String question, List<Keyterm> keyterms){
    // TODO Auto-generated method stub
    BannerProperties properties = BannerProperties.load("./banner.properties");
    File modelFile = new File("gene_model_v02.bin");
    BufferedReader mentionTestFile =null;
    try {
      mentionTestFile = new BufferedReader(new FileReader("./bc2geneMention/train/GENE.eval"));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Logger.getLogger(CRF.class.getName()).setLevel(Level.OFF);
    MalletLogger.getLogger(CRF.class.getName()).setLevel(Level.OFF);    
    
    properties.log();
    HashMap<String, LinkedList<EBase.Tag>> tags=null;
    try {
      tags = new HashMap<String, LinkedList<EBase.Tag>>(EBase.getTags(mentionTestFile));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    int space = question.indexOf(' ');
    String id = question.substring(0, space).trim();
    String sentenceText2 = question.substring(space).trim();
    Sentence sentence = EBase.getSentence(id, sentenceText2, properties.getTokenizer(), tags);
    //mentionsTest.addAll(sentence.getMentions());
    //mentionsAlternate.addAll(getMentions(sentence, alternateTags)); 
    
    Tokenizer tokenizer = properties.getTokenizer();
    CRFTagger tagger=null;
    try {
      tagger = CRFTagger.load(modelFile, properties.getLemmatiser(), properties.getPosTagger(), properties.getPreTagger());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    tagger.setTextDirection(properties.getTextDirection());
    PostProcessor postProcessor = properties.getPostProcessor();
    LocalAbbreviationPostProcessor localPP = new LocalAbbreviationPostProcessor();        
    try
    {
            String sentenceText = sentence.getText();
            Sentence sentence2 = new Sentence(sentence.getTag(), sentenceText);
            tokenizer.tokenize(sentence2);
            tagger.tag(sentence2);
            if (postProcessor != null)
                postProcessor.postProcess(sentence2);
            localPP.postProcess(sentence2);
            //String a = sentence2.getTrainingText(properties.getTagFormat());
            List<Mention> MenList = sentence2.getMentions();
            for(Mention Men : MenList){
              System.out.println("Extract::::"+Men.getText());
              String str = Men.getText();
              
              Keyterm Vterm;
              
              if( (Vterm = ContainsTerm(keyterms,str)) != null ){
            	  float prob = (float)0.8;
            	  Vterm.setProbablity(prob);
              }else{
            	  Vterm = new Keyterm(str);
            	  Vterm.setProbablity((float)0.8);
            	  keyterms.add(Vterm);
              }              
                          
            }
    }
    finally{
      
    }
    
    FileWriter fstream = null;
	try {
		fstream = new FileWriter("BANNERUPDATER.txt",true);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    BufferedWriter out = new BufferedWriter(fstream);
    PrintWriter pw = new PrintWriter(out,false);
    pw.println(keyterms);
    pw.close();    
    
    for(int i = 0 ; i < keyterms.size(); i++){
    	System.out.println(keyterms.get(i).getText() + " Prob: " + keyterms.get(i).getProbability());
    }    
    return keyterms;
 
  }
  public Keyterm ContainsTerm(List <Keyterm> keyList, String str){
	  for(int i = 0 ; i < keyList.size(); i ++){
		  Keyterm a = keyList.get(i);
		  if(a.getText().equals(str)){
			  return a;
		  }
	  }
	  return null;
  }  
}
