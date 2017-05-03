/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_logic.documents;

/**
 *
 * @author Administrator
 */
public class AnswerItem {
    
    private String name;
    private String path;
    private String type;
    private double score;
    private String text;
    
    public AnswerItem(String text){
        name = text;
        path ="";
        type = "";
        score = -1;
        text = "";        
    }
            
    public AnswerItem(String a_name, String a_path, String a_type, double a_score, String a_text){
        name = a_name;
        path = a_path;
        type = a_type;
        score = a_score;
        text = a_text;        
    }
    
    public String get_name(){
        return name;
    }
    public String get_path(){
        return path;
    }
    public String get_type(){
        return type;
    }
    public double get_score(){
        return score;
    }
    public String get_text(){
        return text;
    }
    
    @Override
    public String toString(){
          String hits = "";
        
        if (get_score() != -1){
            hits += "<h2><font color=#0000FF>"+get_name()+"</font></h2>";
            hits += "<font color=#0000FF><b>Scored:</font></b><font color=#00000> "+get_score()+"<br>";
            hits += "<font color=#0000FF><b>Type:</font></b><font color=#00000> "+get_type()+"<br>";            
            hits += "<font color=#0000FF><b>Location:</font></b> <br><font color=#00000>"+get_path()+"<br>";
            hits += "<font color=#0000FF><b>Take a look:</font></b><br><font color=#00000>"+get_text()+"<br><br>";
        }
        else if (!get_name().isEmpty()){
            hits += "<br><h1><font color=#0000FF>Answers for query:"+get_name()+"</font></h1><br>";
        }
        return hits;
    }        
}
