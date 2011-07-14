package main;
import java.sql.Statement;
import java.io.File;
import java.io.IOException;

import java.sql.SQLException;
import java.util.Map.Entry;

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

	    if(args.length < 1){
	        System.err.println("Usage: importer [args] <path>");
	        System.exit(1);
	    }

		Document document;
		Database db = new Database();

//		String dbName = "dbprak";
//		String user = "postgres";
//		String password = "db";

        String dbName = "rdb_praktikum";
        String user = "henning";
        String password = "henning";

		db.openDatabase(dbName, user, password);


		db.cleanDatabase();
		document = readXMLDocument(args[0]);

		Read read = new Read();
		read.readNode(document.getElementsByTagName("node"));
		read.readWay(document.getElementsByTagName("way"));
		//read.readNode(document.getElementsByTagName("node"));

		int total = 0;
		System.out.println("Inserted Tuples:");
		for(Entry<String, Integer> t : read.getInserted().entrySet()){
		    System.out.println(t.getKey() + ": " + t.getValue());
		    total += t.getValue().intValue();
		}
		System.out.println("Total: " + total);

		System.out.println("Violated Assertions:");
		for(Entry<String, Integer> a : read.getViolated().entrySet()){
		    System.out.println(a.getKey() + ": " + a.getValue());
		}
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
 * Sucht in der Tabelle nodes nach der entsprechenden ref und gibt lat und lon zur�ck
 */









}

