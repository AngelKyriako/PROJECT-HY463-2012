/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_ui;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import scoobydoo_logic.ScoobyMainModel;

/**
 *
 * @author Administrator
 */
public class ScoobyGroupByLabelArea extends JFrame implements ActionListener{
    
    private ScoobyMainUI Scooby_UI;
    
    public ScoobyGroupByLabelArea(String title, ScoobyMainUI scooby_main_ui, Map<String,Long> entries){
        super(title);
        setIconImage(Toolkit.getDefaultToolkit().getImage(scooby_main_ui.get_ScoobyConstants().get_scooby()));
        setSize(300, 600);
        setResizable(false);
        setLocation(scooby_main_ui.getLocationOnScreen().x + scooby_main_ui.getSize().width, scooby_main_ui.getLocationOnScreen().y);
        
        JPanel background = new JPanel();
        background.setLayout(new GridLayout(entries.size(),1));
        
        JPanel next_panel = null;
        JButton next_button = null;
        for (Map.Entry<String,Long> next_entry : entries.entrySet()){
            next_button = new JButton(next_entry.getKey()+" appeared "+next_entry.getValue()+" times");
            next_button.setSize(10, 100);
            next_button.addActionListener(this);
            
            next_panel = new JPanel();
            next_panel.setSize(10, 1);
            next_panel.add(next_button);
            background.add(next_button);
        }
        
        JScrollPane scrolled_background = new JScrollPane(background);
        scrolled_background.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));        
        add(scrolled_background);
        
        setVisible(true);
        
        Scooby_UI = scooby_main_ui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String Button_Name = e.getActionCommand();
        int Group_By_Flag = 0;
        if (getTitle().equals("Group By Twitter")){
            Group_By_Flag = 1;
        }
        else if (getTitle().equals("Group By Relevant_Twitters")){
            Group_By_Flag = 2;
        }
        else if (getTitle().equals("Group By Events")){
            Group_By_Flag = 3;
        }
        else{
            System.err.println("Unrecognized group by operation, program terminated");
            System.exit(1);
        }
        
        ScoobyAnswersArea Group_By = new ScoobyAnswersArea(Scooby_UI, 600, 600, "Group By",
                                                           getLocationOnScreen(), getSize());        
        
        Group_By.DisplayResults( Scooby_UI.get_ScoobyModel().Manage_Group_By
                                                           (
                                                             Button_Name.split(" ")[0],
                                                             Scooby_UI.get_ScoobyConstants().get_Tweet_Index_Path(),
                                                             Group_By_Flag
                                                           )
                               );
        System.out.println("query that searched: "+Button_Name.split(" ")[0]);
    }
    
}
