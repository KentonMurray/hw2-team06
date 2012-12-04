package edu.cmu.lti.oaqa.openqa.test.team06.passage;

import edu.cmu.lti.oaqa.openqa.hello.passage.KeytermWindowScorer;

public class KentonKeytermWindowScorerSum implements KeytermWindowScorer{
  
  public double scoreWindow(int begin, int end, int matchesFound, int totalMatches,
          int keytermsFound, int totalKeyterms, int textSize) {
    int windowSize = end - begin;
    double offsetScore = ((double) textSize - (double) begin) / (double) textSize; //Do I care about offset? Maybe a lil
    return ((.25d * (double) matchesFound / (double) totalMatches) + .25d
            * ((double) keytermsFound / (double) totalKeyterms) + .25d
            * (1 - ((((double) windowSize / (double) textSize)) * ((double) windowSize / (double) textSize))) //Squaring to penalize longer windows
            + .25d * offsetScore);
  }
}
