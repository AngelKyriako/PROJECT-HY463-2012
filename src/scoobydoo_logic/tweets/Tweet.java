/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_logic.tweets;

import scoobydoo_logic.documents.Document;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Angel
 */
public class Tweet extends Document{

    private String twitter; //the person that tweeted this message (first @)          
    private ArrayList<String> retwitters;//all the twitters that retweeted it (RT)    
    private Map <String,Integer> events;//all the hashtag words in this tweet (#)   // na dw ti 8a valw gia value
    private Map <String,Integer> tweeters_related;//all the users related to this tweet (@)
    private String text;
    private ArrayList<String> links;

    //constructors
    public Tweet(){}//default constructor
    
    public Tweet(int id, String path, String type){
        super(id,path, type);
        twitter = null;
        retwitters = new ArrayList(10);
        events = new HashMap<String,Integer>(10);
        tweeters_related = new HashMap<String,Integer>(10);
        text = null;
        links = new ArrayList(3);
    }
    
    public Tweet(int id, double norm, long byte_length, String path, String type){
        super(id, norm, byte_length, path, type);
        twitter = null;
        retwitters = new ArrayList(10);
        events = new HashMap<String,Integer>(10);
        tweeters_related = new HashMap<String,Integer>(10);
        text = null;
        links = new ArrayList(3);
    }        
    
    /*mutators*/
    public void set_the_tweeter(String a_twitter){
        twitter = a_twitter;
    }

    public void add_a_retwitter(String a_twitter){
        retwitters.add(a_twitter);
    }
    
    public void add_an_event(String an_event){
        events.put(an_event, events.size()+1);
    }
    
    public void add_a_related_twitter(String a_twitter){
        tweeters_related.put(a_twitter, tweeters_related.size()+1);
    }
       
    public void set_text(String some_string){
        text = some_string;
    }    
    
    public void add_a_link(String a_link){
        links.add(a_link);
    }
    
    /*accessors*/
    public String get_twitter(){
        return twitter;
    }
    
    public ArrayList<String> get_retweeters(){
        return retwitters;
    }
    
    public Map<String,Integer> get_events(){
        return events;
    }
    
    public Map<String,Integer> get_related_twitters(){
        return tweeters_related;
    }
    
    public String get_text(){
        return text;
    }
    
    public ArrayList<String> get_links(){
        return links;
    }

}
