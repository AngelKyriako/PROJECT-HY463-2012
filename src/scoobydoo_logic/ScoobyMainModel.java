/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_logic;

import java.util.logging.Level;
import java.util.logging.Logger;
import scoobydoo_logic.tweets.TweetInvertedIndex;
import scoobydoo_logic.documents.Word;
import scoobydoo_logic.tweets.url_manager;
import scoobydoo_logic.tweets.TweetQuerySearcher;
import scoobydoo_logic.tweets.Tweet;
import scoobydoo_logic.documents.QuerySearcher;
import scoobydoo_logic.documents.DocumentInvertedIndex;
import scoobydoo_logic.documents.Document;
import scoobydoo_logic.documents.AnswerItem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import scoobydoo_logic.tweets.TweetAnswerItem;

/**
 *
 * @author Angel
 * 
 * includes...
 * 
 * constructor
 * accessors
 * string filter
 * methods for indexing
 * methods for loading
 * method for searching
 */
public class ScoobyMainModel {
    
    private DocumentInvertedIndex ScoobyDocIndex;
    private TweetInvertedIndex ScoobyTweetIndex;
    private HashMap <String, Integer> Stop_Words;    
    private Map<String,Word> Vocabulary;
    private ArrayList<Long> TweetPositionsbyHotness;
    private Map<String,Long> AllTwitters;
    private Map<String,Long> AllEvents;    
    
    private double Avg_Doc_Length;
    private static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");
    
    public ScoobyMainModel(){
        ScoobyDocIndex = null;
        ScoobyTweetIndex = null;
        Stop_Words =  new HashMap<String, Integer>();         
        Vocabulary = new TreeMap<String,Word>();
        TweetPositionsbyHotness = new ArrayList<Long>();
        AllTwitters = new TreeMap<String,Long>();
        AllEvents = new TreeMap<String,Long>();
    }
    
    public DocumentInvertedIndex get_ScoobyDocIndex(){
        return ScoobyDocIndex;
    }
    
    public TweetInvertedIndex get_ScoobyTweetIndex(){
        return ScoobyTweetIndex;
    }
    
    public Map<String,Word> get_Vocabulary(){
        return Vocabulary;
    }
    
    public Map<String,Long> get_AllTwitters(){
        return AllTwitters;
    }
    
    public Map<String,Long> get_AllEvents(){
        return AllEvents;
    }
    
    
    private static String stripDiacritics(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = DIACRITICS_AND_FRIENDS.matcher(str).replaceAll("");
        
        return str;
    }
    
    public String Filter_word(String a_word , boolean is_search_filtering){
        if( a_word.length() > 1 && !a_word.matches(".*\\d.*")){ /*ignore words less than 2 characters and numbers*/
            a_word = a_word.toLowerCase();
            
            if (a_word.isEmpty() || a_word.contains("\\s") || a_word.equals("-")){
                return null;
            }
            else if (a_word.contains("http://")){
                
                int first_link_index = a_word.indexOf(a_word)+1;
                int last_link_index = -1;
                for (int i=a_word.length()-1; i>=first_link_index; --i){
                    if(Character.isDigit(a_word.charAt(i)) || Character.isLetter(a_word.charAt(i))){
                        last_link_index = i+1;
                        
                        if (a_word.startsWith("http://")){
                            //System.out.println(a_word.substring(0, last_link_index));
                            return a_word.substring(0, last_link_index);
                        }
                        else{
                            //System.out.println(a_word.substring(first_link_index, last_link_index));
                            return a_word.substring(first_link_index, last_link_index);
                        }
                    }
                }
            }
            else if (is_search_filtering || !Stop_Words.containsKey(a_word)){                
                try {
                    return stripDiacritics(new String(a_word.getBytes(), "UTF-8"));
                }
                catch (UnsupportedEncodingException ex) {
                    System.err.println("UnsupportedEncodingException in Filter word "+ex.getMessage() );
                    return null;
                }
            }
        }
        return null;
    }
    
    /**
     * loop all text files on the 
     * argument path and call Read_File
     */
    private void Read_Folder(String path, boolean isTweet)
    {
        File folder = new File(path);
        
        File[] listOfFiles = folder.listFiles(); 
        JFileChooser chooser = new JFileChooser();
        for (int i = 0; i < listOfFiles.length; ++i) 
        {
            if (listOfFiles[i].isFile()){
                try {
                    if (isTweet){
                        Read_File(listOfFiles[i], new String(chooser.getTypeDescription(listOfFiles[i]).getBytes(), "UTF-8"), -1);                        
                    }
                    else{
                        Read_File(listOfFiles[i], new String(chooser.getTypeDescription(listOfFiles[i]).getBytes(), "UTF-8"), 0);
                    }
                } 
                catch (UnsupportedEncodingException ex) {
                    System.err.println("Unsupported Encoding exception at Type "+ex.getMessage());
                }
            }
            else if (listOfFiles[i].isDirectory()){
                System.out.println("Entered subfolder: " + listOfFiles[i].getName()); /*test entering all subfolders*/
                Read_Folder(listOfFiles[i].getAbsolutePath(), isTweet);
            }
            else{
                System.err.println("Folder or Directory ACCESS DENIED");
            }
        }
    }    
    
    private void Read_File(File a_file, String File_Type, int flag){

        if (flag != -1 && flag != 0 && flag != 1 && flag != 2 && flag != 3 && flag != 4 && flag != 5)
            throw new IllegalArgumentException("Not recognized flag: \""+ flag +"\"");
 
        try
        {                        
            BufferedReader br = new BufferedReader(new FileReader(a_file.getAbsolutePath()));
            
            if(flag == -1 && (File_Type.equals("Text Document") || File_Type.equals("File")))
                Read_a_Tweet(a_file, File_Type, br);
            else if (flag == 0 && (File_Type.equals("Text Document") || File_Type.equals("File")))
                Read_a_Document(a_file, File_Type, br);                
            else if (flag == 1)
                Init_StopWords(a_file, br);
            else if (flag == 2)
                Load_Vocabulary_File(br);
            else if (flag == 3)
                Load_Twitter_Event_File(br, true);
            else if (flag == 4)
                Load_Twitter_Event_File(br, false);
            else if (flag == 5)
                Load_Tweet_Positions_by_Hotness_File(br);
            else
                System.err.println("unrecognized flag: \""+ flag +"\" or not supported type of file.");
            br.close(); 
        } 
        catch (FileNotFoundException e) { 
            System.err.println("File not found exception in Read File "+e.getMessage() );
        }
        catch (IOException e) {
            System.err.println("IO exception in Read File "+e.getMessage() );
        }              
    }
    /**
     * completely reads and indexes every word of a tweet
     * @param a_file: a file
     * @param File_Type: the type of the file
     * @param reader: a already initialized reader that will read the file 
     */      
    private void Read_a_Tweet(File a_file, String File_Type, BufferedReader reader){
        
         try {
            Tweet next_tweet = new Tweet(ScoobyTweetIndex.get_AllDocuments().size(),
                                    a_file.getAbsolutePath(),
                                    File_Type);            

            Word w = new Word();
            String currentLine = null, tweet_text = "", original_tweeter = null;
            long ByteLength = 0;
            boolean is_a_retweet = false;
            int twitter_mode = 0; // 0: before twitter  1: after twitter, before RT  2: after : or -, at main tweet
            while ((currentLine = reader.readLine())!= null)
            {              
                String[] parser = currentLine.split("\\s+");//"[\\p{Punct}\\s«»’—•°··‘]+"
                for(String currentWord : parser){
                    
                    if ((currentWord = Filter_word(currentWord, false)) == null )
                        continue;
                    
                    if(currentWord.charAt(0)=='#')
                    {
                        next_tweet.add_an_event(currentWord.substring(1) );
                        
                        ScoobyTweetIndex.insert_an_event(currentWord.substring(1) );
                        ScoobyTweetIndex.Insert_Word(currentWord.substring(1), next_tweet, ByteLength);
                    }                    
                    else if(currentWord.charAt(0)=='@')
                    {
                        if (twitter_mode == 2){// just mention of a twitter
                            next_tweet.add_a_related_twitter(currentWord.substring(1) );
                            
                            ScoobyTweetIndex.insert_a_twitter(currentWord.substring(1) ); 
                            ScoobyTweetIndex.Insert_Word(currentWord.substring(1), next_tweet, ByteLength);
                        }
                        else if (twitter_mode == 0){ // the twitter of the tweet
                            next_tweet.set_the_tweeter(currentWord.substring(1) );
                            
                            ScoobyTweetIndex.insert_a_twitter(currentWord.substring(1) );
                            ScoobyTweetIndex.Insert_Word(currentWord.substring(1), next_tweet, ByteLength);
                            twitter_mode = 2;                            
                        }
                        else if (twitter_mode == 1){ // the original twitter of the Retweet                       
                            original_tweeter = currentWord.substring(1, currentWord.length()-1);
                            
                            ScoobyTweetIndex.insert_a_twitter(currentWord.substring(1, currentWord.length()-1) );
                            ScoobyTweetIndex.Insert_Word(currentWord.substring(1), next_tweet, ByteLength);
                            is_a_retweet = true;                                 
                            twitter_mode = 2;
                        }                     
                    }
                    else if(currentWord.equals("rt"))
                    {
                        twitter_mode = 1;
                    }
                    else if(currentWord.startsWith("http://"))
                    {
                        next_tweet.add_a_link(currentWord);
                        //manage_url(currentWord, next_tweet, ByteLength);
                    }
                    else if (twitter_mode == 2){
                        ScoobyTweetIndex.Insert_Word(currentWord, next_tweet, ByteLength);
                        tweet_text += currentWord;
                    }
                    else{
                        System.err.println("Required twitter mode=2 but it is="+twitter_mode+"Retweet was expected but it was not one!!!");                      
                        twitter_mode = 2;
                    }
                }
                ByteLength += currentLine.getBytes().length+2;/*add the bytes to the position counter*/
            }
            /*We hate empty tweets so we toss them out*/
            if (!tweet_text.isEmpty()){
                next_tweet.set_Byte_Length(a_file.length());/*set the byte length of the document...it will be used for okapi retrieval model*/
                next_tweet.set_text(tweet_text);
                if (!is_a_retweet)
                    ScoobyTweetIndex.Insert_Document(next_tweet);
                else
                    manage_retweet(original_tweeter, next_tweet);
            }
        }
        catch (IOException ex) {
            System.err.println("IO exception in Read a Document "+ex.getMessage() );
        }
    }
    
    private void manage_retweet(String original_twitter, Tweet a_tweet){
        Tweet original_tweet;
                
        if ((original_tweet = get_original_tweet(original_twitter, a_tweet.get_text())) != null){
            // the twitter of this tweet is the retwitter of the original one
            original_tweet.add_a_retwitter(a_tweet.get_twitter());
        }
        else{
            ScoobyTweetIndex.Insert_Document(a_tweet);
        }
    }
    
    private Tweet get_original_tweet(String twitter, String tweet_text){
        
        Tweet next_tweet = new Tweet();
        
        for(Map.Entry <Integer,Document> entry : ScoobyTweetIndex.get_AllDocuments().entrySet()){
            
            if (entry.getValue() instanceof Tweet){
                next_tweet = (Tweet)entry.getValue();
                if (next_tweet.get_twitter().equals(twitter) && next_tweet.get_text().equals(tweet_text))
                    return next_tweet;
            }
            else{
                System.err.println("Problem in <get_original_tweet>...entry is not instance of Tweet");     
            }
        }
        return null;
    }
      
    private void manage_url(String s, Tweet current_tweet, long byte_length) throws MalformedURLException, IOException{
        if(get_title_words(s).size()>0)
        {
            for (String a_title_word : get_title_words(s)){
                if(a_title_word!=null){
                ScoobyTweetIndex.Insert_Word(a_title_word, current_tweet, byte_length);}
            }
        }else{ScoobyTweetIndex.Insert_Word(null, current_tweet, byte_length);}  
    }  
    
   public ArrayList<String> get_title_words(String s) throws MalformedURLException, IOException{
        
        ArrayList<String> title_of_page = new ArrayList();
        url_manager manager = new url_manager();          
        URL myUrl = null;
        try {
            myUrl = new URL(s);
        } catch (MalformedURLException ex) {
            System.err.println("Something is wrong in <Read_a_Tweet> required twitter mode = 2");
            return null;                        
        }
        
        
        HttpURLConnection urlConnection = (HttpURLConnection)new URL(s).openConnection();
       
        String location = urlConnection.getHeaderField("Location");
        System.out.println("location "+location);
        if(location==null)
        {
                 
              String t = manager.GetTitle(myUrl);
              title_of_page =  manager.get_title(t);
      
              return title_of_page;
        }
        else{
             urlConnection.setInstanceFollowRedirects(true);
             System.out.println("Response code is " + urlConnection.getResponseCode());
        }
        
        return null;
    }
    /**
     * completely reads and indexes every word of a document
     * @param a_file: a file
     * @param File_Type: the type of the file
     * @param reader: a already initialized reader that will read the file 
     */
    private void Read_a_Document(File a_file, String File_Type, BufferedReader reader)
    {
        try {
            
            Document doc = new Document(ScoobyDocIndex.get_AllDocuments().size(),/*every previous size is the next document id*/
                                                a_file.getAbsolutePath(),
                                                File_Type
                                                );
            ScoobyDocIndex.Insert_Document(doc);             
            
            String currentLine = null;
            long ByteLength = 0;
            while ((currentLine = reader.readLine())!= null)
            {
                String[] parser = currentLine.split("[\\p{Punct}\\s«»’—•°··‘]+");
                
                for(String currentWord : parser){                        
                    if ((currentWord = Filter_word(currentWord, false)) != null )
                        ScoobyDocIndex.Insert_Word(currentWord, doc, ByteLength);
                }
                //System.out.println(currentLine+"  byte length: "+currentLine.getBytes().length+"  normal length: "+ currentLine.length()); /*test print*/            
                ByteLength += currentLine.getBytes().length+2;/*add the bytes to the position counter*/
            }
            doc.set_Byte_Length(a_file.length());/*set the byte length of the document...it will be used for okapi retrieval model*/
        }
        catch (IOException ex) {
            System.err.println("IO exception in Read a Document "+ex.getMessage() );
        }
    }
   
    
    private void Load_Vocabulary_File(BufferedReader reader) throws IOException
    {
        String id = null, currentLine = null;
        double idf = 0;
        long df = 0, posting_file_pointer = 0;
        
        /*First load the average length of all the documents*/            
        Avg_Doc_Length = (double)Double.parseDouble(reader.readLine());
        while ((currentLine = reader.readLine()) != null) 
        {                 
            StringTokenizer parser = new StringTokenizer(currentLine,"\t\n");
            while (parser.hasMoreTokens()) 
            {   /*read every field of word object in the line */                   

                try{
                    if (parser.hasMoreTokens())
                        id = parser.nextToken();
                    if (parser.hasMoreTokens())
                        df = (long)Long.parseLong(parser.nextToken());
                    if (parser.hasMoreTokens())
                        idf = (double)Double.parseDouble(parser.nextToken());
                    if (parser.hasMoreTokens())
                        posting_file_pointer = (long)Long.parseLong(parser.nextToken());
                    /*add the word in the vocabulary*/
                    this.Vocabulary.put(id, new Word(id, df, idf, posting_file_pointer));
                }
                catch(Exception e){
                    System.err.println("Error while Loading Vocabulary File "+e.getMessage());
                }
                //System.out.println(id+" "+df+" "+idf+" "+posting_file_pointer);/*test print*/
            } 
        }
    }
    
    private void Load_Tweet_Positions_by_Hotness_File(BufferedReader reader){
        try {
            String currentLine = null;
            int next_index=0;
            while ((currentLine = reader.readLine()) != null) 
            {                 
                TweetPositionsbyHotness.add(next_index++,Long.parseLong(currentLine));
            }
        }catch (IOException ex) {
            Logger.getLogger(ScoobyMainModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    private void Load_Twitter_Event_File(BufferedReader reader, boolean isTwitterFile) throws IOException
    {
        String id = null, currentLine = null;
        long popularity = 0 ;
        
        while ((currentLine = reader.readLine()) != null) 
        {                 
            StringTokenizer parser = new StringTokenizer(currentLine,"\t\n");
            while (parser.hasMoreTokens()) 
            {   /*read every field of word object in the line */                   

                try{
                    if (parser.hasMoreTokens())
                        id = parser.nextToken();
                    if (parser.hasMoreTokens())
                        popularity = (long)Long.parseLong(parser.nextToken());
                    
                    if (isTwitterFile)
                        AllTwitters.put(id, popularity);
                    else
                        AllEvents.put(id, popularity);
                }
                catch(Exception e){
                    System.err.println("Error while Loading Twitter/Event File "+e.getMessage());
                }
            }
        }        
    }

    private void Init_StopWords(File a_file, BufferedReader reader) 
                                throws UnsupportedEncodingException, IOException
    {
        String currentLine = null;
        while ((currentLine = reader.readLine()) != null) 
        {                 
            currentLine = currentLine.toLowerCase(); 
            StringTokenizer parser = new StringTokenizer(currentLine," \t\n\r\f"); 
            while (parser.hasMoreTokens()) 
            {                      
                String word = parser.nextToken(); 
                Stop_Words.put(word, 1);
            } 
        }
    }
    
    public void Manage_Indexing(String IndexingPath, String CollectionPath, boolean isTweet){
        
        double start_time = System.nanoTime();        
        
        File stopwords_file = new File("resources\\stopwords.txt");
        if (stopwords_file.exists())
            Read_File(stopwords_file, "Text Document", 1);
        else
            System.err.println("Whoops, wrong stopwords file path.");
        
        if (isTweet){
            ScoobyTweetIndex = new TweetInvertedIndex(IndexingPath);
            Read_Folder(CollectionPath, isTweet);
            ScoobyTweetIndex.Create_CollectionIndex(ScoobyTweetIndex.get_Path());            
        }
        else{
            ScoobyDocIndex = new DocumentInvertedIndex(IndexingPath);
            Read_Folder(CollectionPath, isTweet);
            ScoobyDocIndex.Create_CollectionIndex(ScoobyDocIndex.get_Path());
        }
        
        /*
        //create file for countings
        File a_file = null;
        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            a_file = new File("indexing_time_count_file.txt");
            fstream = new FileWriter(a_file, true);
            out = new BufferedWriter(fstream);        
            out.write( (System.nanoTime() - start_time)* Math.pow(10, -9) + "\n");
            out.close();      
        }catch (IOException ex) {
            Logger.getLogger(DocumentInvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        */
    }
    
    public void Manage_Loading(String inverted_index_path, boolean is_tweet_index){
        File VocabularyFile = new File(inverted_index_path+"\\VocabularyFile.txt");
        File TwittersFile = new File(inverted_index_path+"\\TwittersFile.txt");
        File EventsFile = new File(inverted_index_path+"\\EventsFile.txt");
        
        if (is_tweet_index)
            if (VocabularyFile.exists() && TwittersFile.exists() && EventsFile.exists()){
                Read_File(VocabularyFile, "Text Document", 2);
                Read_File(TwittersFile, "Text Document", 3);
                Read_File(EventsFile, "Text Document", 4);            
            }
            else{
                System.out.println("tweet inverted index");
            }
        else
            if (VocabularyFile.exists()){
                Read_File(VocabularyFile, "Text Document", 2);         
            }
            else{
                System.out.println("document inverted index");
            }
    }
    
    public TreeMap<Integer, AnswerItem> Manage_Searching(String query, int Model, int significance, String IndexPath, boolean is_tweet)
    {
        TreeMap<Integer, AnswerItem> result = null;
        double start_time = System.nanoTime();

        if (is_tweet){
            TweetQuerySearcher ScoobyTweet_Searcher = new TweetQuerySearcher(query, this, IndexPath);
            ScoobyTweet_Searcher.Load_Everything();
            ScoobyTweet_Searcher.Update_Documents_Score(Model, significance, Avg_Doc_Length);
            result = ScoobyTweet_Searcher.GenerateAnswer();        
        }
        else{
            QuerySearcher ScoobySearcher = new QuerySearcher(query, this, IndexPath);
            ScoobySearcher.Load_Everything();
            ScoobySearcher.Update_Documents_Score(Model, Avg_Doc_Length);
            result = ScoobySearcher.GenerateAnswer();
        }    
        /*
        //create file for countings
        File a_file = null;
        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            a_file = new File("searching_countings_file.txt");
            fstream = new FileWriter(a_file, true);
            out = new BufferedWriter(fstream);        
            out.write( new DecimalFormat("#.######").format( (System.nanoTime() - start_time)* Math.pow(10, -9) ) + "   \t" + ScoobySearcher.get_Query_Words_Num()+"\t\t");
            out.write( ScoobySearcher.get_Doc_Results_Num() + "\t" + ScoobySearcher.get_Query_Words_Num()+"\n");
            out.close();      
        }catch (IOException ex){
            Logger.getLogger(DocumentInvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        return result;
    }
    /**
     * GroupBy menu items' functionality
     * @param search_item: the label the user picked, a hash tag or a twitter.
     * @param IndexPath: the path of the inverted index
     * @param group_by_flag: the flag of what version of the group by functionality the user wants(1:twitter, 2:relevant twitter, 3:event)
     * @return: the results after the necessary deletions have been occured.
     */
    public TreeMap<Integer,AnswerItem> Manage_Group_By(String search_item, String IndexPath, int group_by_flag){
        
        if (group_by_flag != 1 && group_by_flag != 2 && group_by_flag != 3){
            throw new IllegalArgumentException("Not recognized group_by_flag: \""+ group_by_flag +"\"");
        }
        
        TreeMap<Integer,AnswerItem> tweet_answer = Manage_Searching(search_item, 0, 1, IndexPath, true);
        
//        for(Map.Entry<Integer,AnswerItem> entry : tweet_answer.entrySet()){
//            
//            if (entry.getValue() instanceof TweetAnswerItem){
//                TweetAnswerItem next_tweet_answer = (TweetAnswerItem)entry.getValue();
//                
//                if ((group_by_flag == 1) && (!next_tweet_answer.get_twitter().equals(search_item))){
//                    tweet_answer.remove(entry.getKey());    
//                }
//                else if((group_by_flag == 2) && (!next_tweet_answer.get_tweeters_related().containsKey(search_item))){
//                    tweet_answer.remove(entry.getKey()); 
//                }
//                else if((group_by_flag == 3) && (!next_tweet_answer.get_events().containsKey(search_item))){
//                    tweet_answer.remove(entry.getKey()); 
//                }
//            }
//            else{
//                System.err.println("In Manage_Group_By, AnswerItem "+entry.getValue().get_name()+
//                                                        " is not an instance of TweetAnswerItem.");
//                tweet_answer.remove(entry.getKey());
//            }
//        }
        return tweet_answer;
    }
    
    public TreeMap<Integer,AnswerItem> Manage_Hot_Tweets(String inverted_index_path, int top_num){
        File TweetsByHotnessFile = new File(inverted_index_path+"\\TweetsByHotnessFile.txt");
        if (TweetsByHotnessFile.exists()){
            Read_File(TweetsByHotnessFile, "Text Document", 5);
            
            TweetQuerySearcher ScoobyTweet_Searcher = new TweetQuerySearcher("", this, inverted_index_path);
            return ScoobyTweet_Searcher.Generate_Hottest_Tweets(TweetPositionsbyHotness, top_num);
        }
        else{
//            System.out.println("Gamw");
            return null;
        }
    }
    

}
