package edu.cmu.lti.oaqa.openqa.test.team06.keyterm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;

public class VgangWhatizitUpdater extends AbstractKeytermExtractor{

  @Override
  protected List<Keyterm> getKeyterms(String SentenceText) {
    List<Keyterm> returnedTerms = new ArrayList<Keyterm>();
    // TODO Auto-generated method stub
    String lines[] = SentenceText.split("\\r?\\n");
    for (int i = 0; i < lines.length; i++) {
      //dieaseannotator.proteinDiseaseAnnotation(lines[i]);
      try {
        String serverUrl = (SentenceText.length() == 1)? SentenceText : HttpClient.SERVER_URL;
        HttpClient client = new HttpClient(serverUrl);
        String s = "whatizitProteinDiseaseUMLS\n <document xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:z='http://www.ebi.ac.uk/z' source='Whatizit'><text>"+ SentenceText + "</text></document>";
        byte[] b = s.getBytes("US-ASCII");
        InputStream in = new ByteArrayInputStream(b);
        client.upload(in, returnedTerms);
        client.download(System.out, returnedTerms);
        client.close();
      }
      catch (IOException e){
        e.printStackTrace();
        System.err.println(e.getMessage());
      }
    }
    
   return returnedTerms;//null;
  }

//  public static void main(String[] args) {
//    VgangWhatizitUpdater vg = new VgangWhatizitUpdater();
//  vg.getKeyterms("How does APC (adenomatous polyposis coli) protein affect actin assembly");
//}
}
