/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_logic.documents;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Angel
 * Object including the count of 
 * a certain word in this document
 * and its name
 * Name: the name of the document
 * Count: the count of the word 
 * in the document
 */
public class AppearsInDoc{
    
    private Document Doc;
    private long Doc_Freq;
    private double TF;
    private double TF_IDF;
    private Map <Integer,Long> positions;

    public AppearsInDoc(){}
    
    public AppearsInDoc(Document a_doc){
        this.Doc = a_doc;
        this.Doc_Freq = 0;
        this.TF = 0;
        this.TF_IDF = 0;        
        this.positions = new HashMap <Integer,Long>(8);
    }
    
    public AppearsInDoc(Document a_doc, double tf, double tf_idf, int positions_length){
        this.Doc = a_doc;
        this.Doc_Freq = 0;
        this.TF = tf;
        this.TF_IDF = tf_idf;
        this.positions = new HashMap <Integer,Long>(positions_length);      
    }
    
    public Document get_Doc(){
        return this.Doc;
    }
    
    public long get_Doc_Freq(){
        return this.Doc_Freq;
    }
    
    public double get_TF(){
        return this.TF;
    }
    
    public double get_TF_IDF(){
        return this.TF_IDF;
    }
    
    public Map <Integer,Long> get_All_Positions(){
        return this.positions;
    }
    
    public long get_Position(int i){
        return this.positions.get(i);
    }
    
    public void set_Doc(Document a_doc){
        this.Doc = a_doc;
    }
    
    public void Insert_Position(Long a_position){
        this.positions.put(positions.size(), a_position);
    }    

    public void calculate_TF(){
        this.TF = (double)this.Doc_Freq / (double)this.Doc.get_Max_Freq();
    }
    
    public void calculate_TF_IDF(double word_idf){
        this.TF_IDF = this.TF * word_idf;
    }
    
    public void update_Max_Freq(){
        if (this.Doc_Freq > this.Doc.get_Max_Freq() )
            this.Doc.set_Max_Freq(this.Doc_Freq);
    }
    /**
     * setter that raises the count
     */
    public void RaiseCount(){
        ++this.Doc_Freq;
        update_Max_Freq();
    }
}
