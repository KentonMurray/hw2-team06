package edu.cmu.lti.oaqa.openqa.test.team06.passage;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.jsoup.Jsoup;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.hello.passage.KeytermWindowScorerSum;
import edu.cmu.lti.oaqa.openqa.test.team06.passage.*;
import edu.cmu.lti.oaqa.openqa.hello.passage.SimplePassageExtractor;

public class KentonBioPassageExtractor extends SimplePassageExtractor {

  @Override
  protected List<PassageCandidate> extractPassages(String question, List<Keyterm> keyterms,
          List<RetrievalResult> documents) {

    List<PassageCandidate> result = new ArrayList<PassageCandidate>();
    // System.out.println("Testing");
    for (RetrievalResult document : documents) {
    //RetrievalResult document = documents.get(1);
      //System.out.println("RetrievalResult: " + document.toString());
      String id = document.getDocID();
      try {
        String htmlText = wrapper.getDocText(id);

        // cleaning HTML text
        String text = htmlText;
//        String text = Jsoup.parse(htmlText).text().replaceAll("([\177-\377\0-\32]*)", "")/* .trim() */;
        //System.
        // for now, making sure the text isn't too long
        //text = text.substring(0, Math.min(15000, text.length()));
        //System.out.println(text);

        KentonPassageCanditateFinder finder = new KentonPassageCanditateFinder(id, text,
                new KentonKeytermWindowScorerSum());
        List<String> keytermStrings = Lists.transform(keyterms, new Function<Keyterm, String>() {
          public String apply(Keyterm keyterm) {
            return keyterm.getText();
          }
        });
        List<PassageCandidate> passageSpans = finder.extractPassages(keytermStrings
                .toArray(new String[0]));
        for (PassageCandidate passageSpan : passageSpans){
          result.add(passageSpan);
          //System.out.println("passageSpan: " + passageSpan.getDocID() + " Start: "+passageSpan.getStart() + " End: " + passageSpan.getEnd());
        }
      } catch (SolrServerException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

}
