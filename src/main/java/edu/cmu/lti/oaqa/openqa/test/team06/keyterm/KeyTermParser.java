package edu.cmu.lti.oaqa.openqa.test.team06.keyterm;

import java.io.File;
import java.util.*;
import java.util.Iterator;
import java.util.Set;
import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.util.AbstractExternalizable;
import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;

public class KeyTermParser extends AbstractKeytermExtractor{
  protected List<Keyterm> getKeyterms(String question){
    List<Keyterm> result_list = new ArrayList<Keyterm>();
    //Call the methods from Lingpipe NER to detect the gene names in the question.
    String lingpipeNER = "src/main/java/ne-en-bio-genetag.HmmChunker";
    File modelFile = new File(lingpipeNER);
    Chunker chunker;
    try{
      chunker = (Chunker) AbstractExternalizable.readObject(modelFile);
      Chunking chunking;
      Set<Chunk> cs;
      Iterator<Chunk> iter;
      Chunk c;
      chunking = chunker.chunk(question);
      cs = chunking.chunkSet();
      iter = cs.iterator();
      String key_term;
      Keyterm k;
      while(iter.hasNext()){
        c = iter.next();
        key_term = question.substring(c.start(), c.end());
        k = new Keyterm(key_term);
        result_list.add(k);
      }
    }catch(Exception e){
      System.err.println(e.getMessage());
    }
    return result_list;
  }
}
