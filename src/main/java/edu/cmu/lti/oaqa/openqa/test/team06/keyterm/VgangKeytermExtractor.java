package edu.cmu.lti.oaqa.openqa.test.team06.keyterm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;

public class VgangKeytermExtractor extends AbstractKeytermExtractor {

  @Override
  protected List<Keyterm> getKeyterms(String SentenceText) {
    // TODO Auto-generated method stub
    String lines[] = SentenceText.split("\\r?\\n");
    System.out.println("Starting Annotation of text");
    List<Keyterm> keyterms = new ArrayList<Keyterm>();
    PosTagNamedEntityRecognizer nounRecognizer;

    BufferedReader file = null;
    final int max = 100000;
    // retrieving gene names
    ArrayList<String> list = new ArrayList<String>(max);
    String line;
    try {

      file = new BufferedReader(new FileReader("src/main/resources/data/genenames.in"));
      try {
        while (file.readLine() != null) {
          line = file.readLine();
          if (line != null) {
            list.add(line.toUpperCase());
          }
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      try {
        nounRecognizer = new PosTagNamedEntityRecognizer();
        for (int i = 0; i < lines.length; i++) {
          String[] token = lines[i].split(" "); // Splitting the sentence into token.
          for (int j = 1; j < token.length; j++) {

            Map<Integer, Integer> GeneMap = nounRecognizer.getGeneSpans(token[j]);
            Set<Entry<Integer, Integer>> entrySet = GeneMap.entrySet();
            for (Entry<Integer, Integer> entry : entrySet) {

              if (list.contains((token[j].substring(entry.getKey(), entry.getValue()))
                      .toUpperCase())) {
                keyterms.add(new Keyterm(token[j].substring(entry.getKey(), entry.getValue())));

              }
            }
          }
        }
      } catch (ResourceInitializationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      file.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //System.out.println(keyterms);
    return keyterms;
  }
}
