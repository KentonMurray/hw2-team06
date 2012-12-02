package edu.cmu.lti.oaqa.openqa.test.team06.keyterm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.util.AbstractExternalizable;

import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;

public class KentonKeytermExtractor extends AbstractKeytermExtractor {

  @Override
  protected List<Keyterm> getKeyterms(String questionText) {

    List<Keyterm> importantTerms = new ArrayList<Keyterm>();
    
    int start;
    int end;
    
    //Using the same lingpipe file after seeing the merge
    String lingpipeNER = "src/main/java/ne-en-bio-genetag.HmmChunker";
    File modelFile = new File(lingpipeNER);
    
    System.out.println("Reading chunker from file=" + modelFile);
    Chunker chunker;
    try {
      chunker = (Chunker) AbstractExternalizable.readObject(modelFile);

      Chunking chunking = chunker.chunk(questionText);
      String importantTerm;
      for (Chunk chunked : chunking.chunkSet()) {
        start = chunked.start();
        end = chunked.end();
        
        importantTerm = questionText.substring(start, end);
        importantTerms.add(new Keyterm(importantTerm));
      }

    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    
    
    return importantTerms;

  }
}
