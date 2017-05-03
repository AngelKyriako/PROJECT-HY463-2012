/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_logic.documents;

import scoobydoo_logic.documents.Document;
import scoobydoo_logic.documents.AppearsInDoc;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
/**
 *
 * @author Angel
 */
public class Word {
    
    private String Id;
    private double Query_DF;
    private long DF;
    private double IDF;
    private long Posting_File_Index;
    /*DF = size of the following Map*/
    private Map <Integer,AppearsInDoc> Appearing_Docs;
    /**
     * default constructor
     */
    public Word(){}
    /**
     * Constructor
     * @param id(string): the name of the word
     */
    public Word(String id){
        
        this.Id = id;
        this.Query_DF = 0;
        this.DF = 0;
        this.IDF = 0;
        this.Posting_File_Index = 0;
        this.Appearing_Docs = new TreeMap();
    }
    /**
     * Constructor to be called during loading
     * @param id(String):name of word
     * @param idf(double): idf weight
     * @param posting_pointer(long): pointer to posting file 
     */
    public Word(String id, long df, double idf, long posting_pointer){
        this.Id = id;
        this.Query_DF = 0;
        this.DF = df;
        this.IDF = idf;
        this.Posting_File_Index = posting_pointer;
        this.Appearing_Docs = new HashMap((int)DF);        
    }
    /**
     * 
     */
    /**
     * inserts a new doc 
     * @param doc(AppearsInDoc): an appearing doc of the word
     */
    public void Insert_Appearing_Doc(Document doc, long pos){
        
        /*if it is the first time an appearance is
         *added for this word and document constuct
         * the positions hashtable first*/
        if ( !this.Appearing_Docs.containsKey(doc.get_Id()) ){
            this.Appearing_Docs.put(doc.get_Id(),new AppearsInDoc(doc));
        }
        
        this.get_Appearing_Doc(doc.get_Id()).RaiseCount();//raise count that is used for tf
        this.get_Appearing_Doc(doc.get_Id()).Insert_Position(pos);// add new position  
    }
    
    public void Insert_Appearing_Doc(int doc_id, AppearsInDoc an_appearing_Doc){
        if (!Appearing_Docs.containsKey(doc_id)){/*na rwtisw ton voh8o giati to petaei to exception ama valw idies lekseis se diaforetika query*/
            //throw new IllegalArgumentException("documend should not have already entered as an Appearing_doc");
        
            this.Appearing_Docs.put(doc_id, an_appearing_Doc);
        }
    }
    
    /**
     * @param an_index the line of the first document
     * the word appears in, on the posting file document
     */
    public void set_Posting_file_index(long an_index){
        this.Posting_File_Index = an_index;
    }
    /**
     * mutator that is used only for the query structure
     * @param new_df the new df to be setted
     */
    public void set_Query_DF(long new_df){
        this.Query_DF = new_df;
    }
    
    public void calculate_IDF(long All_Documents_count){
        
        this.DF = this.Appearing_Docs.size();
        
        this.IDF = (Math.log10((double)All_Documents_count / (double)this.DF )
                          / (Math.log(2)) );
        //System.out.println("idf: "+ IDF+"   df: "+DF);/*check tf and idf*/
        //this.IDF = (double)All_Documents_count / (double)this.Appearing_Docs.size();/*check without log*/
    }
    /**
     * gets the name 
     * @return(string) the name of the word 
     */
    public String get_Id(){
        return this.Id;
    }
    
    public double get_Query_DF(){
        return this.Query_DF;
    }
    /**
     * gets the DF of the word in all documents
     * @return(int) the sum DF of the word 
     */
    public long get_DF(){
        return this.DF;
    }
    
    public double get_IDF(){
        return this.IDF;
    }
    /**
     * @return(int) the pointer to
     * the posting document
     */
    public long get_Posting_file_index(){
        return this.Posting_File_Index;
    }
    /**
     * gets all the appearing docs
     * @return(Map <String,AppearsInDoc>) the name of the word 
     */    
    public Map <Integer,AppearsInDoc> get_All_Appearing_Docs(){
        return this.Appearing_Docs;
    }
    /**
     * gets an appearing doc
     * @param key(string): the name of the document to get 
     * @return(AppearsInDoc) an appearing doc of the word 
     */    
    public AppearsInDoc get_Appearing_Doc(Integer key){
        return this.Appearing_Docs.get(key);
    }
}
