package edu.cmu.lti.oaqa.openqa.test.team06.passage;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.openqa.hello.passage.KeytermWindowScorerProduct;
import edu.cmu.lti.oaqa.openqa.hello.passage.KeytermWindowScorerSum;
import edu.cmu.lti.oaqa.openqa.hello.passage.PassageCandidateFinder;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

public class KentonPassageCanditateFinder {
  private String text;
  private String docId;

  private int textSize; // values for the entire text

  private int totalMatches;

  private int totalKeyterms;

  private KentonKeytermWindowScorerSum scorer;

  public KentonPassageCanditateFinder(String docId, String text, KentonKeytermWindowScorerSum scorer) {
    super();
    this.text = text;
    this.docId = docId;
    this.textSize = text.length();
    this.scorer = scorer;
  }
 public int GetLength(List<HasWord> a){
		int length = 0;
		for(HasWord token:a){
			length += token.toString().length();
		}
		return length;
	}

public int Getdifference(TreeMap<Integer,Integer> lengthMap,int key){
	int diff = 0;
	for(Map.Entry<Integer,Integer> entry: lengthMap.entrySet()){
		if(key < entry.getKey())
			break;
		diff = entry.getValue();
	}
	return diff;
}
public String RemoveHTMLtag(String text){
  	String striptext = text;
	String htmldelimit = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    Pattern p = Pattern.compile(htmldelimit);
    striptext = text.replaceAll(htmldelimit,"");
    return striptext;
}
  public TreeMap<Integer,Integer> MakeLengthMap(String text){
	  TreeMap<Integer,Integer> lengthMap = new TreeMap<Integer,Integer>();
	  String htmldelimiter = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
        Pattern p = Pattern.compile(htmldelimiter);
        Matcher m = p.matcher(text);	
        int lastend = 0;
        int cumulength = 0;
        int cumuHTML = 0;
        while(m.find()){
        	cumulength += m.start() - lastend;
        	cumuHTML += (m.end() - m.start());
        	lengthMap.put(cumulength, cumuHTML);
        	lastend = m.end();
        	System.out.println(m.start() +" "+ m.end());
        }		  
        return lengthMap;
  }
  /*
  public List<String> sentencesplitter(String paragraph){
	  Reader reader = new StringReader(paragraph);
	  DocumentPreprocessor dp = new DocumentPreprocessor(reader);

	  List<String> sentenceList = new LinkedList<String>();
	  Iterator<List<HasWord>> it = dp.iterator();
	  while (it.hasNext()) {
	     StringBuilder sentenceSb = new StringBuilder();
	     List<HasWord> sentence = it.next();
	     for (HasWord token : sentence) {
	        if(sentenceSb.length()>1) {
	           sentenceSb.append(" ");
	        }
	        sentenceSb.append(token);
	     }
	     sentenceList.add(sentenceSb.toString());
	  }
	  return sentenceList;
  }
  */
  public int FindEnd(String paragraph, int begin,List<HasWord> a, List<HasWord> b){
		String ptr = new String(paragraph.substring(begin));
		int length = begin;
		
		for(HasWord token: a){
			System.out.println("token: " + token + "ptr: " + ptr.substring(0, 100)) ;
			if(token.toString().length()<3)
				continue;
			int len = ptr.indexOf(token.toString());
			if(len >=0 ){
				length += (len + token.toString().length());
				System.out.println("len: " + len + "string length: " + token.toString().length());
				ptr = ptr.substring(len + token.toString().length());
			}
			else{
				continue;
			}
	//		System.out.println("Token: " + token.toString() + " length:" + length);
		}
		int len = ptr.indexOf(b.get(0).toString());
		return length + len;
} 
  public List<String> sentencesplitter(String paragraph){
		Reader reader = new StringReader(paragraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);

		List<String> sentenceList = new ArrayList<String>();
		Iterator<List<HasWord>> it = dp.iterator();
		int len = 0;
		List<HasWord> sentence;
		String ptr = new String(paragraph);
		String nextptr = new String();
		System.out.println("sentencesplitter");
		if(it.hasNext()){
			List<HasWord> next_sentence = it.next();
			while (it.hasNext()) {
				StringBuilder sentenceSb = new StringBuilder();
				sentence = next_sentence;
				next_sentence = it.next();
				//System.out.println("nextptr"+nextptr);

				//int begin = ptr.indexOf(sentence.get(0).toString());
				int begin = 0;
				int end = FindEnd(ptr,begin,sentence,next_sentence);
				System.out.println("begin" + begin + "end" + end);
				//int end = nextptr.indexOf(next_sentence.get(0).toString())+length;
				System.out.println(ptr.substring(begin, end));
				sentenceList.add(ptr.substring(begin, end));
				System.out.println("Get sentences: " + ptr.substring(begin,end));
				ptr = ptr.substring(end);
			}
			sentenceList.add(ptr);
		}
		return sentenceList;
  }
  
  public List<PassageCandidate> extractPassages(String[] keyterms) {
	  System.out.println("New Article!!!!!!!!!");
    List<List<PassageSpan>> matchingSpans = new ArrayList<List<PassageSpan>>();
    String striptext = RemoveHTMLtag(text);
    TreeMap<Integer,Integer> lengthMap = MakeLengthMap(text);
    //String striptext = text;
    String[] sentencestring = striptext.split("[.?!]");
    List<String > sentences = Arrays.asList(sentencestring);
//    System.out.println("striptext: ");
//    System.out.println(striptext);
//    List<String> sentences = sentencesplitter(striptext);
//    int len = 0;
//    System.out.println("total sentences:"+sentences.size());
//
//    for(int i = 0 ; i < sentences.size() ;i++){
//    	System.out.println("Sentence"+i+":   "+sentences.get(i));
//    	System.out.println("Sentence"+i+":   "+striptext.substring(len, len+sentences.get(i).length()));
//    	len+=sentences.get(i).length();
//    }
    
//    System.out.println("OriginalText: ");
//    System.out.println(striptext.substring(0,len));

    
    List<PassageCandidate> result = new ArrayList<PassageCandidate>();
    int accu = 0;
    int sentinel = 0;
    int sentenceMatches = 0;
    totalMatches = 0;
    totalKeyterms = 0;
//    for(String sent: sentences){
    for(int i = 0 ; i < sentences.size() ;i++){
   // Find all keyterm matches.
      System.out.println("i::::"+i+"!!!!!!!");
      String sent = sentences.get(i);
      sentinel = 0;
      List<PassageSpan> matchedSpans = new ArrayList<PassageSpan>();
      for (String keyterm : keyterms) {
    	if(keyterm.length() < 3)
    		continue;
        Pattern p = Pattern.compile(keyterm);
        Matcher m = p.matcher(sent);
        while (m.find()) {
          if(sentinel == 0){
        	int begin= 0;
        	int end = 0;
        	if(i - 1 >= 0 ){
        		begin = accu - (sentences.get(i-1).length()+1);
        	}
        	else{
        		begin = 0;
        	}
        	if(i+1 < sentences.size()){
        		end = accu + sent.length() +1+ sentences.get(i+1).length()+1;
        	}
        	else{
        		end = accu + sent.length()+1;
        	}
            //PassageSpan match = new PassageSpan(accu , sent.length() + accu + 1);
        	System.out.println("Begin"+ begin + " end:"+end);
        	System.out.println("i:"+i+"   "+text.substring(begin,end));
        	PassageSpan match = new PassageSpan(begin,end);
            matchedSpans.add(match);          
            sentinel = 1;
          }
          totalMatches++;
          sentenceMatches++;
        }
        if (!matchedSpans.isEmpty()) {
          matchingSpans.add(matchedSpans);
          totalKeyterms++;
        }       
      }
      accu += (sent.length()+1);
    }
    System.out.println("TotalMatches:" + totalMatches + "TotalKeyterms" + totalKeyterms);


    // create set of left edges and right edges which define possible windows.
    HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
    
    List<Integer> leftEdges = new ArrayList<Integer>();
    List<Integer> rightEdges = new ArrayList<Integer>();
    for (List<PassageSpan> keytermMatches : matchingSpans) {
      for (PassageSpan keytermMatch : keytermMatches) {
        Integer leftEdge = keytermMatch.begin;
        Integer rightEdge = keytermMatch.end;
        hash.put(leftEdge, rightEdge);
       if (!leftEdges.contains(leftEdge)){
          leftEdges.add(leftEdge);
        }
        if (!rightEdges.contains(rightEdge)){
          rightEdges.add(rightEdge);
        }
      }
    }
    System.out.println("Hash Map OVER!!!!!!!!!");
    // For every possible window, calculate keyterms found, matches found; score window, and create
    // passage candidate.
//    List<PassageCandidate> result = new ArrayList<PassageCandidate>();
    for (Integer begin : leftEdges) {
      for (Integer end : rightEdges) {
        if (end <= begin)
          continue;
        //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        //if(hash.get(begin) != end)continue;
        if(hash.get(begin).intValue() + 1500 < end.intValue())
        	continue;
        // This code runs for each window.
        int keytermsFound = 0;
        int matchesFound = 0;
        for (List<PassageSpan> keytermMatches : matchingSpans) {
          boolean thisKeytermFound = false;
          for (PassageSpan keytermMatch : keytermMatches) {
            if (keytermMatch.containedIn(begin, end)) {
              matchesFound++;
              thisKeytermFound = true;
            }
          }
          if (thisKeytermFound)
            keytermsFound++;
        }
        double score = scorer.scoreWindow(begin, end, matchesFound, totalMatches, keytermsFound,
                totalKeyterms, textSize);
        PassageCandidate window = null;
        try {
        //  System.out.println("Key Sentence: " + text.substring(begin, begin+3));
        //  System.out.println("Score: " + score);
          window = new PassageCandidate(docId, begin + Getdifference(lengthMap,begin), end+Getdifference(lengthMap,end), (float) score, null);
        } catch (AnalysisEngineProcessException e) {
          e.printStackTrace();
        }
        result.add(window);
      }
    }
    System.out.println("Find Window OVER!!!!!!!!!");
    // Sort the result in order of decreasing score.
    // Collections.sort ( result , new PassageCandidateComparator() );
    return result;

  }

  @SuppressWarnings("unused")
  private class PassageCandidateComparator implements Comparator {
    // Ranks by score, decreasing.
    public int compare(Object o1, Object o2) {
      PassageCandidate s1 = (PassageCandidate) o1;
      PassageCandidate s2 = (PassageCandidate) o2;
      if (s1.getProbability() < s2.getProbability()) {
        return 1;
      } else if (s1.getProbability() > s2.getProbability()) {
        return -1;
      }
      return 0;
    }
  }

  class PassageSpan {
    private int begin, end;

    public PassageSpan(int begin, int end) {
      this.begin = begin;
      this.end = end;
    }

    public boolean containedIn(int begin, int end) {
      if (begin <= this.begin && end >= this.end) {
        return true;
      } else {
        return false;
      }
    }
  }

  public static void main(String[] args) {
    PassageCandidateFinder passageFinder1 = new PassageCandidateFinder("1",
            "The quick brown fox jumped over the quick brown fox.",
            new KeytermWindowScorerProduct());
    PassageCandidateFinder passageFinder2 = new PassageCandidateFinder("1",
            "The quick brown fox jumped over the quick brown fox.", new KentonKeytermWindowScorerSum());
    String[] keyterms = { "quick", "jumped" };
    List<PassageCandidate> windows1 = passageFinder1.extractPassages(keyterms);
    System.out.println("Windows (product scoring): " + windows1);
    List<PassageCandidate> windows2 = passageFinder2.extractPassages(keyterms);
    System.out.println("Windows (sum scoring): " + windows2);
  }

}
