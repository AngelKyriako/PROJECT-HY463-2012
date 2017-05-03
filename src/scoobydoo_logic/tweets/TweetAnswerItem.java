/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_logic.tweets;

import scoobydoo_logic.documents.AnswerItem;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class TweetAnswerItem extends AnswerItem{
    
    private String twitter;
    private ArrayList<String> retwitters;
    private Map<String,Integer> tweeters_related;    
    private Map<String,Integer> events;
    
    public TweetAnswerItem(String text){
        super(text);
    }
    
    public TweetAnswerItem( String a_name, String a_path, String a_type, double a_score, String some_text, String a_twitter,
                            ArrayList<String> some_retwitters, Map<String,Integer> some_tweeters_related, Map<String,Integer> some_events){
        super(a_name, a_path, a_type, a_score, some_text);
        twitter = a_twitter;
        retwitters = some_retwitters;
        tweeters_related = some_tweeters_related;
        events = some_events;
    }
    
    public String get_twitter(){
        return twitter;
    }
    
    public ArrayList<String> get_retwitters(){
        return retwitters;
    }
    
    public Map <String,Integer> get_tweeters_related(){
        return tweeters_related;
    }
    
    public Map <String,Integer> get_events(){
        return events;
    }
    
    @Override
    public String toString(){
          String hits = "";
        
        if (get_score() != -1){
            hits += "<h2><font color=#0000FF>"+get_name()+"</font></h2>";
            if (get_score() != 0)
                hits += "<font color=#0000FF><b>Scored:</font></b><font color=#00000> "+get_score()+"<br>";
            hits += "<font color=#0000FF><b>Type:</font></b><font color=#00000> "+get_type()+"<br>";
            hits += "<font color=#0000FF><b>Location:</font></b> <br><font color=#00000>"+get_path()+"<br>";
            hits += "<font color=#0000FF><b>Take a look:</font></b><br><font color=#00000>"+get_text()+"<br><br>";
        }
        else if (!get_name().isEmpty()){
            hits += "<br><h1><font color=#0000FF>Answers for query:"+get_name()+"</font></h1><br>";
        }
        return hits;
    }      
}
