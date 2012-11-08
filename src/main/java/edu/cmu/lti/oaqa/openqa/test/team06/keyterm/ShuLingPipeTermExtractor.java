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

public class ShuLingPipeTermExtractor extends AbstractKeytermExtractor{

  @Override
  protected List<Keyterm> getKeyterms(String question) {
    // TODO Auto-generated method stub
    File modelFile = new File("src/ne-en-bio-genetag.HmmChunker");
    Chunker chunker = null;
    Chunking chunking;
    Chunk[] Chunkarray;
    try {
      chunker = (Chunker) AbstractExternalizable.readObject(modelFile);
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    chunking  = chunker.chunk(question);
    Chunkarray = chunking.chunkSet().toArray(new Chunk[0]);
    //List<Keyterm> KeyList;
    List<Keyterm> KeyList = new ArrayList<Keyterm>();
    for(int j = 0 ; j < Chunkarray.length; j++){
      String str;
      str = question.substring(Chunkarray[j].start(),Chunkarray[j].end());
      //System.out.println(str);
      KeyList.add(new Keyterm(str));
    }
    return KeyList;
  }
  public static void main(String args[]){
   //ShuLingPipeTermExtractor SLP = new ShuLingPipeTermExtractor();
   
    //SLP.getKeyterms("");
    //SLP.getKeyterms("QUERY: disease s Alzheimer affect gene Presenilin-1 the in mutations do How QUERY: gene Presenilin-1 disease s Alzheimer affect gene Presenilin-1 the in mutations do How");
  }
}
