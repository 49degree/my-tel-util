package com.xys.ecg.file;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xys.ecg.bean.XmlNodeEntity;
import com.xys.ecg.sqlite.CommDB;
import com.xys.ecg.sqlite.DBHelper;
public class EcgXmlFileOperate {
	
	private DBHelper dbHelper=null;
	private SQLiteDatabase sqldatabase=null;
    private String fileName = null;

    public EcgXmlFileOperate(String fileName ,Context context)
    {
    	this.fileName = fileName;
    	try
		{
			dbHelper=new DBHelper(context,CommDB.dataBaseName);
			sqldatabase=dbHelper.getWritableDatabase();
		}catch(Exception ex)
		{
			
		}
    }
    


    
    public XmlNodeEntity selectEcgXmlNode(String parentNodeName, String parentNodeAttributeValue,String childNodeName) 
    {
    	XmlNodeEntity xne = new XmlNodeEntity();
		Cursor cursor=sqldatabase.query(fileName,null, parentNodeName+"=? ",new String[]{""+parentNodeAttributeValue+""}, null, null, null);
		while(cursor.moveToNext())
		{
			xne.setParentNodeName(parentNodeName);
			xne.setParentNodeAttributeValue(parentNodeAttributeValue);
			xne.setChildNodeName(childNodeName);
			xne.setChildNodeAttributeValue(cursor.getString(cursor.getColumnIndex(childNodeName))) ;
		}
		cursor.close();

    	return xne;
    }
    
    public XmlNodeEntity selectEcgXmlNode(String currentNodeName) 
    {
    	XmlNodeEntity xne = new XmlNodeEntity();
		Cursor cursor=sqldatabase.query(fileName,null, null,null, null, null, null);
		while(cursor.moveToNext())
		{
			xne.setParentNodeName(currentNodeName);
			xne.setParentNodeAttributeValue(cursor.getString(cursor.getColumnIndex(currentNodeName)));

		}
		//cursor.close();

    	return xne;
    }
    
    
    public void updateEcgXmlCurrentNode(String currentNodeName, String currentNodeAttributeValue)
    {
	
		try
		{
		sqldatabase.execSQL("update "+ this.fileName +" set "+ currentNodeName.trim()+" = '" + currentNodeAttributeValue +"'" );

		}catch(Exception ex){
		                  	
		}
	
    }
    
    public void updateEcgXmlChildNode(String parentNodeName ,String parentNodeAttributeValue ,String childNodeName ,String childNodeAttributeValue) 
    {
		try
		{
			sqldatabase.execSQL("update "+ this.fileName +" set "+ childNodeName+"=" + childNodeAttributeValue +" where "+parentNodeName+"="+parentNodeAttributeValue );

		}catch(Exception ex){
			
		}
    }
    

    public void close()
    {
    	sqldatabase.close();
    }
  
    
}
