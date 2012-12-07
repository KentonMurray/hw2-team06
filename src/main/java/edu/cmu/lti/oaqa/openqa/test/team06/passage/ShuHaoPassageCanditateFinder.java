package edu.cmu.lti.oaqa.openqa.test.team06.passage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

public class ShuHaoPassageCanditateFinder {
  private String text;
  private String docId;

  private int textSize; // values for the entire text

  private int totalMatches;

  private int totalKeyterms;

  private ShuHaoKeytermWindowScorerSum scorer;

  public ShuHaoPassageCanditateFinder(String docId, String text, ShuHaoKeytermWindowScorerSum scorer) {
    super();
    this.text = text;
    this.docId = docId;
    this.textSize = text.length();
    this.scorer = scorer;
  }
  public int Getdifference(TreeMap<Integer,Integer> lengthMap,int key){
		int diff = 0;
		for(Map.Entry<Integer, Integer> entry: lengthMap.entrySet()){
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
  
  public List<PassageCandidate> extractPassages(String[] keyterms) {
    List<List<PassageSpan>> matchingSpans = new ArrayList<List<PassageSpan>>();
    String striptext = RemoveHTMLtag(text);
    String[] sentences = striptext.split("[.?!]"); 
    TreeMap<Integer,Integer> lengthMap = MakeLengthMap(text);
    
    
    List<PassageCandidate> result = new ArrayList<PassageCandidate>();
    int accu = 0;
    int sentinel = 0;
    int sentenceMatches = 0;
    totalMatches = 0;
    totalKeyterms = 0;
    for(int i = 0 ; i < sentences.length ;i++){
   // Find all keyterm matches.
      String sent = sentences[i];
      sentinel = 0;
      sentenceMatches = 0;
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
            		begin = accu - (sentences[i-1].length()+1);
            	}
            	else{
            		begin = 0;
            	}
            	if(i+1 < sentences.length){
            		end = accu + sent.length() +1+ sentences[i+1].length()+1;
            	}
            	else{
            		end = accu + sent.length()+1;
            	}

            	PassageSpan match = new PassageSpan(begin,end);
                matchedSpans.add(match);          
                sentinel = 1;
          }
          totalMatches++;
          sentenceMatches++;
          continue;
        }
        if (!matchedSpans.isEmpty()) {
          matchingSpans.add(matchedSpans);
          totalKeyterms++;
        }
        
//        if(sentenceMatches != 1){
//          double score = scorer.scoreWindow(accu + 1, sent.length() + accu + 1,
//                  sentenceMathces, totalMatches, keytermsFound,
//                  totalKeyterms, textSize);
//          PassageCandidate window = null;
//          try {
//            window = new PassageCandidate(docId, begin, end, (float) score, null);
//          } catch (AnalysisEngineProcessException e) {
//            e.printStackTrace();
//          }
//          result.add(window);
//        }
        
      }
      accu += (sent.length() + 1);
    }
    System.out.println("TotalMatches:" + totalMatches + "TotalKeyterms" + totalKeyterms);
//    // Find all keyterm matches.
//    for (String keyterm : keyterms) {
//      Pattern p = Pattern.compile(keyterm);
//      Matcher m = p.matcher(text);
//      while (m.find()) {
//        PassageSpan match = new PassageSpan(m.start(), m.end());
//        matchedSpans.add(match);
//        totalMatches++;
//      }
//      if (!matchedSpans.isEmpty()) {
//        matchingSpans.add(matchedSpans);
//        totalKeyterms++;
//      }
//    }

    // create set of left edges and right edges which define possible windows.
    HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
    
    List<Integer> leftEdges = new ArrayList<Integer>();
    List<Integer> rightEdges = new ArrayList<Integer>();
    for (List<PassageSpan> keytermMatches : matchingSpans) {
      for (PassageSpan keytermMatch : keytermMatches) {
        Integer leftEdge = keytermMatch.begin;
        Integer rightEdge = keytermMatch.end;
        //System.out.println(leftEdge + " " + rightEdge);
        hash.put(leftEdge, rightEdge);
       if (!leftEdges.contains(leftEdge)){
          leftEdges.add(leftEdge);
        }
        if (!rightEdges.contains(rightEdge)){
          rightEdges.add(rightEdge);
        }
      }
    }

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
          //System.out.println("Key Sentence: " + text.substring(begin, begin+3));
          //System.out.println("Score: " + score);
        	System.out.println("DocID: " + docId + " " + (begin + Getdifference(lengthMap,begin)) + " " + (end + Getdifference(lengthMap,end)) );
          window = new PassageCandidate(docId, begin + Getdifference(lengthMap,begin), end + Getdifference(lengthMap,end), (float) score, null);
        } catch (AnalysisEngineProcessException e) {
          e.printStackTrace();
        }
        result.add(window);
      }
    }

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
            "The quick brown fox jumped over the quick brown fox.", new ShuHaoKeytermWindowScorerSum());
    String[] keyterms = { "quick", "jumped" };
    List<PassageCandidate> windows1 = passageFinder1.extractPassages(keyterms);
    System.out.println("Windows (product scoring): " + windows1);
    List<PassageCandidate> windows2 = passageFinder2.extractPassages(keyterms);
    System.out.println("Windows (sum scoring): " + windows2);
  }

}