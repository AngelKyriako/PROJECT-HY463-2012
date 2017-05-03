/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_logic.documents;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 *
 * @author Angel
 */
public class Document {
    
    private int Id;
    private Map <String,Word> All_DF;
    private long Max_Freq;
    private double norm;
    private double Score;
    private String Path;
    private String Type;
    private long BytePosition;//the length of the document in bytes
    private long ByteLength;
    
    public Document(){}
    
    public Document(int id, String path, String type){
        this.Id = id;
        this.All_DF = new HashMap<String,Word>(120);
        this.Max_Freq = 0;
        this.Score = 0;
        this.norm = 0;
        this.BytePosition = 0;
        this.ByteLength = 0;
        this.Path = path;
        this.Type = type;        
    }
    
    public Document(int id, double norm, long byte_length, String path, String type){
        this.Id = id;
        this.All_DF = null;
        this.Score = 0;
        this.norm = norm;
        this.BytePosition = 0;
        this.ByteLength = byte_length;
        this.Path = path;
        this.Type = type;        
    }    
    /**
     * mutator Inserts a new word in the hash map
     * @param word(string): the name(key) of the next word
     * @param df(long): the df of the word
     */
    public void Insert_Word(Word a_word){
        this.All_DF.put(a_word.get_Id(), a_word);
    }
    /**
     * mutator called only after the overall
     * reading of the text documents
     * @return(double): the Norm of the document 
     * Norm = |vector of doc| = sqrt(sumof(pow(idfi,2)))
     */    
    public void Calculate_Norm(){
        double Sum_idf_square = 0;

        for (Entry<String,Word> entry : All_DF.entrySet()){
            Word a_word = entry.getValue();

            Sum_idf_square += Math.pow(a_word.get_Appearing_Doc(this.get_Id()).get_TF_IDF()/*a_word.get_IDF()*/ , 2);
            
        }

        //System.out.println("Norm: "+Math.sqrt(Sum_idf_square));/*test print*/
        this.norm =  Math.sqrt(Sum_idf_square);        
    }    
    /**
     * sets a new value to the Maximum frequency
     * @param next_freq(long): the frequence of the
     *                    new word added in the doc
     *                    Throws an exception if it
     *                    is less than the max freq
     */
    public void set_Max_Freq(long next_freq){
            if (next_freq < Max_Freq)
                throw new IllegalArgumentException("New freq must be > than Max Freq");
           
                this.Max_Freq = next_freq;
    }
    
    public void set_Score(double new_score){
        this.Score = new_score;
    }
    
    public void set_Byte_Position(long a_position){
        this.BytePosition = a_position;
    }
    
    public void set_Byte_Length(long length){
        this.ByteLength = length;
    }
    
    public int get_Id(){
        return this.Id;
    }    
    /**
     * getter called only after the overall
     * reading of the text documents
     * @return(long) the Maximum frequency
     *           of a word in the document
     */
    public long get_Max_Freq(){
        return this.Max_Freq;
    }
    
    public double get_Score(){
        return this.Score;
    }
    /**
     * getter called only after the overall
     * reading of the text document
     * @return(long) the length of the
     *                  document in bytes
     */
    public long get_Byte_Length(){
        return this.ByteLength;
    }
    
    public String get_Path(){
        return this.Path;
    }
    
    public String get_Type(){
        return this.Type;
    }
    /**
     * getter that is going to be called during the 
     * writing of the posting file to keep the 
     * necessary pointers to the documents file
     * @return(long) the byte position in the documents file
     */
    public long get_Byte_Position(){
        return BytePosition;
    }
    /**
     * getter called only after the overall
     * reading of the text documents
     * @return(double): the Norm of the document 
     */
    public double get_Norm(){
        return this.norm;
    }

}
