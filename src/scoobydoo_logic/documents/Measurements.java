/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_logic.documents;

import scoobydoo_logic.documents.Word;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class Measurements {

    private Map<String,Word> loaded_voc;
    
    public Measurements(Map<String,Word> a_voc){
        loaded_voc = a_voc;
    }
    
    public String Generate_Random_Query(int Q_length){
        
        String Ret_Query = "";
        ArrayList<Integer> random_entries = new ArrayList<Integer>(Q_length);
        int entry_counter = 0;
        int query_words_generated = 0;
        int random_temp = -1;        
        /*add Q_length random ints that represent entrieson vocabulary tree map*/
        for (int count=0; count<Q_length; ++count){
            random_temp = 0 + (int)(Math.random() * ((loaded_voc.size() - 1) - 0) ) ; /*generate random in range [0 - loaded_voc.size()-1] */
            random_entries.add(count,random_temp);
        }
        /*sort them*/
        Collections.sort(random_entries);
        
        /*search them in the tree map and add the word of the query*/
        for(Map.Entry<String, Word> entry : loaded_voc.entrySet()){
            
            while (query_words_generated < Q_length && entry_counter == random_entries.get(query_words_generated) ){
                Ret_Query += entry.getKey()+" ";
                ++query_words_generated;
            }
            
            if (query_words_generated == Q_length)
                    break;
            
            ++entry_counter;
        }
        return Ret_Query;
    }
    
}
