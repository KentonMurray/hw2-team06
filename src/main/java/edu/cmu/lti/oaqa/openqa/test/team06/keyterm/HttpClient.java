package edu.cmu.lti.oaqa.openqa.test.team06.keyterm;
/* **
 * This code has been designed/implemented and is maintained by:
 * 
 * Miguel Arregui (miguel.arregui@ebi.ac.uk)
 * 
 * Any comments and/or feedback are welcome and encouraged. 
 * 
 * Started on:    5 May 2006.
 * Last reviewed: 7 June 2006.
 */





import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;

import edu.cmu.lti.oaqa.framework.data.Keyterm;



/***
 * This is a trivial HTTP client that can be used to
 * contact the WHATIZIT pipelines over streamed HTTP.
 * At the other end of the line there is a servlet
 * listening for the following format:
 * 
 *   nameOfAProcessingPipeline
 *   <document xmlns:xlink='http://www.w3.org/1999/xlink' 
 *             xmlns:z='http://www.ebi.ac.uk/z' 
 *             source='Whatizit'><text>
 *     Your text comes here, it can be as long as you 
 *     need and can contain any XML-like tags (except 
 *     </document>). Only blocks of text wrapped with
 *     the "<text> ... </text>" tags will be processed.
 *     There can be as many of these blocks as you need.     
 *   </text></document>
 *   
 * For the sake of clarity the syntax rule follows:
 * 
 *    nameOfAProcessingPipeline
 *   <document xmlns:xlink='http://www.w3.org/1999/xlink' 
 *             xmlns:z='http://www.ebi.ac.uk/z' 
 *             source='Whatizit'>
 *             
 *             (<text> your text </text>)+
 *             
 *   </document>
 * 
 * The first line must contain the name of a pipeline
 * followed by a linefeed. The subsequent blocks of 
 * text must be wrapped with the <text> tags. All of 
 * the text blocks must be wrapped with the <document> 
 * tags. 
 * 
 * The life cycle of this class is as follows:
 * 
 *   HttpClient client = new HttpClient();
 *   client.upload(System.in);
 *   client.download(System.out);
 *   client.close();
 *   
 * Note: To invoque the client set the "http.keepAlive"
 * parameter to false:  -Dhttp.keepAlive=false  
 */
public class HttpClient {
	
	// URL of the processing servlet
  public static final String SERVER_URL 
    = "http://www.ebi.ac.uk/webservices/whatizit/pipe";     
  
  // The actual size of the chunks sent/received to/from the servlet
  protected static final int BUFFER_SIZE = 502; 
  protected byte [] buffer;
  protected boolean uploadDone;
  protected boolean downloadDone;
  protected URL url;
  protected HttpURLConnection conn;
  
  
  
  public HttpClient () throws MalformedURLException, IOException {
  	this(SERVER_URL);
  }
  
  
  public HttpClient (String urlStr) throws MalformedURLException, IOException {
  	uploadDone = false;
    downloadDone = false;
    buffer = new byte [BUFFER_SIZE];
  	this.url = new URL(urlStr);  	
  	conn = (HttpURLConnection)this.url.openConnection();  	  
    conn.setRequestMethod("POST");
    conn.setUseCaches(false);    
    conn.setDoInput(true);
    conn.setDoOutput(true);                   
    conn.setRequestProperty("Content-Type", "UTF-8");
    conn.setRequestProperty("Transfer-Encoding", "chunked");     
    conn.setChunkedStreamingMode(BUFFER_SIZE);
    conn.connect();
  }    
  
    
  public void upload (InputStream in, List<Keyterm> returnedKeyterms) throws IOException {  	
  	if (uploadDone) throw new IOException("Upload done already.");
  	OutputStream out = conn.getOutputStream();
  	readFromTo(in, out, returnedKeyterms);
    out.close();
    System.out.println("uploading");
    uploadDone = true;  	
  }
  
  
  public void download (OutputStream out, List<Keyterm> returnedKeyterms) throws IOException {
  	if (downloadDone) throw new IOException("Download done already.");
  	InputStream in = conn.getInputStream();
  	readFromTo(in, out, returnedKeyterms);
  	in.close();
    downloadDone = true;
  }
  
  
  public void close (){
  	conn.disconnect();
  }
  
  public void readKeyterms(List<Keyterm> returnedKeyterms){
    BufferedReader file = null;
    try {
      file = new BufferedReader(new FileReader("letsee.txt"));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String line;
    try {
      while ((line = file.readLine()) != null) {
        if (line != null) {
          //System.out.println(line);
          if(line.contains("z:uniprot")){
            String token[] = line.split("<z:un");
            for(int k = 0; k< token.length; k++){
              //System.out.println("lines for protein: " + token[k]);
              if(token[k].contains("iprot>")){
               String t1 = token[k].substring(token[k].indexOf(">")+1, token[k].indexOf("</z:uniprot>"));
               System.out.println("protein: "+ t1);
               
               Keyterm Vterm = new Keyterm(t1);//.originalText());
               Vterm.setProbablity((float)0.8);
               returnedKeyterms.add(Vterm);
              }
            } 
          }
          
          if(line.contains("<z:e sem=") && line.contains("disease")){
            String token1[] = line.split("<z:e");
           for(int k = 0; k< token1.length; k++){
             //System.out.println("lines for disease: " + token1[k]);
             if(token1[k].contains("disease")){
             String s1 = token1[k].substring(token1[k].indexOf(">")+1, token1[k].indexOf("</z:e>"));
             System.out.println("disease: " + s1);
             
             Keyterm Dterm = new Keyterm(s1);
             Dterm.setProbablity((float)0.5);
             returnedKeyterms.add(Dterm);
             }
           } 
          }
       }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  
  protected void readFromTo (InputStream in, OutputStream out, List<Keyterm> returnedKeyterms) 
  throws IOException {  	
  	int bread = 0;
    while (-1 != (bread=in.read(buffer, 0, BUFFER_SIZE))){
      FileOutputStream newOut = new FileOutputStream("letsee.txt");  
      out.write(buffer, 0, bread);
      newOut.write(buffer, 0, bread);
      readKeyterms(returnedKeyterms);
      out.flush();
      newOut.close();
  	}  	
  }  
       
    
//  public void proteinDiseaseAnnotation (String args){  	
//  	try {
//      String serverUrl = (args.length() == 1)? args : SERVER_URL;
//  	  HttpClient client = new HttpClient(serverUrl);
//  	  String s = "whatizitProteinDiseaseUMLS\n <document xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:z='http://www.ebi.ac.uk/z' source='Whatizit'><text>"+ args + "</text></document>";
//  	  byte[] b = s.getBytes("US-ASCII");
//  	  InputStream in = new ByteArrayInputStream(b);
//  	  client.upload(in);
//  	  client.download(System.out);
//  	  client.close();
//  	}
//  	catch (IOException e){
//  		e.printStackTrace();
//  		System.err.println(e.getMessage());
//  	}
//  }
}
// Eof - Miguel Arregui (miguel.arregui@ebi.ac.uk)