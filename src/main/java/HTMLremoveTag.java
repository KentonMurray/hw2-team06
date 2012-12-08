import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

import edu.stanford.nlp.process.DocumentPreprocessor;


public class HTMLremoveTag {
	public static int Getdifference(TreeMap<Integer,Integer> lengthMap,int key){
		int diff = 0;
		for(Map.Entry<Integer,Integer> entry: lengthMap.entrySet()){
			if(key < entry.getKey())
				break;
			diff = entry.getValue();
		}
		return diff;
	}
	  public static String RemoveHTMLtag(String text){
		  	String striptext = text;
			String htmldelimit = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
	        Pattern p = Pattern.compile(htmldelimit);
	        Matcher m = p.matcher(striptext);
	        striptext = text.replaceAll(htmldelimit,"");
	        return striptext;
	  }
	  public static TreeMap<Integer,Integer> MakeLengthMap(String text){
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
	public static void main(String[] args){
		String text = "<tag 123>asdffds<tag<tag2>>home";
		//String text = "abc<kkk>def<gh>ij";
		String text2 = Jsoup.parse(text).text().replaceAll("([\177-\377\0-\32]*)", "");
		TreeMap<Integer,Integer> lengthMap = MakeLengthMap(text);
		

        for(Map.Entry<Integer,Integer> entry: lengthMap.entrySet()){
        	System.out.println(entry.getKey() + " " + entry.getValue());
        }
        for(int i = 0 ; i < 10 ; i++){
        	System.out.println(Getdifference(lengthMap,i));
        }
        System.out.println(lengthMap);
        String striptext = RemoveHTMLtag(text);
        System.out.println("striptext:" + striptext);
        System.out.println("text2:" + text2);
        for(int i = 0 ; i < striptext.length() ;i++){
        	System.out.println(striptext.charAt(i)+ " "+ text.charAt(i+Getdifference(lengthMap,i)));
        }
	//	Reader reader = new StringReader(paragraph);
	//	DocumentPreprocessor dp = new DocumentPreprocessor(reader);
	}
}
