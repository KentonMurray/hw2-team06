package edu.cmu.lti.oaqa.openqa.test.team06.retrieval;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.hello.retrieval.SimpleSolrRetrievalStrategist;

public class SimpleBioSolrRetrievalStrategist extends SimpleSolrRetrievalStrategist {

  protected List<RetrievalResult> retrieveDocuments(String query) {
    System.out.println("AAAAAAHHAHAHHAHAHAHHAAHA");
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    List<RetrievalResult> result = new ArrayList<RetrievalResult>();
    try {
      SolrDocumentList docs = wrapper.runQuery(query, hitListSize);

      for (SolrDocument doc : docs) {

        RetrievalResult r = new RetrievalResult((String) doc.getFieldValue("id"),
                (Float) doc.getFieldValue("score"), query);
        result.add(r);
        System.out.println(doc.getFieldValue("id"));
        System.out.println("Kenton");
      }
    } catch (Exception e) {
      System.err.println("Error retrieving documents from Solr: " + e);
    }
    return result;
  }

}
