/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_ui;

import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 *
 * @author Angel
 */
public class ScoobyMenu extends JMenuBar{
    
    private JMenuItem DocCollectionItem;
    private JMenuItem TweetCollectionItem;
    private JMenuItem DocInvertedIndexItem;
    private JMenuItem TweetInvertedIndexItem;
    
    private JRadioButtonMenuItem VectorSpace;
    private JRadioButtonMenuItem OKAPI;
    
    private JRadioButtonMenuItem no_significance;
    private JRadioButtonMenuItem AT;       
    private JRadioButtonMenuItem HashTag;
    
    private JMenuItem Hottest;
    private JMenuItem GroupBy_Twitter;
    private JMenuItem GroupBy_Event;
    
    private JMenu Help;
    private JMenu Credits;
    
    private JFileChooser DocCollectionChooser;
    private JFileChooser TweetCollectionChooser;
    private JFileChooser DocIndexChooser;
    private JFileChooser TweetIndexChooser;
    
    public ScoobyMenu(){
        super();
        
        /*Add them both to the Select Paths Menu*/
        JMenu Select_Paths = new JMenu("Select Paths");
        Select_Paths.addSeparator();        
        
        /*menu for paths of collections*/
        JMenu Document_Paths = new JMenu("Document Paths");
        Document_Paths.addSeparator();
        DocCollectionItem = new JMenuItem("Document Collection Folder");
        Document_Paths.add(DocCollectionItem);
        DocInvertedIndexItem = new JMenuItem("Document Inverted Index Folder");
        Document_Paths.add(DocInvertedIndexItem);
        
        /*menu for paths of inverted indexes*/
        JMenu Tweet_Paths = new JMenu("Tweet Paths");
        Tweet_Paths.addSeparator();
        TweetCollectionItem = new JMenuItem("Tweet Collection Folder");
        Tweet_Paths.add(TweetCollectionItem);
        TweetInvertedIndexItem = new JMenuItem("Tweet Inverted Index Folder");
        Tweet_Paths.add(TweetInvertedIndexItem);
        
        Select_Paths.add(Document_Paths);
        Select_Paths.addSeparator();
        Select_Paths.add(Tweet_Paths);        
        
        /*Select Retrieval Model*/
        JMenu Model = new JMenu("Retrieval Models");
        Model.addSeparator();
        
        /*Group of models radio buttons.*/
        ButtonGroup Models_group = new ButtonGroup();
        VectorSpace = new JRadioButtonMenuItem("Vector Space");
        VectorSpace.setSelected(true);
        Models_group.add(VectorSpace);
        OKAPI = new JRadioButtonMenuItem("OKAPI B25");
        Models_group.add(OKAPI);
        
        Model.add(VectorSpace);
        Model.addSeparator();           
        Model.add(OKAPI);
        
        
        JMenu Tweet_Model = new JMenu("Tweets Significance");
       
         /*Group of tweet models radio buttons.*/
        ButtonGroup Tweet_Models_group = new ButtonGroup();
        
        no_significance= new JRadioButtonMenuItem("None");
        Tweet_Models_group.add(no_significance);
        no_significance.setSelected(true);
        
        AT = new JRadioButtonMenuItem("@Users");
        Tweet_Models_group.add(AT);       
        
        HashTag = new JRadioButtonMenuItem("#Hashtags");
        Tweet_Models_group.add(HashTag);       
        
        Tweet_Model.add(no_significance);        
        Tweet_Model.addSeparator();         
        Tweet_Model.add(AT);        
        Tweet_Model.addSeparator();   
        Tweet_Model.add(HashTag);
        
        
        /*Tweet group by functionality*/
        JMenu Tweet_Group = new JMenu("Group Tweets By");
        
        Hottest= new JMenuItem("Hotness");
        GroupBy_Twitter = new JMenuItem("@User");
        GroupBy_Event = new JMenuItem("#HashTag");  
        
        Tweet_Group.add(Hottest);        
        Tweet_Group.addSeparator();         
        Tweet_Group.add(GroupBy_Twitter);        
        Tweet_Group.addSeparator();   
        Tweet_Group.add(GroupBy_Event);
        
        JMenu Tweets = new JMenu("Tweets");
        Tweets.add(Tweet_Model);
        Tweets.add(Tweet_Group);
        
        Help = new JMenu("Help");
        Credits = new JMenu("Credits");
        
            
        /*Add Menus to the Menu bar*/
        add(Select_Paths);
        add(Model);
        add(Tweets);
        add(Help);
        add(Credits);
        
        /*init the File choosers*/
        DocCollectionChooser = new JFileChooser();
        DocCollectionChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        DocIndexChooser = new JFileChooser();
        DocIndexChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        TweetCollectionChooser = new JFileChooser();
        TweetCollectionChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        TweetIndexChooser = new JFileChooser();
        TweetIndexChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
    }

    public void Update_Doc_Path(ScoobyMainUI main, boolean IsIndex){

        if (IsIndex){
            DocIndexChooser.setCurrentDirectory(new File(main.get_ScoobyConstants().get_Doc_Index_Path()));
            DocIndexChooser.showOpenDialog(null);
            main.get_ScoobyConstants().set_Doc_Index_Path(DocIndexChooser.getSelectedFile().getAbsolutePath());

            /*load vocabulay file*/
            main.get_ScoobyModel().Manage_Loading(main.get_ScoobyConstants().get_Doc_Index_Path(), false);
        }
        else{
            DocCollectionChooser.setCurrentDirectory(new File(main.get_ScoobyConstants().get_Doc_Collection_Path()));
            DocCollectionChooser.showOpenDialog(null);
            main.get_ScoobyConstants().set_Doc_Collection_Path(DocCollectionChooser.getSelectedFile().getAbsolutePath());            
        }
    }
    
    public void Update_Tweet_Path(ScoobyMainUI main, boolean IsIndex){

        if (IsIndex){
            TweetIndexChooser.setCurrentDirectory(new File(main.get_ScoobyConstants().get_Tweet_Index_Path()));
            TweetIndexChooser.showOpenDialog(null);
            main.get_ScoobyConstants().set_Tweet_Index_Path(TweetIndexChooser.getSelectedFile().getAbsolutePath());  
            
            /*Load the Vocabulary, twitters and events file*/
            main.get_ScoobyModel().Manage_Loading(main.get_ScoobyConstants().get_Tweet_Index_Path(), true);
        }
        else{
            TweetCollectionChooser.setCurrentDirectory(new File(main.get_ScoobyConstants().get_Tweet_Collection_Path()));
            TweetCollectionChooser.showOpenDialog(null);
            main.get_ScoobyConstants().set_Tweet_Collection_Path(TweetCollectionChooser.getSelectedFile().getAbsolutePath());            
        }        
    }    
    
    public void Update_Retrieval_Model(ScoobyMainUI main, int Model){
        main.get_ScoobyConstants().set_Model_Flag(Model);
    }
    
    public void Update_Tweet_Retrieval_Model(ScoobyMainUI main, int TweetModel){
        main.get_ScoobyConstants().set_Tweet_Model_Flag(TweetModel);
    }
    
    public void Display_Hot_Tweets(ScoobyMainUI main){
        ScoobyAnswersArea HotTweets = new ScoobyAnswersArea(main, 600, 600,"Hottest Tweets", main.getLocationOnScreen(), main.getSize());
        HotTweets.DisplayResults(main.get_ScoobyModel().Manage_Hot_Tweets(main.get_ScoobyConstants().get_Tweet_Index_Path(), main.get_ScoobyConstants().get_Top_Hot_Tweets_Num()));
    }
    
    public void Display_Group_By_Twitter(ScoobyMainUI main){
        ScoobyGroupByLabelArea Twitter = new ScoobyGroupByLabelArea("Group By Twitter", main, main.get_ScoobyModel().get_AllTwitters());
    }
    
    public void Display_Group_By_Events(ScoobyMainUI main){
        ScoobyGroupByLabelArea Events = new ScoobyGroupByLabelArea("Group By Events", main, main.get_ScoobyModel().get_AllEvents());
    }
    
    public void Display_Help(ScoobyMainUI main){
        ScoobyAnswersArea help = new ScoobyAnswersArea(main, 600, 400, main.get_ScoobyConstants().get_help_text(), Help.getLocationOnScreen(), Help.getSize());
    }
    
    public void Display_Credits(ScoobyMainUI main){
        ScoobyAnswersArea credits = new ScoobyAnswersArea(main, 600, 400, main.get_ScoobyConstants().get_credits_text(), Credits.getLocationOnScreen(), Credits.getSize());
    }
    
    /*accessors*/
    public JMenuItem getDocCollectionMenuItem(){
        return DocCollectionItem;
    }
    
    public JMenuItem getDocInvertedIndexMenuItem(){
        return DocInvertedIndexItem;
    }
    
    public JMenuItem getTweetCollectionItem(){
        return TweetCollectionItem;
    }
    
    public JMenuItem getTweetInvertedIndexMenuItem(){
        return TweetInvertedIndexItem;
    }

    public JRadioButtonMenuItem get_VectorSpaceRadioButton(){
        return VectorSpace;
    }
    
    public JRadioButtonMenuItem get_OKAPIRadioButton(){
        return OKAPI;
    }
    
    public JRadioButtonMenuItem get_NoneRadioButton(){
        return no_significance;
    }
    
    public JRadioButtonMenuItem get_TwitterRadioButton(){
        return AT;
    }
    
    public JRadioButtonMenuItem get_EventRadioButton(){
        return HashTag;
    }
    
    public JMenuItem get_Hottest(){
        return Hottest;
    }
    
    public JMenuItem get_GroupBy_Twitter(){
        return GroupBy_Twitter;
    }
    
    public JMenuItem get_GroupBy_Event(){
        return GroupBy_Event;
    }
    
    public JMenu get_Help(){
        return Help;
    }
       
    public JMenu get_Credits(){
        return Credits;
    }    
}
