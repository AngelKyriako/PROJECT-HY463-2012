/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Administrator
 */
public class ScoobyIndexingArea extends JPanel{
    
    private JButton DocIndexing;
    private JButton TweetIndexing;    

    
    public ScoobyIndexingArea(ScoobyDooPathsAndFlags ScoobyConstants){
        
        setLayout(new GridLayout(1,3));            
        
        DocIndexing = new JButton("DocIndexing",new ImageIcon(ScoobyConstants.get_initButton()));
        DocIndexing.setBackground(Color.white);        
        DocIndexing.setPreferredSize(new Dimension(130,50));
        
        TweetIndexing = new JButton("TweetIndexing",new ImageIcon(ScoobyConstants.get_ineetButton()));
        TweetIndexing.setBackground(Color.white);        
        TweetIndexing.setPreferredSize(new Dimension(130,50));
        
        JPanel empty = new JPanel();
        empty.setBackground(Color.white);
        
        add(DocIndexing);
        add(empty);        
        add(TweetIndexing);
    }
    
    public JButton get_Doc_Init_Button(){
        return DocIndexing;
    }
    
    public JButton get_Tweet_Init_Button(){
        return TweetIndexing;
    }
    
    public void Start_Doc_Indexing(ScoobyMainUI main){

        if (main.get_ScoobyConstants().get_Doc_Collection_Path().isEmpty()){
            JOptionPane.showMessageDialog(new JFrame(),
                    "   Please select the path of the folder that\n"
                  + "   includes the documents collection to index\n"
                  + "               from the menu.\n\n"
                  + "HINT: there are samples on the resources folder.\n");
        }
        else if(main.get_ScoobyConstants().get_Doc_Index_Path().isEmpty()){
            JOptionPane.showMessageDialog(new JFrame(),
                    "Please select the path where the folder of the documents inverted index\n"
                   +"       is going to be created, from the menu.\n");
        }
        else{
            /*Create the Inverted File*/
            main.get_ScoobyModel().Manage_Indexing(main.get_ScoobyConstants().get_Doc_Index_Path(), 
                                                   main.get_ScoobyConstants().get_Doc_Collection_Path(),
                                                   false);
            
            /*Update the Inverted Index's path (append the folder itself)*/
            main.get_ScoobyConstants().set_Doc_Index_Path(main.get_ScoobyConstants().get_Doc_Index_Path()+"\\Scooby_Doc_Inverted_Index");
            /*Load the Vocabulary file*/
            main.get_ScoobyModel().Manage_Loading(main.get_ScoobyConstants().get_Doc_Index_Path(), false);
        }
    }    
    
    public void Start_Tweet_Indexing(ScoobyMainUI main){

        if (main.get_ScoobyConstants().get_Tweet_Collection_Path().isEmpty()){
                JOptionPane.showMessageDialog(new JFrame(),
                                "   Please select the path of the folder that\n"
                          + "   includes the tweets collection to index\n"
                          + "               from the menu\n\n"
                   + "HINT: there are samples on the resources folder.\n");
        }
        else if(main.get_ScoobyConstants().get_Tweet_Index_Path().isEmpty()){
                JOptionPane.showMessageDialog(new JFrame(),
                                "Please select the path where the folder of the tweets inverted index\n"
                           +"       is going to be created, from the menu");
        }
        else{
                /*Create the Inverted File*/
                main.get_ScoobyModel().Manage_Indexing(main.get_ScoobyConstants().get_Tweet_Index_Path(), 
                                                                                           main.get_ScoobyConstants().get_Tweet_Collection_Path(),
                                                                                           true );

                /*Update the Inverted Index's path (append the folder itself)*/
                main.get_ScoobyConstants().set_Tweet_Index_Path(main.get_ScoobyConstants().get_Tweet_Index_Path()+"\\Scooby_Tweet_Inverted_Index");
                /*Load the Vocabulary, twitters and events file*/
                main.get_ScoobyModel().Manage_Loading(main.get_ScoobyConstants().get_Tweet_Index_Path(), true);       }
    }
    
}
