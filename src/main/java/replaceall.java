import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;


public class replaceall {
	public static int GetLength(List<HasWord> a){
		int length = 0;
		for(HasWord token:a){
			length += token.toString().length();
		}
		return length;
	}
	public static int FindEnd(String paragraph, int begin,List<HasWord> a, List<HasWord> b){
		String ptr = new String(paragraph.substring(begin));
		int length = begin;
		
		for(HasWord token: a){
	//		System.out.println("ptr: " + ptr);
			int len = ptr.indexOf(token.toString());
			length += (len + token.toString().length());
	//		System.out.println("len: " + len + "string length: " + token.toString().length());
			ptr = ptr.substring(len + token.toString().length());
	//		System.out.println("Token: " + token.toString() + " length:" + length);
		}
		int len = ptr.indexOf(b.get(0).toString());
		return length + len;
	}
	public static void main(String[] args){
		String paragraph = "The outcome of the negotiations is vital\n, because the current tax levels signed into law by President George W. Bush expire on Dec. 31. The Unless Congress acts, tax rates on virtually all Americans who pay income taxes will rise on Jan. 1. That could affect economic growth and even holiday sales.";
		Reader reader = new StringReader(paragraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);

		List<String> sentenceList = new ArrayList<String>();
		Iterator<List<HasWord>> it = dp.iterator();
		int len = 0;
		List<HasWord> sentence;
		String ptr = new String(paragraph);
		String nextptr = new String();
		if(it.hasNext()){
			List<HasWord> next_sentence = it.next();
			while (it.hasNext()) {
				StringBuilder sentenceSb = new StringBuilder();
				sentence = next_sentence;
				next_sentence = it.next();
				//System.out.println("nextptr"+nextptr);
				int begin = ptr.indexOf(sentence.get(0).toString());
				int end = FindEnd(ptr,begin,sentence,next_sentence);
				//int end = nextptr.indexOf(next_sentence.get(0).toString())+length;
				//System.out.println(ptr.substring(begin, end));
				sentenceList.add(ptr.substring(begin, end));
				ptr = ptr.substring(end);
			}
			sentenceList.add(ptr);
		}
		   /*for (HasWord token : sentence) {
			   System.out.println(token + " " + token.toString().length());
			  //if(( paragraph.charAt(len)==' ')&& sentenceSb.length()>1){
			   if(sentenceSb.length()>1){ 
		         sentenceSb.append(" ");
		      }
		      sentenceSb.append(token);
		      len+=token.toString().length();
		   }
		   len++;
		   sentenceList.add(sentenceSb.toString());
		   */
		
		int accu = 0;
		for(int i = 0 ; i < sentenceList.size();i++){
			System.out.println(sentenceList.get(i));
			System.out.println(paragraph.substring(accu,accu+sentenceList.get(i).length()));
			accu = accu + sentenceList.get(i).length();
		}
	}
}
