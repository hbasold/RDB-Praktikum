package main;
import java.sql.Statement;
import java.io.File;
import java.io.IOException;

import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import klassen.Database;
import klassen.Read;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;




public class Main {

	public static Statement stmt;

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException{
		Document document;
		Database db = new Database();
		
		
		db.openDatabase();
		
		
		//db.cleanDatabase();
		document = readXMLDocument("downloads/map.osm");
		
		Read read = new Read();
		read.readNode(document.getElementsByTagName("node"));
		read.readWay(document.getElementsByTagName("way"));
        
	}





public static Document readXMLDocument (String filename) throws ParserConfigurationException, SAXException, IOException
{
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder;
	Document document;
	builder = factory.newDocumentBuilder();
	document = builder.parse(new File(filename));
	
	return document;
}



/*
 * Sucht in der Tabelle nodes nach der entsprechenden ref und gibt lat und lon zurück
 */




    




}

