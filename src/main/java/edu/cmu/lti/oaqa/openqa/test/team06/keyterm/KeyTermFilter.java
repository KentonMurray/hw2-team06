package edu.cmu.lti.oaqa.openqa.test.team06.keyterm;

import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermUpdater;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* branch: yiwenche version0.0*/

public class KeyTermFilter extends AbstractKeytermUpdater{
  protected List<Keyterm> updateKeyterms(String question, List<Keyterm> keyterms){
    List<Keyterm> update_list = new ArrayList<Keyterm>();
    Keyterm k;
    int i;
    Pattern filter = Pattern.compile("^[a-z]$|^[0-9]+$");
    Matcher matcher;
    for(i = 0; i < keyterms.size(); i++){
      k = keyterms.get(i);
      matcher = filter.matcher(k.toString());
      if(!matcher.find()){
        update_list.add(k);
      }
    }
    return update_list;
  }
}
