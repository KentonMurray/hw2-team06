package edu.cmu.lti.oaqa.openqa.test.team06.keyterm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;

public class VgangKeytermExtractor extends AbstractKeytermExtractor {

  @Override
  protected List<Keyterm> getKeyterms(String SentenceText) {

    String lines[] = SentenceText.split("\\r?\\n");
    System.out.println("Starting Annotation of text");
    List<Keyterm> keyterms = new ArrayList<Keyterm>();
    PosTagNamedEntityRecognizer nounRecognizer;
    final int max = 100000;
    // retrieving gene names
    ArrayList<String> list = new ArrayList<String>(max);
    String line;
    try {
      File f = new File("src/main/resources/data/genename.txt");
      if (f.exists()) {
        System.out.println("Reading Gene dataBank name directory generated through feature coupling generalization framework");
      } else {
        System.out.println("Please wait: Downloading online Gene dataBank name directory");
        URL url = new URL("https://www.dropbox.com/s/95stn2993xfpqzm/genename.txt.gz?dl=1");
        URLConnection con = url.openConnection();
        BufferedInputStream in = new BufferedInputStream(con.getInputStream());
        FileOutputStream out = new FileOutputStream("src/main/resources/data/genename.txt.gz");
        int i = 0;
        byte[] bytesIn = new byte[3000000];
        while ((i = in.read(bytesIn)) >= 0) {
          out.write(bytesIn, 0, i);
        }
        out.close();
        in.close();
        try {
          Unzip("src/main/resources/data/genename.txt.gz");
          System.out.println("Reading Gene dataBank name directory generated through feature coupling generalization framework");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      BufferedReader file = new BufferedReader(new FileReader(
              "src/main/resources/data/genename.txt"));
      try {
        while ((line = file.readLine()) != null) {
          if (line != null) {
            list.add(line.toUpperCase());
         }
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      try {
        System.out.println("Implementing Stanford CoreNLP pos tag entity recognizer and Gene dataBank name directory");
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
    System.out.println(keyterms);
   //  return keyterms;
    return null;
  }

  public static String Unzip(String inFilePath) throws Exception {
    GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(inFilePath));
    String outFilePath = inFilePath.replace(".gz", "");
    OutputStream out = new FileOutputStream(outFilePath);
    byte[] buf = new byte[1024];
    int len;
    while ((len = gzipInputStream.read(buf)) > 0)
      out.write(buf, 0, len);
    gzipInputStream.close();
    out.close();
    new File(inFilePath).delete();
    return outFilePath;
  }

 public static void main(String[] args) {
  VgangKeytermExtractor vg = new VgangKeytermExtractor();
  vg.getKeyterms("160|What is the role of PrnP in mad cow disease?");
  }

}
