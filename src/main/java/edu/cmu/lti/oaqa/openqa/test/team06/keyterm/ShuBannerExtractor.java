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

import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.umass.cs.mallet.base.fst.CRF;
import edu.umass.cs.mallet.base.util.MalletLogger;

public class ShuBannerExtractor extends AbstractKeytermExtractor{
  @Override
  protected List<Keyterm> getKeyterms(String question){
    // TODO Auto-generated method stub
    BannerProperties properties = BannerProperties.load("/usr4/ziy/tmp/hw2-eval/team06/banner.properties");
    File modelFile = new File("/usr4/ziy/tmp/hw2-eval/gene_model_v02.bin");
    BufferedReader mentionTestFile =null;
    try {
      mentionTestFile = new BufferedReader(new FileReader("/usr4/ziy/tmp/hw2-eval/team06/GENE.eval"));//./bc2geneMention/train/GENE.eval"));
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
    List<Keyterm> KeyList = new ArrayList<Keyterm>();
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
              KeyList.add(new Keyterm(Men.getText()));
            }
    }
    finally{
      
    }
    
    FileWriter fstream = null;
	try {
		fstream = new FileWriter("BANNER.txt",true);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    BufferedWriter out = new BufferedWriter(fstream);
    PrintWriter pw = new PrintWriter(out,false);
    pw.println(KeyList);
    pw.close();    
    
    return KeyList;
    
  }
}
