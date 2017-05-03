/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_logic.tweets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import scoobydoo_logic.documents.QuerySearcher;
import scoobydoo_logic.documents.Document;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import scoobydoo_logic.ScoobyMainModel;
import scoobydoo_logic.documents.AnswerItem;
import scoobydoo_logic.documents.Word;

/**
 *
 * @author Administrator
 */
public class TweetQuerySearcher extends QuerySearcher{
    
    private Map<String,Long> AllTwitters;
    private Map<String,Long> AllEvents;
    
    public TweetQuerySearcher(String query, ScoobyMainModel scooby_model, String index_path){
        super(query, scooby_model, index_path);
        AllTwitters = scooby_model.get_AllTwitters();
        AllEvents = scooby_model.get_AllEvents();
    }
    
    @Override
    public Document Load_Document_File(RandomAccessFile document_file, long Index_of_Posting_File) throws IOException{
        
        document_file.seek(Index_of_Posting_File);
        StringTokenizer parser = new StringTokenizer(document_file.readLine(),"\t\n");
        
        /*doc basic attributes*/
        Tweet a_tweet = null;
        int id = -1;
        double norm = -1;
        long byte_length = -1;
        String path = null, type = null;
        /*tweet attributes*/
        String a_twitter = null;
        long next_size = -1;
        
        /*read id of document*/
        if (parser.hasMoreTokens())
                id = (int)Integer.parseInt(parser.nextToken());

        /*if it has not been already inserted read the rest of the info and insert*/
        if (!get_Document_Results().containsKey(id)){
            
            if (parser.hasMoreTokens())
                norm = (double)Double.parseDouble(parser.nextToken());

            if (parser.hasMoreTokens())
                byte_length = (long)Long.parseLong(parser.nextToken());

            if (parser.hasMoreTokens())
                path = parser.nextToken();

            if (parser.hasMoreTokens())
                type = parser.nextToken();
            
            /*create the new tweet with the basic document attributes*/
            a_tweet = new Tweet(id, norm, byte_length, path, type);
            
            /*now continue with the tweet attributes*/
            if (parser.hasMoreTokens()){
                a_twitter = parser.nextToken();
                a_tweet.set_the_tweeter(a_twitter);
            }
            if (parser.hasMoreTokens())
                next_size = (long)Long.parseLong(parser.nextToken());
                for (int i=0; i < next_size; ++i){
                    a_tweet.add_a_retwitter(parser.nextToken());
                }
            if (parser.hasMoreTokens())
                next_size = (long)Long.parseLong(parser.nextToken());
                for (int i=0; i < next_size; ++i){
                    a_tweet.add_a_related_twitter(parser.nextToken());
                }
            if (parser.hasMoreTokens())
                next_size = (long)Long.parseLong(parser.nextToken());
                for (int i=0; i < next_size; ++i){
                    a_tweet.add_an_event(parser.nextToken());
                }
            if (parser.hasMoreTokens())
                next_size = (long)Long.parseLong(parser.nextToken());
                for (int i=0; i < next_size; ++i){
                    a_tweet.add_a_link(parser.nextToken());
                }
            /*end loading*/
            
            get_Document_Results().put(id, a_tweet);
            
            
//            System.out.println("Doc at index line"+Index_of_Posting_File+" D"+a_tweet.get_Id()+" Path: "+a_tweet.get_Path() +
//                    " Type: "+a_tweet.get_Type() +" byte_length: "+a_tweet.get_Byte_Length() + "twitter: "+a_tweet.get_twitter()
//                    );
        }
        
        return get_Document_Results().get(id);
    } 
    
    /**
     * sets all the tweets' scores
     * @param RetrievalModel 
     */
    public void Update_Documents_Score(int RetrievalModel, int significance, double Avgdl){
        Word next_query_word = null;
        
        /*update the similarity based on the significance the user has chosen*/
        for (Map.Entry<String,Word> entry : get_Query_Words().entrySet()){
            next_query_word = entry.getValue();
            Update_Similarity_By_Significance(next_query_word,significance);
        }
        Update_Documents_Score(RetrievalModel, Avgdl);
        /* 8a mporouse na mpei edw kati gia na dinw akomh megalutero 
         * scor sthn periptwsh pou ena query word einai to twitter enos tweet*/
    }
    
    /**
     * method that updates the similarity of the argument query word
     * by a multiplication of its similarity variable depending on the significance
     * @param a_query_word: a query word
     * @param significance: the significance flag chosen by the user
     */
    private void Update_Similarity_By_Significance(Word a_query_word, int significance){
        
        Update_by_Hotness(a_query_word, get_Query_Words_Similarity().get(a_query_word.get_Id()));
        if (significance == 1){
            Update_by_Twitter(a_query_word, get_Query_Words_Similarity().get(a_query_word.get_Id()));
        }
        else if (significance == 2){
            Update_by_Events(a_query_word, get_Query_Words_Similarity().get(a_query_word.get_Id()));
        }
    }
    
    private void Update_by_Hotness(Word a_query_word, double word_similarity){
            get_Query_Words_Similarity().put(a_query_word.get_Id(), word_similarity/* *RTnumber/maxRTnumber */);
    }
    
    private void Update_by_Twitter(Word a_query_word, double word_similarity){
        if ( AllTwitters.containsKey( a_query_word.get_Id())){
            get_Query_Words_Similarity().put(a_query_word.get_Id(), word_similarity*2);
        }
    }
    
    private void Update_by_Events(Word a_query_word, double word_similarity){
        if ( AllEvents.containsKey(a_query_word.get_Id())){
            get_Query_Words_Similarity().put(a_query_word.get_Id(), word_similarity*2);
        }
    }
    
    
    public TreeMap<Integer,AnswerItem> Generate_Hottest_Tweets(ArrayList<Long> positions_by_hotness, int top_num){
        try {
            RandomAccessFile Document_file = new RandomAccessFile( get_Inverted_Index_Path()+"\\DocumentFile.txt", "r");
            
            for (int i=0; i<top_num && i<positions_by_hotness.size(); ++i){
                get_Sorted_Document_Results().put((double)i, Load_Document_File(Document_file, positions_by_hotness.get(i)) );
            }
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(TweetQuerySearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(TweetQuerySearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return GenerateAnswer();
    }
    
    @Override
    public TreeMap<Integer,AnswerItem> GenerateAnswer(){
        
        int index_of_path_name = 0, hit_id = 1;
        Tweet next_tweet = null;
        
        for(Map.Entry<Double, Document> entry : get_Sorted_Document_Results().entrySet()){
            index_of_path_name = entry.getValue().get_Path().lastIndexOf("\\") + 1;
            
            if (entry.getValue() instanceof Tweet){
                next_tweet = (Tweet)entry.getValue();
                
                get_Hits().put(hit_id, new TweetAnswerItem( next_tweet.get_Path().substring(index_of_path_name),
                                                            next_tweet.get_Path().substring(0, index_of_path_name),
                                                            next_tweet.get_Type(),
                                                            next_tweet.get_Score(),
                                                            Generate_Tweet_Text(next_tweet),
                                                            next_tweet.get_twitter(),
                                                            next_tweet.get_retweeters(),
                                                            next_tweet.get_related_twitters(),
                                                            next_tweet.get_events()
                                                          )
                              );
                ++hit_id;
            }
            else{
                System.err.println("Document "+entry.getValue().get_Path()+" is not an instance of Tweet.");
            }
        }
        
        if (get_Sorted_Document_Results().isEmpty())
            get_Hits().put(hit_id, new TweetAnswerItem("Scooby Doo does not have a clue, please try again with different words..."));
        
        return get_Hits();
    }
    
    
    private String Generate_Tweet_Text(Tweet a_tweet){
        BufferedReader reader = null;
        String tweet_text = "", current_Line = null;
        
        try {
            reader = new BufferedReader(new FileReader(new File(a_tweet.get_Path()).getAbsolutePath()));
            while ( (current_Line = reader.readLine()) != null){
                for (String a_link : a_tweet.get_links()){
                    
                    if (current_Line.contains(a_link)){
                        current_Line.replaceAll(a_link, "<a href="+a_link+">"+a_link+"</a>");
                        System.out.println(current_Line);
                    }
                }
                tweet_text += current_Line + "\n";
            }
            reader.close();
        }
        catch (IOException ex) {
            Logger.getLogger(TweetQuerySearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return tweet_text;
    }
}
