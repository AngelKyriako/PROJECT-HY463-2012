/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import scoobydoo_logic.documents.AnswerItem;

/**
 *
 * @author Angel
 */
class ScoobyAnswersArea extends JFrame{
    
    private JScrollPane ScrolledAnswers;
    private JEditorPane Answers;
    
    public ScoobyAnswersArea(ScoobyMainUI main){

        super("Hits");
        setBackground(Color.white);
        setIconImage(Toolkit.getDefaultToolkit().getImage(main.get_ScoobyConstants().get_scooby()));
        setSize(600, 600);
        setLocation(main.getLocation().x + main.getWidth() , main.getLocation().y);
        
        //Create the Answers text area
        Answers = new JEditorPane("text/html","Scooby Doo does not know where to start searching from...");
        Answers.setEditable(false); 
        
        //And now add a scroll bar to the Text area
        ScrolledAnswers = new JScrollPane(Answers);
        ScrolledAnswers.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        add(ScrolledAnswers);
        
        add_hyper_link_listener();
        
        setVisible(true);
    }
    
    public ScoobyAnswersArea(ScoobyMainUI main, int Width, int Height, String a_msg, Point screen_location, Dimension size){

        super();
        
        setBackground(Color.white);
        setIconImage(Toolkit.getDefaultToolkit().getImage(main.get_ScoobyConstants().get_scooby()));
        setSize(Width, Height);        
        setLocation(screen_location.x + size.width, screen_location.y);
        
        //Create the Answers text area
        Answers = new JEditorPane("text/html",a_msg);
        Answers.setEditable(false); 
        
        //And now add a scroll bar to the Text area
        ScrolledAnswers = new JScrollPane(Answers);
        ScrolledAnswers.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        add(ScrolledAnswers);
        setVisible(true);
    }    
    
    private void add_hyper_link_listener(){
        
        Answers.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {

                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
                                System.err.println( "Desktop doesn't support the browsing, program terminated." );
                                System.exit(1);
                        }
                        try {
                                desktop.browse( e.getURL().toURI() );
                        }
                        catch ( Exception ex ) {
                                System.err.println( ex.getMessage() );
                        }

                } 
            }
        });        
    }
    
    public void DisplayResults(TreeMap<Integer,AnswerItem> Results){
        String hits = "";
        AnswerItem next_hit = null;
        
        for(Map.Entry<Integer, AnswerItem> entry : Results.entrySet()){
            next_hit = entry.getValue();
            hits += next_hit;
        }
        Answers.setText(hits);
    }
}
