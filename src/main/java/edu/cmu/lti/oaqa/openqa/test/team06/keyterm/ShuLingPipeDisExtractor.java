package edu.cmu.lti.oaqa.openqa.test.team06.keyterm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Streams;

import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;

public class ShuLingPipeDisExtractor extends AbstractKeytermExtractor{

  public String[] delimit(String str){
		String delimiter = "\\(|\\)";
		//s.replaceAll("\\s+$", "");
		String b[] = str.split(delimiter);
		for(int i = 0 ; i < b.length ; i++)
			b[i] = (b[i].replaceAll("^\\s+", "").replaceAll("\\s+$",""));
		return b;
  }
  @Override
  protected List<Keyterm> getKeyterms(String question) {
    // TODO Auto-generated method stub
    //File modelFile = new File("./ne-en-bio-genia.TokenShapeChunker");
	URL modelUrl = null;
	try {
		modelUrl = new URL("file:./ne-en-bio-genia.TokenShapeChunker");
	} catch (MalformedURLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	ObjectInputStream ois = null;
	try {
		ois = new ObjectInputStream(modelUrl.openStream());
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    Chunker chunker = null;
    Chunking chunking;
    Chunk[] Chunkarray;
    try {
      //chunker = (Chunker) AbstractExternalizable.readObject(modelFile);
    	chunker = (Chunker) ois.readObject();
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
      System.out.println("Extract::::"+str);
      String strs[] = delimit(str);
      for(int k = 0 ; k < strs.length ; k++){
    	  Keyterm DisTerm = new Keyterm(strs[k]);
    	  DisTerm.setProbablity((float)0.5);
    	  KeyList.add(DisTerm);
      }
    }
/*    
    FileWriter fstream = null;
	try {
		fstream = new FileWriter("tokenShape.txt",true);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    BufferedWriter out = new BufferedWriter(fstream);
    PrintWriter pw = new PrintWriter(out,false);
    pw.println(KeyList);
    pw.close();
*/
    for(int i = 0 ; i < KeyList.size(); i++){
    	System.out.println(KeyList.get(i).getText() + " Prob: " + KeyList.get(i).getProbability());
    }    
    Streams.closeQuietly(ois);
    return KeyList;
  }
}
