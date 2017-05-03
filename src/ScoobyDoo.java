/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.logging.Level;
import java.util.logging.Logger;
import scoobydoo_logic.ScoobyMainModel;
import scoobydoo_ui.ScoobyMainUI;
import scoobydoo_logic.documents.Measurements;
/**
 *
 * @author Angel
 */
public class ScoobyDoo {
    
    public static void main(String args[])
    {
        /*Manage Sccoby Doogle Model*/
        ScoobyMainModel ScoobyModel = new ScoobyMainModel();
        
        /*Manage Scooby Doogle UI*/
        ScoobyMainUI ScoobyUI = new ScoobyMainUI(ScoobyModel);
        /*
        //Manage Scooby Measurements
        while(true){
            //start only after the vocabulary has been created successfully
            if (!ScoobyModel.get_Vocabulary().isEmpty()){
                try {
                    new Thread().sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ScoobyDoo.class.getName()).log(Level.SEVERE, null, ex);
                }
                Measurements ScoobyMeasure = new Measurements(ScoobyModel.get_Vocabulary());
                for (int i=0; i<100; ++i){
                    //search 1000 queries 250 for each query length [1-4]
                    ScoobyModel.Manage_Searching(ScoobyMeasure.Generate_Random_Query(1), 0);
                }
                for (int i=0; i<100; ++i){
                    //search 1000 queries 250 for each query length [1-4]
                    ScoobyModel.Manage_Searching(ScoobyMeasure.Generate_Random_Query(2), 1);
                }
                for (int i=0; i<100; ++i){
                    //search 1000 queries 250 for each query length [1-4]
                    ScoobyModel.Manage_Searching(ScoobyMeasure.Generate_Random_Query(3), 0);
                }
                for (int i=0; i<100; ++i){
                    //search 1000 queries 250 for each query length [1-4]
                    ScoobyModel.Manage_Searching(ScoobyMeasure.Generate_Random_Query(4), 1);
                }
                
                break;
            }    
        }

         */    
    }
}
