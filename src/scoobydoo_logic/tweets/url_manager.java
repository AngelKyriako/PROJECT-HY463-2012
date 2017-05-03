/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoobydoo_logic.tweets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class url_manager {

    
  private String startTag = "<title>";
  private String endTag = "</title>";
  private int startTagLength = startTag.length();
  
  public ArrayList<String> get_title(String title)
  {
        ArrayList<String> t = new ArrayList();
        String str[] = title.split("[-.() ]+");
        for(int i = 0; i < str.length; i++) {
                t.add(str[i]);
        }
        return t;
  }
  
  public String GetTitle( URL theURL )
  {
    BufferedReader bufReader;
    String line;
    boolean foundStartTag = false;
    boolean foundEndTag = false;
    int startIndex, endIndex;
    String title = "";
    
    try
    {
    
      bufReader = new BufferedReader( new InputStreamReader(theURL.openStream()) );
      while( (line = bufReader.readLine()) != null && !foundEndTag)
      {
      
        if( !foundStartTag && (startIndex = line.toLowerCase().indexOf(startTag)) != -1 )
        {
          foundStartTag = true;
        }
        else
        {
          startIndex = -startTagLength;
        }
        
        if( foundStartTag && (endIndex = line.toLowerCase().indexOf(endTag)) != -1 )
        {
          foundEndTag = true;
        }
        else
        {
          endIndex = line.length();
        }
       
        if( foundStartTag || foundEndTag )
        {
          title += line.substring( startIndex + startTagLength, endIndex );
        }
      }
    
      bufReader.close();
    
     
 
      if( title.length() > 0 )
      {
        System.out.println("Title: "+title); //ektupwsi titlou 
      }
      else
      {
        System.out.println("No title found in document.");
      }
      
    }
    catch( IOException e )
    {
      System.out.println( "GetTitle.GetTitle - error opening or reading URL: " + e );
    }
    return title;
  }
   
}
