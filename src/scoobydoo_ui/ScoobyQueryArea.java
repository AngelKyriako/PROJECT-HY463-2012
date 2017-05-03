/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 *
 * @author Angel
 */
class ScoobyQueryArea extends JPanel{
    
    private JTextField QueryBar;
    private JButton Doc_Search;
    private JButton Tweet_search;
    
    public ScoobyQueryArea(ScoobyDooPathsAndFlags ScoobyConstants){
        super();
        setBackground(Color.white);

        QueryBar = new JTextField("Enter Query...");
        QueryBar.setPreferredSize(new Dimension(300,30));
        QueryBar.setFont(new Font("Tahoma", Font.BOLD, 16));
        QueryBar.setForeground(new Color(60, 80, 139));

        Doc_Search = new JButton("DocSearch",new ImageIcon(ScoobyConstants.get_DocSearchButton()));
        Doc_Search.setBackground(Color.white);
        Doc_Search.setPreferredSize(new Dimension(130,45));
        
        Tweet_search = new JButton("TweetSearch",new ImageIcon(ScoobyConstants.get_TweetSearchButton()));
        Tweet_search.setBackground(Color.white);
        Tweet_search.setPreferredSize(new Dimension(130,45));
        

        add(Doc_Search);
        add(QueryBar);
        add(Tweet_search);
        
        
    }
    
    public JTextField get_Query_Bar(){
        return QueryBar;
    }
    
    public JButton get_Doc_Search_Button(){
        return Doc_Search;
    }
    
    public JButton get_Tweet_Search_Button(){
        return Tweet_search;
    }
    

    public void Doc_Search_Query(ScoobyMainUI main){

        /*if the scooby inverted index folder is already selected by the user search*/
        if(main.get_ScoobyConstants().get_Doc_Index_Path().endsWith("Scooby_Doc_Inverted_Index") ){
            
            /*Create the Answers Text Area*/
            ScoobyAnswersArea AnswersArea = new ScoobyAnswersArea(main);
            /*Display the Answers to the text Area*/        
            AnswersArea.DisplayResults(
                    main.get_ScoobyModel().Manage_Searching( QueryBar.getText(),
                                                             main.get_ScoobyConstants().get_Model_Flag(),
                                                             -1,
                                                             main.get_ScoobyConstants().get_Doc_Index_Path(),
                                                             false)
                                      );
            //System.out.println("path of inverted index: "+main.get_ScoobyConstants().get_Doc_Index_Path());
        }
        else{/*else pop up error message*/
            JOptionPane.showMessageDialog(new JFrame(),
                    "Please select a path of a document scooby\n"
                  + "   inverted Index folder from the menu");
        }
    }

    public void Tweet_Search_Query(ScoobyMainUI main){
        if(main.get_ScoobyConstants().get_Tweet_Index_Path().endsWith("Scooby_Tweet_Inverted_Index") ){

            /*Create the Answers Text Area*/
            ScoobyAnswersArea AnswersArea = new ScoobyAnswersArea(main);
            /*Display the Answers to the text Area*/        
            AnswersArea.DisplayResults(
                main.get_ScoobyModel().Manage_Searching( QueryBar.getText(),
                                                         main.get_ScoobyConstants().get_Model_Flag(),
                                                         main.get_ScoobyConstants().get_TweetModel_Flag(),
                                                         main.get_ScoobyConstants().get_Tweet_Index_Path(),
                                                         true)
                                   );
            //System.out.println("path of inverted index: "+main.get_ScoobyConstants().get_Tweet_Index_Path());                       
            }
            else{/*else pop up error message*/
                    JOptionPane.showMessageDialog(new JFrame(),
                                    "Please select a path of a tweet scooby\n"
                              + "   inverted Index folder from the menu");
            }
        }
}