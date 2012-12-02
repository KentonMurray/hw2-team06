package edu.cmu.lti.oaqa.openqa.hellobioqa.retrieval;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

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
public class YiwenChenRetrieval extends AbstractRetrievalStrategist {

  protected Integer hitListSize;

  protected SolrWrapper wrapper;

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    try {
      this.hitListSize = (Integer) aContext.getConfigParameterValue("hit-list-size");
    } catch (ClassCastException e) { // all cross-opts are strings?
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
          List<Keyterm> keyterms) {
    System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
    System.out.println("keyterms: ");
    for(Keyterm kt: keyterms){
      System.out.println(kt.toString());
    }
    System.out.println();
    File file = new File("src/main/resources/input/stopwords.txt");
    String query = null;
    String line = null;
    HashSet<String> stopwords = new HashSet<String>();
    int i;
    try{
      BufferedReader input = new BufferedReader(new FileReader(file));
      while((line = input.readLine()) != null){
        line = line.trim();
        stopwords.add(line);
      }
      input.close();
      System.out.println("questionText: " + questionText);
      String[] terms = questionText.split(" ");
      terms[terms.length - 1] = terms[terms.length - 1].substring(0, terms[terms.length - 1].length() - 1);
      ArrayList<String> support_terms = new ArrayList<String>();
      int sentinel = 0;
      for(i = 0; i < terms.length; i++){
        terms[i] = terms[i].trim();
        if(terms[i].length() == 0)continue;
        if(i == 0)terms[i] = terms[i].toLowerCase();
        if(stopwords.contains(terms[i]))continue;
        sentinel = 0;
        for (Keyterm keyterm : keyterms) {
          line = keyterm.getText();
          if(terms[i].equals(line) || line.contains(terms[i])){
            sentinel = 1;
            break;
          }
        }
        if(sentinel == 0){
          support_terms.add(terms[i]);
        }
      }
      System.out.println(support_terms);
      query = formulateQuery(keyterms, support_terms);
      System.out.println(" QUERY: " + query);
      System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
      return retrieveDocuments(query);
    }catch(Exception e){
      System.err.println(e.getMessage());
    }finally{
      return retrieveDocuments(query);
    }
  };

  protected String formulateQuery(List<Keyterm> keyterms, ArrayList<String> support_terms) {
    StringBuffer key_part = new StringBuffer();
    StringBuffer support_part = new StringBuffer();
    
  //original algorithm
//    for(Keyterm keyterm : keyterms){
//      key_part.append(keyterm.toString() + " ");
//    }
    String kt;
    String[] kt_split;
    for (Keyterm keyterm : keyterms) {
      kt = keyterm.getText();
      kt = kt.trim();
      if(kt.indexOf(" ") == -1){
        key_part.append("+" + keyterm.getText() + " ");
      }else{
        kt_split = kt.split(" ");
        for(String k: kt_split){
          key_part.append("+" + k + " ");
        }
      }
    }
    if(support_terms.size() > 0){
      support_part.append("+(");
      for(String support_term : support_terms){
        System.out.println(support_term);
        support_part.append(support_term + " ");
      }
    }
    support_part.deleteCharAt(support_part.length() - 1);
    support_part.append(")");
    String query = key_part.toString() + support_part.toString();
    
    //original algorithm
    //String query = key_part.toString();
    return query;
  }

  private List<RetrievalResult> retrieveDocuments(String query) {
    List<RetrievalResult> result = new ArrayList<RetrievalResult>();
    try {
      SolrDocumentList docs = wrapper.runQuery(query, hitListSize);
      for (SolrDocument doc : docs) {
        RetrievalResult r = new RetrievalResult((String) doc.getFieldValue("id"),
                (Float) doc.getFieldValue("score"), query);
        result.add(r);
        System.out.println(doc.getFieldValue("id"));
      }
    } catch (Exception e) {
      System.err.println("Error retrieving documents from Solr: " + e);
    }
    return result;
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    super.collectionProcessComplete();
    wrapper.close();
  }
}
