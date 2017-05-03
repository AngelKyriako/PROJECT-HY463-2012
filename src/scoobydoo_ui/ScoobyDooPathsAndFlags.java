package scoobydoo_ui;

/**
 *
 * @author Angel
 */
public class ScoobyDooPathsAndFlags {
    
    private String DocCollectionPath;
    private String DocIndexPath;
    private String TweetCollectionPath;
    private String TweetIndexPath;
    private int ModelFlag;
    private int TweetModelFlag;
    
    public ScoobyDooPathsAndFlags(){
        this.DocCollectionPath = "";
        this.DocIndexPath = "";
        this.TweetCollectionPath = "";
        this.TweetIndexPath = "";
        this.ModelFlag = 0;
        this.TweetModelFlag = 0;
    }
    
	/*mutators*/
    public void set_Doc_Collection_Path(String a_path){
        this.DocCollectionPath = a_path;
    }
    
    public void set_Tweet_Collection_Path(String a_path){
        this.TweetCollectionPath = a_path;
    }
    
    public void set_Doc_Index_Path(String a_path){
        this.DocIndexPath = a_path;
    }
    
    public void set_Tweet_Index_Path(String a_path){
        this.TweetIndexPath = a_path;
    }
    
    public void set_Model_Flag(int Num){
        this.ModelFlag = Num;
    }
     public void set_Tweet_Model_Flag(int Num){
        this.TweetModelFlag = Num;
    }
    
	/*accessors*/
    public String get_Doc_Collection_Path(){
        return this.DocCollectionPath;
    }
    
    public String get_Tweet_Collection_Path(){
        return this.TweetCollectionPath;
    }
    
    public String get_Doc_Index_Path(){
        return this.DocIndexPath;
    }
    
    public String get_Tweet_Index_Path(){
        return this.TweetIndexPath;
    }
    
    public int get_Model_Flag(){
        return this.ModelFlag;
    }
	
	public int get_TweetModel_Flag(){
        return this.TweetModelFlag;
    }
    
	
	/* photos */
    public String get_scooby(){
        return "resources/images/scooby.jpg";
    }
    
    public String get_scoobygoogle(){
        return "resources/images/Scooby_Doogle.jpg";
    }
    
    public String get_DocSearchButton(){
        return "resources/images/doc_search.jpg";
    }
    
    public String get_TweetSearchButton(){
        return "resources/images/tweet_search.png";
    }    
    
    public String get_initButton(){
        return "resources/images/init.jpg";
    }
    
    public String get_ineetButton(){
        return "resources/images/ineet.jpg";
    }

        /*messages*/
    public final String get_help_text(){
        String ret = "<h1><font color=#0000FF><b>Scooby Doogle FAQ:</font color></h1><br>";
        ret += "<font color=#000000>"
            + "<b>What is Scooby Doogle ?</b><br>"
            + "Scooby Doogle is a search engine based on an inverted index. It was created for the course "
            + "of information retrieval systems of the computer science department of university of Crete.<br><br>"
                
            + "<b>How can I start searching ?</b><br>"                
            + "There are two modes: <i>Documents</i> and <i>Tweets</i> searching. However it is needed to select "
            + "a collection of documents or tweets to be searched for each mode. In addition to that you need to "
            + "select the path where the inverted index is going to be located after the indexing procedure is "
            + "completed. Finally, you can select any inverted index folder generated by the scooby doogle app "
            + "in order to search based on that index. In order to do any of the above, click on the first menu "
            + "button on top of the window.<br><br>"
                
            + "<b>What information retrieving models does Scooby Doogle support?</b><br>"                
            + "Vector space and OKAPI. You can select which one you prefer from the second menu button.<br><br>"                
                
            + "<b>What is the <i>Tweets</i> menu there for?</b><br>"                
            + "For two reasons. The first menu item <i>Significance</i> is there in order to define if extra weight"
            + "is required for the mentions(@) or the hashtags(#) of the tweets. The second one is there to group by"
            + "tweets based on certain requirements.<br><br>"
                                
            + "</font color>";
        return ret;
    }
    
    public final String get_credits_text(){
        String ret = "<h1><font color=#0000FF><b>Scooby Doogle";
        ret += "<h2><font color=#0000FF>Computer Science Department, University of Crete<br>";
        ret += "The course of information retrival systems<br><br>";
        ret += "Creator:<br>";
        ret += "<i><font color=#000000>Angel Kyriakopoulos (akyriak@csd.uoc.gr)";
        
        return ret;
    }
    
    /*top-K hot tweets*/
    public int get_Top_Hot_Tweets_Num(){
        return 10;
    }
}
