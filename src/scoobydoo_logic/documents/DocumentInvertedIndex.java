/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_logic.documents;


import scoobydoo_logic.documents.Document;
import scoobydoo_logic.documents.AppearsInDoc;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Angel
 */
public class DocumentInvertedIndex 
{
    private String InvertedIndexPath;
    private TreeMap <String, Word> All_Words;
    private TreeMap <Integer, Document> All_Docs;
    private HashMap <String, Integer> Stop_Words;
    private double Avg_length;
    
    public DocumentInvertedIndex(String a_path)
    {
        InvertedIndexPath = a_path;
        All_Words = new TreeMap();
        All_Docs = new TreeMap();
        Stop_Words =  new HashMap<String, Integer>(); 
    }
    
    protected void set_Avg_Length(double a_length){
        Avg_length = a_length;
    }
    
    public String get_Path(){
        return InvertedIndexPath;
    }
    public TreeMap<Integer, Document> get_AllDocuments(){
        return All_Docs;
    }
    public Document get_Document(int key){
        return All_Docs.get(key);
    }    
    public TreeMap<String, Word> get_AllWords(){
        return All_Words;
    }
    public Word get_Word(String key){
        return All_Words.get(key);
    }
    public HashMap <String, Integer> get_StopWords(){
        return Stop_Words;
    }
    public double get_Avg_Length(){
        return Avg_length;
    }
    /**
     * add a document in the map
     * @param Doc: a new document to insert
     */
    public void Insert_Document(Document Doc){
        if (this.All_Docs.containsKey(Doc.get_Id()))
            throw new IllegalArgumentException("document already contained");
        
        this.All_Docs.put(Doc.get_Id(), Doc);
    }
    /**
     * add a word in the map or just raise it's frequency
     * @param id: the word we are reading
     * @param a_doc: the document where it was found 
     * @param an_aprnc: word's location in this document 
     */
    public void Insert_Word(String id, Document a_doc, long an_aprnc){

        /*construct it if new and put it in the treemap*/
        if (!All_Words.containsKey(id)){
            this.All_Words.put(id, new Word(id));
        }
        /*Insert into the appearence*/
        this.All_Words.get(id).Insert_Appearing_Doc(a_doc, an_aprnc);
        
        a_doc.Insert_Word(/*the current word*/this.All_Words.get(id));
        
        //in case of error try the next line first
        //this.All_Docs.get(a_doc.get_Id()).Insert_Word(/*the current word*/this.All_Words.get(id));
        
    }
    /**
     * Create and write the document file
     * @param parentDir(File): the folder where to create the text document
     * @throws IOException 
     */
    private void Create_Documents_File(File parentDir)
    {          
        try{
            final File Documentfile = new File(parentDir, "DocumentFile.txt");
            Documentfile.createNewFile();
            FileWriter fstream = new FileWriter(Documentfile);
            BufferedWriter out = new BufferedWriter(fstream);

            Document next_doc = null;
            long BytesInDocFile = 0;
            double SumofByteLengths = 0; /*used for average of docs' lengths*/
            String temp;

            /*write to document*/
            for(Map.Entry<Integer, Document> entry : All_Docs.entrySet())
            {
                next_doc = entry.getValue();       

                next_doc.Calculate_Norm();
                /*set the position the document has in the document file,
                  it will be used as a posting file's pointer to the doc*/
                next_doc.set_Byte_Position(BytesInDocFile);
                /*keep the doc's info to a string*/
                temp = next_doc.get_Id()+"\t"+ next_doc.get_Norm()+"\t"+next_doc.get_Byte_Length()+
                                             /*edw exoume provlhma me ta ellhnika windows !!!*/
                                         "\t"+ new String(next_doc.get_Path().getBytes(), "UTF-8")+
                                         "\t"+ new String(next_doc.get_Type().getBytes(), "UTF-8")+"\n";
                /*write the doc info to document file*/
                out.write(temp);                
                /*calculate the position of the next document in the document file*/
                BytesInDocFile += temp.getBytes().length;
                /*add the document's length to the sum*/
                SumofByteLengths += next_doc.get_Byte_Length();

            }
            out.close();
            Avg_length = SumofByteLengths / (double)All_Docs.size();/*calculate average of all lengths*/
        }
        catch (Exception e){//Catch exception if any
        System.err.println("Exception in Create Document File, InvertedFile.Java:"+ e.getMessage());
        }            
    }
    
    protected void Calculate_all_TF_IDFs(){
        Word word = null;
        AppearsInDoc ApInDc = null;        
        /*first calculate the tf_idf for every doc x word combination*/
        for (Map.Entry<String,Word> word_entry : All_Words.entrySet()){
            word = word_entry.getValue();
            word.calculate_IDF(All_Docs.size());
            for (Map.Entry<Integer,AppearsInDoc> Appearing_doc_entry : word.get_All_Appearing_Docs().entrySet()){
                ApInDc= Appearing_doc_entry.getValue();
                ApInDc.calculate_TF();
                ApInDc.calculate_TF_IDF(word.get_IDF());            
            }
        }      
    }

    public void Create_Posting_and_Vocabulary_Files(File parentDir)
    {
        try{
            /*create the posting file*/
            File a_file = new File(parentDir, "PostingFile.txt");
            a_file.createNewFile();
            FileWriter fstream = new FileWriter(a_file);
            BufferedWriter Posting_out = new BufferedWriter(fstream);

            /*create the vocabulary file*/
            a_file = new File(parentDir, "VocabularyFile.txt");
            a_file.createNewFile();     
            fstream = new FileWriter(a_file);
            BufferedWriter Vocabulary_out = new BufferedWriter(fstream);

            long posting_index = 0;/*byte length counting the bytes that are getting printed*/
            Word word = null;
            String tempStr = null;

            /*First write the average length of all the documents*/
            Vocabulary_out.write(this.Avg_length+"\n");       
            /*for every word*/
            for (Map.Entry<String,Word> word_entry : All_Words.entrySet())
            {
                word = word_entry.getValue();
                word.set_Posting_file_index(posting_index);
                
                /*write every word to the vocabulary file*/
                Vocabulary_out.write(word.get_Id()+"\t"+word.get_DF()+"\t"+word.get_IDF()+
                                "\t"+word.get_Posting_file_index()+"\n");                

                /*write to posting file for every document this word appears in*/
                for (Map.Entry<Integer,AppearsInDoc> Appearing_doc_entry : word.get_All_Appearing_Docs().entrySet()){

                    AppearsInDoc ApInDc = Appearing_doc_entry.getValue();
                    
                    
                    tempStr = ApInDc.get_Doc().get_Byte_Position()+"\t"+ApInDc.get_TF()+"\t"+ApInDc.get_TF_IDF()+"\t"+ApInDc.get_All_Positions().size()+"\t";
                    posting_index += tempStr.getBytes().length;/*raise the index of the posting file*/
                    Posting_out.write(tempStr);

                    /*for every position of this word in every appearing document*/
                    for (Map.Entry<Integer,Long> position_entry : ApInDc.get_All_Positions().entrySet()){
                        /*write again to posting file*/
                        tempStr = position_entry.getValue()+"\t";
                        posting_index += tempStr.getBytes().length;/*raise the index of the posting file*/
                        Posting_out.write( tempStr);
                    }
                    /*At last write a new line for readability*/
                    posting_index += "\n".getBytes().length;/*raise the index of the posting file*/
                    //Posting_out.write(" ---->"+ApInDc.get_Doc().get_Id()); /*used for testing*/
                    Posting_out.write("\n");                    
                }
            }
            /*close the writers*/
            Posting_out.close();
            Vocabulary_out.close();
        }
        catch (Exception e){//Catch exception if any
            System.err.println("Exception in Create posting&vocabulary File, InvertedFile.Java:"+ e.getMessage());
        }
    }
    
    public void Create_CollectionIndex(String path)
    {
        File parentDir = new File(path+"\\Scooby_Doc_Inverted_Index");
        parentDir.mkdir();
        Calculate_all_TF_IDFs();
        Create_Documents_File(parentDir);
        Create_Posting_and_Vocabulary_Files(parentDir);
        
        /*
        //create files for countings
        File a_file = null;
        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            //bytes of inverted index
            a_file = new File("inverted_index_byte_count_file.txt");
            fstream = new FileWriter(a_file, true);
            out = new BufferedWriter(fstream);        
            out.write( "\n"+parentDir.length());
            out.close();
            //number of docs in the collection
            a_file = new File("Number_of_docs_count_file.txt");
            fstream = new FileWriter(a_file, true);
            out = new BufferedWriter(fstream);        
            out.write( "\n"+All_Docs.size());
            out.close();
            
        }catch (IOException ex) {
            Logger.getLogger(DocumentInvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
      
}
