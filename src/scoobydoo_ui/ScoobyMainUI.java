/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import scoobydoo_logic.ScoobyMainModel;
/**
 *
 * @author Angel
 */
public class ScoobyMainUI extends JFrame implements MouseListener{
    
    private ScoobyDooPathsAndFlags ScoobyConstants;
    private ScoobyMenu Menu;
    private ScoobyAnswersArea HotTweets_Window;
    private ScoobyAnswersArea Twitters_Window;
    private ScoobyAnswersArea Events_Window;
    private ScoobyQueryArea QueryArea;
    private ScoobyIndexingArea IndexingArea;
    private ScoobyMainModel Model;
    
    public ScoobyMainUI(ScoobyMainModel ScoobyModel)
    {
        /*Create The Frame*/
        super("Scooby Doogle");
        /*keep the reference of the logic model of scooby doogle*/
        Model = ScoobyModel;
        /*create the object containing all paths and flags necessary*/
        ScoobyConstants = new ScoobyDooPathsAndFlags();
        /*Create the Window*/
        setIconImage(Toolkit.getDefaultToolkit().getImage(ScoobyConstants.get_scooby()));
        setSize(800, 600);
        setResizable(false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(50 , (Toolkit.getDefaultToolkit ().getScreenSize().height-getHeight())/2);
        /*Create Menu*/
        Menu = new ScoobyMenu();         
        setJMenuBar(Menu);   
        /*Create North Panel*/
        JPanel North = new JPanel();
        JLabel Title = new JLabel(new ImageIcon(ScoobyConstants.get_scoobygoogle()));
        North.add(Title);
        North.setBackground(Color.white);
        
        /*Create Middle Panel*/
        JPanel Middle = new JPanel();
        QueryArea = new ScoobyQueryArea(ScoobyConstants);
        Middle.add(QueryArea);
        Middle.setBackground(Color.white);
        
        /*Create West Panel*/
        JPanel South = new JPanel();
        IndexingArea = new ScoobyIndexingArea(ScoobyConstants);
        South.add(IndexingArea);
        South.setBackground(Color.white);        
        
        /*Add all Panels to Frame*/
        add(North, BorderLayout.NORTH);
        add(Middle, BorderLayout.CENTER);
        add(South, BorderLayout.SOUTH);
                  
        /*add all necessary listeners*/
        AddAllListeners();
        /*make the window visible*/
        setVisible(true);
    }
    
    public ScoobyDooPathsAndFlags get_ScoobyConstants(){
        return ScoobyConstants;
    }
    
    public ScoobyMainModel get_ScoobyModel(){
        return this.Model;
    }
    
    private void AddAllListeners(){
        Menu.getDocCollectionMenuItem().addMouseListener(this);
        Menu.getDocInvertedIndexMenuItem().addMouseListener(this);
        Menu.getTweetCollectionItem().addMouseListener(this);
        Menu.getTweetInvertedIndexMenuItem().addMouseListener(this);

        Menu.get_VectorSpaceRadioButton().addMouseListener(this);
        Menu.get_OKAPIRadioButton().addMouseListener(this);
        
        Menu.get_NoneRadioButton().addMouseListener(this);
        Menu.get_TwitterRadioButton().addMouseListener(this);
        Menu.get_EventRadioButton().addMouseListener(this);
        
        Menu.get_Hottest().addMouseListener(this);
        Menu.get_GroupBy_Twitter().addMouseListener(this);
        Menu.get_GroupBy_Event().addMouseListener(this);
        
        Menu.get_Help().addMouseListener(this);
        Menu.get_Credits().addMouseListener(this);
        
        QueryArea.get_Doc_Search_Button().addMouseListener(this);
        QueryArea.get_Tweet_Search_Button().addMouseListener(this);
        
        IndexingArea.get_Doc_Init_Button().addMouseListener(this);
        IndexingArea.get_Tweet_Init_Button().addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == Menu.get_Hottest()){
            Menu.Display_Hot_Tweets(this);
        }
        else if (e.getSource() == Menu.get_GroupBy_Twitter()){
            Menu.Display_Group_By_Twitter(this);
        }
        else if (e.getSource() == Menu.get_GroupBy_Event()){
            Menu.Display_Group_By_Events(this);
        }        
        else if (e.getSource() == Menu.get_Help()){
            Menu.Display_Help(this);
        }
        else if (e.getSource() == Menu.get_Credits()){
            Menu.Display_Credits(this);
        } 
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        
        if (e.getSource() == Menu.getDocCollectionMenuItem()){
            Menu.Update_Doc_Path(this, false);
        }    
        else if (e.getSource() == Menu.getDocInvertedIndexMenuItem()){
            Menu.Update_Doc_Path(this, true);
        }
        else if (e.getSource() == Menu.getTweetCollectionItem()){
            Menu.Update_Tweet_Path(this, false);
        }            
        else if (e.getSource() == Menu.getTweetInvertedIndexMenuItem()){
            Menu.Update_Tweet_Path(this, true);            
        }
        else if (e.getSource() == Menu.get_VectorSpaceRadioButton()){
            Menu.Update_Retrieval_Model(this, 0);
        }
        else if (e.getSource() == Menu.get_OKAPIRadioButton()){
            Menu.Update_Retrieval_Model(this, 1);
        }
        else if (e.getSource() == Menu.get_NoneRadioButton()){
            Menu.Update_Tweet_Retrieval_Model(this, 0);
        }          
        else if (e.getSource() == Menu.get_TwitterRadioButton()){
            Menu.Update_Tweet_Retrieval_Model(this, 1);
        }        
        else if (e.getSource() == Menu.get_EventRadioButton()){
            Menu.Update_Tweet_Retrieval_Model(this, 2);
        }
        else if (e.getSource() == QueryArea.get_Doc_Search_Button()){
            QueryArea.Doc_Search_Query(this);
        }
        else if (e.getSource() == QueryArea.get_Tweet_Search_Button()){
            QueryArea.Tweet_Search_Query(this);
        }
        else if (e.getSource() == IndexingArea.get_Doc_Init_Button()){
            IndexingArea.Start_Doc_Indexing(this);
        }
        else if (e.getSource() == IndexingArea.get_Tweet_Init_Button()){
            IndexingArea.Start_Tweet_Indexing(this);
        }               
    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        if (e.getSource() == Menu.get_HotRadioButton()){
//            Menu.Display_Hot_Tweets(this, HotTweets_Window);
//        }
//        else if(e.getSource() == Menu.get_TwitterRadioButton()){
//            Menu.Display_Twitters(this, Twitters_Window);
//        }
//        else if(e.getSource() == Menu.get_EventRadioButton()){
//            Menu.Display_Events(this, Events_Window);
//        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        if (e.getSource() == Menu.get_HotRadioButton()){
//            HotTweets_Window.setVisible(false);
//        }
//        else if(e.getSource() == Menu.get_TwitterRadioButton()){
//            Twitters_Window.setVisible(false);
//        }
//        else if(e.getSource() == Menu.get_EventRadioButton()){
//            Events_Window.setVisible(false);
//        }
    }
    
}
