/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_logic.tweets;

import scoobydoo_logic.tweets.Tweet;
import scoobydoo_logic.documents.DocumentInvertedIndex;
import scoobydoo_logic.documents.Document;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Angel
 */
public class TweetInvertedIndex extends DocumentInvertedIndex{
    
    private TreeMap<Double,Document> TweetsByHotness;
    private TreeMap<String,Integer> all_twitters;
    private TreeMap<String,Integer> all_events;

    //constructor
    public TweetInvertedIndex(String a_path)
    {
        super(a_path);
        TweetsByHotness = new TreeMap<Double,Document>();
        all_twitters = new TreeMap<String,Integer>();
        all_events = new TreeMap<String,Integer>();
    }

    public void insert_a_twitter(String a_twitter){
        if (all_twitters.containsKey(a_twitter)){
            all_twitters.put(a_twitter, all_twitters.get(a_twitter)+1);
        }
        else{
            all_twitters.put(a_twitter, 1);
        }
    }
    
    public void insert_an_event(String an_event){
        if (all_events.containsKey(an_event)){
            all_events.put(an_event, all_events.get(an_event)+1);
        }
        else{
            all_events.put(an_event, 1);
        }
    }

    
    private void Create_Tweet_Positions_By_Hotness_File(File parentDir){
        
        /*create the needed information to the TweetsByHotnessFile*/
        try {
            final File TweetPositionsByHotnessFile = new File(parentDir, "TweetsByHotnessFile.txt");
            TweetPositionsByHotnessFile.createNewFile();
            FileWriter fstream = new FileWriter(TweetPositionsByHotnessFile);
            BufferedWriter out = new BufferedWriter(fstream);
            
            Tweet next_tweet = null;
            for(Map.Entry<Double, Document> entry : TweetsByHotness.entrySet()){

                if (entry.getValue() instanceof Tweet){
                    next_tweet = (Tweet)entry.getValue();
                    //System.out.println(next_tweet.get_Id() + "  RTs: "+ next_tweet.get_retweeters().size());
                    out.write(next_tweet.get_Byte_Position()+"\n");
                }
                else{
                    System.err.println("Problem in <Create_Tweets_By_Hotness_File>, document is not instance of tweet.");
                    System.exit(1);
                }

            }            
            
            out.close();
            
        }catch (IOException e) {
            System.err.println("Exception in <Create_Twitter_File>, TweetInvertedFile.Java:"+ e.getMessage());
        }           
    }
    
    private void Create_Twitter_File(File parentDir){
        try {
            final File TwittersFile = new File(parentDir, "TwittersFile.txt");
            TwittersFile.createNewFile();
            FileWriter fstream = new FileWriter(TwittersFile);
            BufferedWriter out = new BufferedWriter(fstream);
            
            for(Map.Entry<String,Integer> entry : all_twitters.entrySet()){
                out.write(entry.getKey()+"\t"+entry.getValue()+"\n");      
            }
            out.close();
            
        }catch (IOException e) {
            System.err.println("Exception in <Create_Twitter_File>, TweetInvertedFile.Java:"+ e.getMessage());
        }       
    }
    
    private void Create_Events_File(File parentDir){
        try {
            final File EventsFile = new File(parentDir, "EventsFile.txt");
            EventsFile.createNewFile();
            FileWriter fstream = new FileWriter(EventsFile);
            BufferedWriter out = new BufferedWriter(fstream);
            
            for(Map.Entry<String,Integer> entry : all_events.entrySet()){
                out.write(entry.getKey()+"\t"+entry.getValue()+"\n");      
            }
            out.close();
            
        } catch (IOException e) {
            System.err.println("Exception in <Create_Events_File>, TweetInvertedFile.Java:"+ e.getMessage());
        }
    }
    
    //mutators that create the 3 txt files of the tweet inverted index
    private void Create_Documents_File(File parentDir)
    {          
        try{
            final File Documentfile = new File(parentDir, "DocumentFile.txt");
            Documentfile.createNewFile();
            FileWriter fstream = new FileWriter(Documentfile);
            BufferedWriter out = new BufferedWriter(fstream);

            Tweet next_tweet = null;
            long BytesInDocFile = 0;
            double SumofByteLengths = 0; /*used for average of docs' lengths*/
            String temp;
            double next_key = 0;
            
            /*write to document*/
            for(Map.Entry<Integer, Document> entry : get_AllDocuments().entrySet())
            { 
                if (entry.getValue() instanceof Tweet){
                    next_tweet = (Tweet)entry.getValue();
                
                    next_tweet.Calculate_Norm();
                    /*set the position the document has in the document file,
                      it will be used as a posting file's pointer to the doc*/
                    next_tweet.set_Byte_Position(BytesInDocFile);
                    /*keep the doc's info to a string*/
                    temp = next_tweet.get_Id()+"\t"+ next_tweet.get_Norm()+"\t"+next_tweet.get_Byte_Length()+
                                                 /*edw exoume provlhma me ta ellhnika windows !!!*/
                                             "\t"+ new String(next_tweet.get_Path().getBytes(), "UTF-8")+
                                             "\t"+ new String(next_tweet.get_Type().getBytes(), "UTF-8")+"\t";

                    /* extra code needed for the tweet (phase 2)*/

                    //twitter
                    temp += next_tweet.get_twitter()+"\t";
                    //retweeters
                    temp += next_tweet.get_retweeters().size()+"\t";
                    for (String retweeter: next_tweet.get_retweeters()){
                        temp += new String(retweeter.getBytes(), "UTF-8")+"\t";
                    }
                    //related twitters (@)
                    temp += next_tweet.get_related_twitters().size()+"\t";
                    for(Map.Entry<String, Integer> mentioning : next_tweet.get_related_twitters().entrySet()){
                        temp += new String(mentioning.getKey().getBytes(), "UTF-8")+"\t";
                    }
                    // events (#)
                    temp += next_tweet.get_events().size()+"\t";
                    for(Map.Entry<String, Integer> hashtag : next_tweet.get_events().entrySet()){
                        temp += new String(hashtag.getKey().getBytes(), "UTF-8")+"\t";
                    }
                    //links
                    temp += next_tweet.get_links().size()+"\t";
                    for (String link: next_tweet.get_links() ){
                        temp += new String(link.getBytes(), "UTF-8")+"\t";
                    }                
                    temp += "\n";
                    /* tweet code end*/

                    /*write the doc info to document file*/
                    out.write(temp);                
                    /*calculate the position of the next document in the document file*/
                    BytesInDocFile += temp.getBytes().length;
                    /*add the document's length to the sum*/
                    SumofByteLengths += next_tweet.get_Byte_Length();
                    
                    next_key = -1 * next_tweet.get_retweeters().size();
                    while(TweetsByHotness.containsKey(next_key)){
                        next_key -= 0.00000001;
                    }
                    TweetsByHotness.put(next_key, next_tweet);                    
                }
                else{
                    System.err.println("Problem in <CreateDocument>, document is not instance of tweet.");
                    System.exit(1);
                }
            }
            out.close();
            set_Avg_Length(SumofByteLengths / (double)this.get_AllDocuments().size() );/*calculate average of all lengths*/
        }
        catch (Exception e){//Catch exception if any
            System.err.println("Exception in Create Document File, TweetInvertedFile.Java:"+ e.getMessage());
        }            
    }    
    
    @Override
    public void Create_CollectionIndex(String path)
    {
        File parentDir = new File(path+"\\Scooby_Tweet_Inverted_Index");
        parentDir.mkdir();
        
        Calculate_all_TF_IDFs();
        Create_Documents_File(parentDir);        
        Create_Posting_and_Vocabulary_Files(parentDir);
        Create_Tweet_Positions_By_Hotness_File(parentDir);
        Create_Twitter_File(parentDir);
        Create_Events_File(parentDir);
    }    
    
    public TreeMap<String,Integer> get_all_twitters(){
        return all_twitters;
    }
    
    public TreeMap<String,Integer> get_all_events(){
        return all_events;    
    }
}
