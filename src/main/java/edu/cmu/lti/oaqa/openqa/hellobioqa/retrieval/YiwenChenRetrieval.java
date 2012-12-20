package edu.cmu.lti.oaqa.openqa.hellobioqa.retrieval;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;

//import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;
import edu.cmu.lti.oaqa.cse.basephase.retrieval.AbstractRetrievalStrategist;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class YiwenChenRetrieval extends AbstractRetrievalStrategist {

  protected Integer hitListSize;

  protected SolrWrapper wrapper;

  //public BufferedWriter output;
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    try {
//      File f = new File("./data/Banner_genename.txt");      
//      output = new BufferedWriter(new FileWriter(f));
      this.hitListSize = (Integer) aContext.getConfigParameterValue("hit-list-size");
    } catch (Exception e) { // all cross-opts are strings?
      this.hitListSize = Integer.parseInt((String) aContext
              .getConfigParameterValue("hit-list-size"));
    }
    String serverUrl = (String) aContext.getConfigParameterValue("server");
    Integer serverPort = (Integer) aContext.getConfigParameterValue("port");
    Boolean embedded = (Boolean) aContext.getConfigParameterValue("embedded");
    String core = (String) aContext.getConfigParameterValue("core");
    try {
      this.wrapper = new SolrWrapper(serverUrl, serverPort, embedded, core);
    } catch (Exception e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  protected final List<RetrievalResult> retrieveDocuments(String questionText,
          List<Keyterm> keyterms){
/*    try{
      output.write("Query: " + questionText);
      output.write("\n");
      output.flush();
    }catch(Exception e){
      System.err.println(e.getMessage());
    }
*/    System.out.println("keyterms: ");
    for(Keyterm kt: keyterms){
      System.out.println(kt.toString());
    }
    System.out.println();
    String query = null;
    try{
      query = formulateQuery(keyterms, 1);
    }catch(Exception e){
      System.err.println(e.getMessage());
    }finally{
      return retrieveDocuments(query, keyterms, 2);
    }
  };

  protected String formulateQuery(List<Keyterm> keyterms, int index) throws Exception{
    StringBuffer key_part = new StringBuffer();
    if(index == 1){
      for (Keyterm keyterm : keyterms) {
          if(keyterm.getProbability() < 0.5){
            //output.write("Verb:" + keyterm.getText() + " ");
            if(keyterm.getText().indexOf(" ") == -1)key_part.append("" + keyterm.getText() + " ");
            else key_part.append("\"" + keyterm.getText() + "\" ");
          }else if(keyterm.getProbability() < 0.8){
            //output.write("Disease:" + keyterm.getText() + " ");
            if(keyterm.getText().indexOf(" ") == -1)key_part.append("+" + keyterm.getText() + " ");
            else key_part.append("+\"" + keyterm.getText() + "\" ");
          }else{  
            //output.write("Gene Name:" + keyterm.getText() + "  ");
            if(keyterm.getText().indexOf(" ") == -1)key_part.append("+" + keyterm.getText() + " ");
            else key_part.append("+\"" + keyterm.getText() + "\" ");
          }
          //output.flush();
        }
    }else if(index == 2){
      for (Keyterm keyterm : keyterms) {
        if(keyterm.getProbability() < 0.5){
          //output.write("Verb:" + keyterm.getText() + " ");
          if(keyterm.getText().indexOf(" ") == -1)key_part.append("" + keyterm.getText() + " ");
          else key_part.append("\"" + keyterm.getText() + "\" ");
        }else if(keyterm.getProbability() < 0.8){
          //output.write("Disease:" + keyterm.getText() + " ");
          if(keyterm.getText().indexOf(" ") == -1)key_part.append("" + keyterm.getText() + " ");
          else key_part.append("\"" + keyterm.getText() + "\" ");
        }else{  
          //output.write("Gene Name:" + keyterm.getText() + "  ");
          if(keyterm.getText().indexOf(" ") == -1)key_part.append("+" + keyterm.getText() + " ");
          else key_part.append("+\"" + keyterm.getText() + "\" ");
        }
        //output.flush();
      }
    }else{
      for (Keyterm keyterm : keyterms) {
        if(keyterm.getProbability() < 0.5){
          //output.write("Verb:" + keyterm.getText() + " ");
          if(keyterm.getText().indexOf(" ") == -1)key_part.append("" + keyterm.getText() + " ");
          else key_part.append("\"" + keyterm.getText() + "\" ");
        }else if(keyterm.getProbability() < 0.8){
          //output.write("Disease:" + keyterm.getText() + " ");
          if(keyterm.getText().indexOf(" ") == -1)key_part.append("" + keyterm.getText() + " ");
          else key_part.append("\"" + keyterm.getText() + "\" ");
        }else{  
          //output.write("Gene Name:" + keyterm.getText() + "  ");
          if(keyterm.getText().indexOf(" ") == -1)key_part.append("" + keyterm.getText() + " ");
          else key_part.append("\"" + keyterm.getText() + "\" ");
        }
        //output.flush();
      }
    }
    
    //output.write("\n");
    String query = key_part.toString();
    //output.write("Structural Query: " + query + "\n");
    return query;
  }

  public String GetHtml(String term)throws Exception{
    StringBuilder builder = new StringBuilder();
    URLConnection connection;
    URL url = new URL("http://www.ncbi.nlm.nih.gov/gene?term=" + term);
    connection = url.openConnection();
    String line;
    try{
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      while((line = reader.readLine()) != null){
        builder.append(line);
      }
    }catch(Exception e){
      System.err.println(e.getMessage());
    }
    String html = builder.toString();
    return html;
  }
  
  private List<RetrievalResult> retrieveDocuments(String query, List<Keyterm> keyterms, int index) {
    List<RetrievalResult> result = new ArrayList<RetrievalResult>();
    List<Keyterm> keyterm_alias = new ArrayList<Keyterm>();
    List<Keyterm> original = new ArrayList<Keyterm>();
    SolrDocumentList temp_docs;
    String temp_query;
    Keyterm k, geneAlias;
    try {
      SolrDocumentList docs = wrapper.runQuery(query, 150);
      //output.write("Number of retireved Documents: " + docs.size());
      //output.write("\n");
      //output.flush();
      
      //
      if(docs.size() < 99){
      List<Keyterm>gene = new ArrayList<Keyterm>();
      List<Keyterm>disease = new ArrayList<Keyterm>();
      List<Keyterm>verb = new ArrayList<Keyterm>();
      for(Keyterm key: keyterms){
        if(key.getProbability() < 0.5)verb.add(key);
        else if(key.getProbability() < 0.8)disease.add(key);
        else  gene.add(key);
      }
      
      int i, j;
      String term;
      HashSet<String> supply;
      for(i = 0; i< gene.size(); i++){
        original.clear();
        k = gene.get(i);
        original.addAll(gene);
        original.remove(k);
        original.addAll(disease);
        original.addAll(verb);
 
        term = k.getText();
        String html = GetHtml(term);
        Pattern p = Pattern.compile("Other Aliases: </dt><dd class=\"desig\">(.*?)</dd>");
        Matcher m = p.matcher(html);
        supply = new HashSet<String>();
        supply.add("NOT");
        supply.add("AND");
        while(m.find()){
          String[] alias;
          if(m.group(1).toString().indexOf(" ") != -1)alias = m.group(1).toString().split(", ");
          else{
            alias = new String[1];
            alias[0] = m.group(1).toString().trim();
          }
          for(j = 0; j < alias.length; j++){
            if(supply.contains(alias[j]))continue;
            if(alias[j].indexOf("<") != -1)continue;
            supply.add(alias[j]);
            keyterm_alias.clear();
            keyterm_alias.addAll(original);
            geneAlias = new Keyterm(alias[j]);
            geneAlias.setProbablity((float)0.9);
            keyterm_alias.add(geneAlias);
            temp_query = formulateQuery(keyterm_alias, 1);
            System.out.println("Original Temp query: " + temp_query);
            //if(temp_query.indexOf("<") != -1)continue;fi
            //output.write("Synonym: " + alias[j] + "\n");
            //output.flush();
            temp_query = temp_query.replaceAll(":", "\\\\:");
            System.out.println("Temp query: " + temp_query);
            temp_docs = wrapper.runQuery(temp_query,  50);
            docs.addAll(temp_docs);
          }
          
        }
      }
      if(docs.size() < 10){
        temp_query = formulateQuery(keyterms, 2);
        temp_docs = wrapper.runQuery(temp_query, 20);
        docs.addAll(temp_docs);
      }
      if(docs.size() < 10){
        temp_query = formulateQuery(keyterms, 3);
        temp_docs = wrapper.runQuery(temp_query, 20);
        docs.addAll(temp_docs);
      }
      }
      //
      
      //output.write("Number of retireved Documents: " + docs.size() + "\n");
      //output.write("\n");
      //output.flush();
      System.out.println("Doc size: " + docs.size());
      for (SolrDocument doc : docs) {
        RetrievalResult r = new RetrievalResult((String) doc.getFieldValue("id"),
                (Float) doc.getFieldValue("score"), query);
        result.add(r);
        System.out.println(doc.getFieldValue("id"));
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Error retrieving documents from Solr: " + e);
    }
    
    Set<RetrievalResult> s = new TreeSet<RetrievalResult>(new Comparator<RetrievalResult>(){
    	public int compare(RetrievalResult o1, RetrievalResult o2) {
            return o1.getDocID().compareTo(o2.getDocID());
        }
    });
    System.out.println("Result size: " + result.size() +"   " +  result);
    s.addAll(result);
    System.out.println("Add all over");
    List<RetrievalResult> res = new ArrayList<RetrievalResult>(s); 

    
    System.out.println("Res size: " + res.size() +"   " +  res);
    return res;
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    super.collectionProcessComplete();
    wrapper.close();
  }
}
