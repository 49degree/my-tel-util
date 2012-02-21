package com.custom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class XMLHandler extends DefaultHandler {  

    
 

    Map<String,String[]> appInfo=null;  

   

    // 记录出现次数  

    int findCount = 0;  

    public XMLHandler() {  
        super();  
        this.appInfo=new HashMap<String,String[]>(5);
    }  

    /*  

     * 文档结束时触发  

     */  

    @Override  

    public void endDocument() throws SAXException {  

        //Log.i("yao", "文档解析结束");  
        super.endDocument();  

    }  

   

    /*  

     * 文档开始时触发  

     */  

    @Override  

    public void startDocument() throws SAXException {  

        //Log.i("yao", "文档解析开始"); 
        super.startDocument();  

    }  

   

    /*  

     * 元素开始时触发  

     */  
    String packageName = "";
    String[] appInfoArt = null;
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {  
    	super.startElement(uri, localName, qName, attributes); 
        //Log.i("yao", qName);
        if (qName.equals("entity")) {  
        	if(!packageName.equals("")&&appInfoArt!=null){
        		appInfo.put(packageName, appInfoArt);
        		
        	}
        		
        	appInfoArt = new String[5];
        	
        }else if (qName.equals("prop")) { 
        	String prop = attributes.getValue("name");
        	if(prop.equals("id")){
        		appInfoArt[0] =  attributes.getValue("value");
        	}else if(prop.equals("url")){
        		appInfoArt[1] =  attributes.getValue("value");
        	}else if(prop.equals("name")){
        		appInfoArt[2] =  attributes.getValue("value");
        	}else if(prop.equals("package")){
        		appInfoArt[3] =  attributes.getValue("value");
        		packageName = appInfoArt[3] ;
        	}else if(prop.equals("activity")){
        		appInfoArt[4] =  attributes.getValue("value");
        	}
        }   
    }  

   

    /*  

     * 元素结束时触发  

     */  

    @Override  

    public void endElement(String uri, String localName, String qName) throws SAXException {  

        //Log.i("yao", "元素解析结束");  

        super.endElement(uri, localName, qName);  

    }  

   

    /*  

     * 读取元素内容  

     */ 

    @Override 

    public void characters(char[] ch, int start, int length) throws SAXException {  

        super.characters(ch, start, length);  

    }  
    
    public Map<String,String[]> getAppInfo(){

    	return this.appInfo;
    }

}
