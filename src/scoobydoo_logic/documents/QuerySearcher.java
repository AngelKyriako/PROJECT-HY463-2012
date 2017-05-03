/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_logic.documents;

import scoobydoo_logic.documents.Document;
import scoobydoo_logic.documents.AppearsInDoc;
import scoobydoo_logic.documents.AnswerItem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import scoobydoo_logic.ScoobyMainModel;

/**
 *
 * @author Angel
 */
public class QuerySearcher{
    
    /*Data structure that contains all the query words or similar of them*/
    private String[] User_Query_Words;
    private Map<String,Word> Query_Words;
    private Map<String,Double> Query_Words_Similarity;
    private double Max_Similarity;
    private int Query_Max_Freq;
    /*data structure that contains all relevant documents with the query*/
    private Map<Integer,Document> Document_Results;
    private Map<Double,Document> Sorted_Document_Results;
    /*the snippets to be printed together with each document*/
    private Map<Double, Long> Sorted_Best_Positions;
    /*the path of inverted_Index*/
    private String Inverted_Index_Path;
    /*The out put of the search engine*/
    private TreeMap<Integer, AnswerItem> Hits;
    
    public QuerySearcher(String query, ScoobyMainModel scooby_model, String index_path){
        
        Hits = new TreeMap<Integer, AnswerItem>();
        Hits.put(0,new AnswerItem(query));        
        Inverted_Index_Path = index_path;
        User_Query_Words = null;
        Query_Words = new HashMap<String,Word>(8);
        Query_Words_Similarity = new HashMap<String,Double>(8); 
        
        Query_Max_Freq = 1;
        Document_Results = new HashMap<Integer,Document>(8);
        Sorted_Document_Results = new TreeMap<Double,Document>();
        Sorted_Best_Positions = new HashMap<Double,Long>(8);
        
        Update_Query_Words(query, scooby_model);
    }
    /**
     * Transformer 
     * Creates a Map that contains: every words that exist in the vocabulary
     * and are similar with the word that the user has typed.
     * Also creates another Map with the same keys as the previous with value 
     * the similarity of the vocabulary word and the query word!!!
     * 
     * @param a_word(Word): this is one of the query words that the user typed
     * @param loaded_voc(Map of Word value): this is the vocabulary that has been loaded
     */
    private void add_similar_words(String a_word, Map<String,Word> loaded_voc){
        double similarity_factor = 0;
        
        for(Map.Entry <String,Word> entry : loaded_voc.entrySet()){
            
            /*Continue only if the first or the last letters of the two words are the same*/
            if ( a_word.charAt(0) == entry.getKey().charAt(0) ){
                
                /*user word is substring of vocabulary word*/
                if ( entry.getKey().contains(a_word) ){
                    similarity_factor = 1 - (((double)(entry.getKey().length() - a_word.length()) * 0.08) - ((double)a_word.length()* 0.05) );
                }/*vocabulary word is substring of user word*/
                else if (a_word.contains(entry.getKey())){
                    similarity_factor = 1 - ( ((double)(a_word.length() - entry.getKey().length()) * 0.17) - ((double)entry.getKey().length() *0.03) ) ;    
                }
                else{
                    similarity_factor = 0;
                    continue;
                }
                
            }/*this case is wanted only for examples like: query "νερο",  vocabulary word "απονερο"*/
            else if (a_word.charAt(a_word.length()-1) == entry.getKey().charAt(entry.getKey().length()-1)
                    && a_word.contains(entry.getKey())){
                similarity_factor = 1 - ( ((double)(a_word.length() - entry.getKey().length()) * 0.19) - ((double)entry.getKey().length() *0.03) ) ;    
            }            
            else{
                    similarity_factor = 0;
                    continue;
            }

            if (similarity_factor > 0.6){

                if (!Query_Words.containsKey (entry.getKey()) ){
                    entry.getValue().set_Query_DF(1);
                    Query_Words.put(entry.getKey(), entry.getValue());
                    /*add to the similarity data structure*/
                    Query_Words_Similarity.put(entry.getKey(), similarity_factor);
                }
                else{
                    if (Query_Words_Similarity.get(entry.getKey()) == similarity_factor ){
                        Query_Words.get(entry.getKey()).set_Query_DF(Query_Words.get(entry.getKey()).get_DF() + 1);

                        /*update max frequency of the query*/
                        if (Query_Max_Freq < Query_Words.get(entry.getKey()).get_DF()){                        
                            Query_Max_Freq = (int)Query_Words.get(entry.getKey()).get_DF();
                        }
                    }/*if it already exists check if the similiratiy factor is different, that means that it is 
                     a substring of more than one user query words */
                    else{
                        long temp_df = Query_Words.get(entry.getKey()).get_DF() + 1;
                        Query_Words_Similarity.put(entry.getKey(), (double)(similarity_factor+Query_Words_Similarity.get(entry.getKey())) / 1.5);
                        Query_Words.get(entry.getKey()).set_Query_DF(temp_df/2);
                    }

                }

                if (similarity_factor > Max_Similarity)
                        Max_Similarity = similarity_factor;                    
            }
        }
    }
    
    private void Update_Query_Words(String query, ScoobyMainModel scooby_model)
    {
        User_Query_Words = query.toLowerCase().split("[\\p{Punct}\\s«»’—•°··‘]+");
        for(int i =0; i < User_Query_Words.length ; i++){
            if ((User_Query_Words[i] = scooby_model.Filter_word(User_Query_Words[i], true)) != null)
                add_similar_words(User_Query_Words[i], scooby_model.get_Vocabulary());
        }
        
        System.out.println("query words structure created:");/*test print*/
        for(Map.Entry <String,Word> w : Query_Words.entrySet()){
            System.out.println("\t\t\t\t"+w.getValue().get_Id()+": with df "+w.getValue().get_DF()+" and similarity "+ Query_Words_Similarity.get(w.getKey()));
        }
    }
    
    public Document Load_Document_File(RandomAccessFile document_file, long Index_of_Posting_File) throws IOException{
        
        document_file.seek(Index_of_Posting_File);
        StringTokenizer parser = new StringTokenizer(document_file.readLine(),"\t\n");
        
        Document a_doc = null;
        int id = 0;
        double norm = 0;
        long byte_length = 0;
        String path = null, type = null;
        
        /*read id of document*/
        if (parser.hasMoreTokens())
                id = (int)Integer.parseInt(parser.nextToken());
        /*if it has not been already inserted read the rest of the info and insert*/
        if (!Document_Results.containsKey(id)){
            if (parser.hasMoreTokens())
                norm = (double)Double.parseDouble(parser.nextToken());
            if (parser.hasMoreTokens())
                byte_length = (long)Long.parseLong(parser.nextToken());
            if (parser.hasMoreTokens())
                path = parser.nextToken();
            if (parser.hasMoreTokens())
                type = parser.nextToken();

            a_doc = new Document(id, norm, byte_length, path, type);

            Document_Results.put(id, a_doc);
            //System.out.println("test document: "+ " D"+Document_Results.get(id).get_Id()+" Path: "+Document_Results.get(id).get_Path() +" Type: "+Document_Results.get(id).get_Type() +" byte_length: "+Document_Results.get(id).get_Byte_Length());            
        }
        return Document_Results.get(id);
    }
    
    public Word Load_Posting_File(Word a_word, RandomAccessFile posting_file, RandomAccessFile document_file) throws IOException{

        /*seek the a line to a certain location*/
        posting_file.seek(a_word.get_Posting_file_index());
        
        double tf = 0, tf_idf = 0;
        int positions_length = 0;
        long posting_file_pointer = 0, a_position = 0;
        Document a_doc;
        
        for (int Appearence=0; Appearence<a_word.get_DF(); ++Appearence){
            /*parse the tokens of the line and */
            StringTokenizer parser = new StringTokenizer(posting_file.readLine(),"\t\n");
            
            /*Read the posting file pointer*/
            if (parser.hasMoreTokens())
                posting_file_pointer = (long)Long.parseLong(parser.nextToken());        
            /*and Load the necessary document from the document file*/
            a_doc = Load_Document_File(document_file, posting_file_pointer);
            /*Load the rest of the posting file */
            if (parser.hasMoreTokens())
                tf = (double)Double.parseDouble(parser.nextToken());            
            if (parser.hasMoreTokens())
                tf_idf = (double)Double.parseDouble(parser.nextToken());
            if (parser.hasMoreTokens())
                positions_length = (int)Integer.parseInt(parser.nextToken());
            /*Insert the readed Appearing Document to the word structure*/
            a_word.Insert_Appearing_Doc(a_doc.get_Id(), new AppearsInDoc(a_doc, tf, tf_idf, positions_length));
            
            /*for every position of the word in the document*/
            for (int position=0; position<positions_length; position++){
                if (parser.hasMoreTokens())
                    a_position = (long)Long.parseLong(parser.nextToken());
                /*Insert in the Appearing Doc*/
                a_word.get_Appearing_Doc(a_doc.get_Id()).Insert_Position(a_position);
            }
            //System.out.println("test appearing doc: "+/*"tf: "+tf+" tf_idf: "+tf_idf+"  number of positions: "+positions_length+ */"  doc id: "+a_doc.get_Id()+ "  doc path: "+a_doc.get_Path());
        }
        return a_word;
    }
    
    public void Load_Everything(){
        try {
            RandomAccessFile Posting_file = new RandomAccessFile(Inverted_Index_Path+"\\PostingFile.txt", "r");
            RandomAccessFile Document_file = new RandomAccessFile(Inverted_Index_Path+"\\DocumentFile.txt", "r");
            for(Map.Entry <String,Word> w : Query_Words.entrySet()){
                w.setValue( Load_Posting_File(w.getValue(), Posting_file, Document_file) );
            }
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(QuerySearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(QuerySearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void ScoreByVectorSpace(Document a_doc){
        double score; 
        double vector_doc = a_doc.get_Norm();
        double vector_query = 0.0; // h alliws norma toy query
        double arithmitis = 0.0;
        double similarity_tf = 0.0;
        AppearsInDoc Apr;
        //System.out.println("Vector Space");
        /*run threw all the posting file entries for this document regarding the query words*/
        for(Map.Entry <String,Word> w : Query_Words.entrySet())
        {
            if( (Apr = w.getValue().get_Appearing_Doc(a_doc.get_Id())) != null )
            {
                similarity_tf =  Query_Words_Similarity.get(w.getKey())*w.getValue().get_Query_DF()/Query_Max_Freq;
               /*we multiply the tf of the query word with the similarity of this word*/
               arithmitis += ( similarity_tf/*q tf*/ * w.getValue().get_IDF()/* q idf*/ )
                                                        * Apr.get_TF_IDF();   //query_tf_idf * document_tf_idf           
                
               vector_query += Math.pow(w.getValue().get_DF()*1 ,2);
            }
            else{
                //System.out.println(" does not exists");
            }
        }       
        vector_query= Math.sqrt(vector_query);
        
        score = arithmitis / (vector_doc * vector_query ) ; 
        a_doc.set_Score(score);
    }
     
    public void ScoreByOkapi(Document a_doc, double Avgdl){
        double k1 = 2.0;
        double b = 0.75;      
        double score = 0.0 ; 
        double similarity_tf = 0;
        AppearsInDoc Apr;
        //System.out.println("OKAPI B25, Meso metro documents: "+ Avgdl); 
        /*run threw all the posting file entries for this document regarding the query words*/
        for(Map.Entry <String,Word> w : Query_Words.entrySet()){
            //System.out.print("to tf ths lekshs "+w.get_Id()+" sto D" + a_doc.get_Path()+":  ");
            if( (Apr = w.getValue().get_Appearing_Doc(a_doc.get_Id())) != null ){
                //System.out.println(""+Apr.get_TF_IDF());
               similarity_tf =  Query_Words_Similarity.get(w.getKey()) * Apr.get_TF();
               
               score += w.getValue().get_IDF() * ( ( similarity_tf * (k1+1) )  /  (similarity_tf + k1*(1-b+ b*( a_doc.get_Norm() / Avgdl ) ) ) );
            }
            else{
                //System.out.println(" does not exists");
            }
        }               
        a_doc.set_Score(score);
    }
    /**
     * sets all the document's scores
     * @param RetrievalModel 
     */
    public void Update_Documents_Score(int RetrievalModel, double Avgdl) 
    {                                          /* 0: vector,1: okapi */
        if (RetrievalModel != 0 && RetrievalModel != 1)
            throw new IllegalArgumentException("Retriaval Model Flag is not 0 or 1");        
              
        Document doc;
        for(Map.Entry<Integer, Document> entry : Document_Results.entrySet()){
            doc = entry.getValue();
            
            if (RetrievalModel == 0){
                ScoreByVectorSpace(doc);/*set score by vector space*/
            }
            else if (RetrievalModel == 1){
                ScoreByOkapi(doc, Avgdl);/*set score by okapi*/
            }
            else{
                System.err.println("Error Not supported Retrieval Model");
            }
            //System.out.println("Exw ena document me score: "+ -doc.get_Score()+"\n");/*test print*/
            /*raise a little bit the score if another document has the same*/
            while ( Sorted_Document_Results.containsKey(-doc.get_Score()) ){
                doc.set_Score(doc.get_Score()+(double)0.0000000000001);
            }
            /*add the document to the sorted by score treemap*/
            Sorted_Document_Results.put(-doc.get_Score(), doc);
            /*add the document Snippet to the sorted by score treemap*/
            Sorted_Best_Positions.put(-doc.get_Score(), Manage_Best_Position(doc, false));
        }
    }
    
    private long Manage_Best_Position(Document a_doc, boolean raw_best_position_flag){
        AppearsInDoc Apr;
        /*<a_position,counter> */
        Map<Long,Integer> PositionsCounter = new HashMap<Long,Integer>(8);
        long Best_Position_Freq = -1;
        long Best_Position = -1;
        /*run throug all the words of the query*/
        for(Map.Entry <String,Word> w : Query_Words.entrySet()){
                /*only for similar words*/
                if (Query_Words_Similarity.get(w.getKey()) > 1 || raw_best_position_flag){
                    //System.out.println("H fash einai sto doc "+a_doc.get_Id()+": flag->"+raw_best_position_flag+" similarity-> "+Query_Words_Similarity.get(w.getKey()));//text print
                    /*for each query word inside the document iterate all the positions of a document*/
                    if( (Apr = w.getValue().get_Appearing_Doc(a_doc.get_Id())) != null ){
                        /*entry key = position, entry value = counter of most frequent position*/
                        for(Map.Entry<Integer, Long> entry : Apr.get_All_Positions().entrySet()){
                            if (!PositionsCounter.containsKey(entry.getValue()))
                                PositionsCounter.put(entry.getValue(), 1);
                            else
                                PositionsCounter.put(entry.getValue(), PositionsCounter.get(entry.getValue())+1);
                        }
                    }                    
                }

        }
        for(Map.Entry<Long,Integer> entry : PositionsCounter.entrySet()){
            //System.out.println("one more possible position");//test print
            if (Best_Position_Freq < entry.getValue()){/*compare by counter(value)*/
                Best_Position_Freq = entry.getValue();/*keep the best line byte_pointer(key)*/
                Best_Position = entry.getKey();
            }
        }
        
        if (Best_Position != -1){
            return Best_Position;
        }
        else{
            //System.out.println("Lets Find again with a true flag");//test print
            return Manage_Best_Position(a_doc, true);
        }
    }   
    
    private String Generate_Snippet(Document a_doc, long position){
        String Snippet_Result = "";
        String raw_snippet = "";
        try {
            RandomAccessFile a_doc_in = new RandomAccessFile(a_doc.get_Path(), "r");
            //System.out.println("In D"+a_doc.get_Id()+" position to seek for snippet: "+ position);//test print
            
            a_doc_in.seek(position);
            
            raw_snippet = a_doc_in.readLine();
            
            if (raw_snippet!= null && !raw_snippet.equals("")){// It is need until we fix the greek snippet Bug !!
                //System.out.println("In D"+a_doc.get_Id()+" I will survive1");//test print
                if (raw_snippet.length() < 150){
                    Snippet_Result = raw_snippet;
                }
                else{
                    Snippet_Result = raw_snippet.substring(0, 150);
                }
            }
            
            //System.out.println("In D"+a_doc.get_Id()+" I will survive2");/*test print*/

        }
        catch (IOException ex) {
            System.err.println("IO Exception in Generate Snippet "+ex.getMessage());
        }
        
        return Snippet_Result;
    }
    
    public TreeMap<Integer,AnswerItem> GenerateAnswer(){
        
        int index_of_path_name = 0, hit_id = 1;

        for(Map.Entry<Double, Document> entry : Sorted_Document_Results.entrySet()){
              index_of_path_name = entry.getValue().get_Path().lastIndexOf("\\") + 1;
            
            Hits.put(hit_id, new AnswerItem( entry.getValue().get_Path().substring(index_of_path_name),
                                             entry.getValue().get_Path().substring(0, index_of_path_name),
                                             entry.getValue().get_Type(),
                                             entry.getValue().get_Score(),
                                             Generate_Snippet( entry.getValue(), Sorted_Best_Positions.get(entry.getKey()) )
                                           )
                    );
            
            ++hit_id;
        }
        
        if (Sorted_Document_Results.isEmpty())
            Hits.put(hit_id, new AnswerItem("Scooby Doo does not have a clue, please try again with different words..."));
        
        return Hits;
    }    
    
    public Map<String,Word> get_Query_Words(){
        return Query_Words;
    }
    
    public Map<String,Double> get_Query_Words_Similarity(){
        return Query_Words_Similarity;
    }
    
    public int get_Query_Words_Num(){
        return User_Query_Words.length;
    }
    
    public Map<Integer,Document> get_Document_Results(){
        return Document_Results;
    }
    public Map<Double,Document> get_Sorted_Document_Results(){
        return Sorted_Document_Results;
    }
    public int get_Doc_Results_Num(){
        return Sorted_Document_Results.size();
    }
    
    public TreeMap<Integer,AnswerItem> get_Hits(){
        return Hits;
    }
    
    public String get_Inverted_Index_Path(){
        return Inverted_Index_Path;
    }
    
}
