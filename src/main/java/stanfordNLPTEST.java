import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;

public class stanfordNLPTEST {
	public static int FindStart(List<HasWord> list, String paragraph){
		String htmldelimit = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
		String com = new String();
		for(int i = 0 ; i < 5 ;i++){
			if(i>= list.size()) break;
			com = com + list.get(i).toString() + ".";
		}
		System.out.println(com);
        Pattern p = Pattern.compile(com);
        Matcher m = p.matcher(paragraph);
        while(m.find()){
        	System.out.println(m.start());
        	System.out.println(paragraph.substring(m.start()));
        }
        return 1;
	}
  public static void main(String[] args) throws IOException {
    //for (String arg : args) {
      // option #1: By sentence.
	  
	  String paragraph = "Another ex-Golden Stater, Paul Stankowski from Oxnard, is contending " +
	  		"for a berth on the U.S. Ryder Cup team after winning his first PGA Tour" +
	  		"event last year and staying within three strokes of the lead throughthree " +
	  		"rounds of last month's U.S. Open. H.J. Heinz Company said it " +
	  		"completed the sale of its Ore-Ida frozen-food business catering to the" +
	  		"service industry to McCain Foods Ltd. for about $500 million." +
	  		"It's the first group action of its kind in Britain and one of" +
	  		"only a handful of lawsuits against tobacco companies outside the" +
	  		"U.S. A Paris lawyer last year sued France's Seita SA on behalf of" +
	  		"two cancer-stricken smokers. Japan Tobacco Inc. faces a suit from" +
	  		"five smokers who accuse the government-owned company of hooking" +
	  		"them on an addictive product.";
	  Reader reader = new StringReader(paragraph);
      DocumentPreprocessor dp = new DocumentPreprocessor(reader);
      for (List sentence : dp) {
    	FindStart(sentence,paragraph);
        System.out.println(sentence);
      }
      // option #2: By token
      /*PTBTokenizer ptbt = new PTBTokenizer(new FileReader(arg),
              new CoreLabelTokenFactory(), "");
      for (CoreLabel label; ptbt.hasNext(); ) {
        label = ptbt.next();
        System.out.println(label);
      }*/
    
  }
}