package edu.cmu.lti.oaqa.openqa.test.team06.keyterm;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import banner.Sentence;
import banner.tokenization.Tokenizer;
import bc2.Base;

public class EBase extends Base{
  public static HashMap<String, LinkedList<Base.Tag>> getTags(BufferedReader tagFile) throws IOException{
    return Base.getTags(tagFile);
  }
  public static Sentence getSentence(String id, String sentenceText, Tokenizer tokenizer, HashMap<String, LinkedList<Base.Tag>> tags){
    return Base.getSentence(id, sentenceText, tokenizer, tags);
  }
}

