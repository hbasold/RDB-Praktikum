package main;
import java.sql.Statement;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.lang.model.element.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import dbObjects.NodeObject;

import org.postgresql.geometric.PGcircle;
import org.postgresql.geometric.PGpoint;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;



public class Main {
	

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException{
		Document document;

		openDatabase();
		document = readXMLDocument("downloads/map.osm");
		
		readNode(document.getElementsByTagName("node"));
		readWay(document.getElementsByTagName("way"));
        
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

public static void openDatabase () throws ClassNotFoundException, SQLException
{
	Class.forName("org.postgresql.Driver");
	String url= "jdbc:postgresql://localhost:5432/test";
	Connection conn= DriverManager.getConnection(url,"postgres","blade11*");
	Statement stmt = conn.createStatement();
	//stmt.execute("create table ampel (position Point)");
	//stmt.close();
	
}

public static void insertCircle(Connection conn) throws SQLException{
	// Konstruktiondes Kreisesauscenter und radius
	PGpoint center = new PGpoint(1, 2.5);
	double radius = 4;
	PGcircle circle = new PGcircle(center, radius); 
	// vorbereitendes Einfuegensmit"?" alsParameter
	PreparedStatement ps= conn.prepareStatement("INSERT INTO geomtest(mycirc) VALUES (?)");
	// setzendes Parameters und ausfuehrenderAnweisung
	ps.setObject(1, circle);
	ps.executeUpdate();
	ps.close();
	
}



private void retrieveCircle(Connection conn) throws SQLException{
Statement stmt = conn.createStatement();
// Anfrage fuer Kreis und Kreisflaeche
ResultSet rs= stmt.executeQuery(
"SELECT mycirc, area(mycirc) FROM geomtest");
rs.next();
// Kreisdatenholenper getObjectund castenauf PGcircle
PGcircle circle = (PGcircle)rs.getObject(1);
// berechneteKreisflaecheholenper getDouble
double area = rs.getDouble(2);
// center und radius extrahieren
PGpoint center = circle.center;
double radius = circle.radius;
// alleDatenausgeben
System.out.println("Center (X, Y) = (" + center.x+ ", " + center.y+ ")");
System.out.println("Radius = " + radius);
System.out.println("Area = " + area);
}

@SuppressWarnings("unused")
private static void readNode(NodeList node){
	String k;
	int intampeln=0;
	int haltestelle=0;
	NodeObject nodeObject = new NodeObject();

	int totalElementWays = node.getLength();
    System.out.println("Total of ways: " + totalElementWays);

    for(int s=0; s<totalElementWays ; s++){

        Node firstNode = node.item(s);
        NodeList childList = firstNode.getChildNodes();
        
		String id = firstNode.getAttributes().getNamedItem("id").getNodeValue();
        int totalElementChild = childList.getLength();
        nodeObject.setId(firstNode.getAttributes().getNamedItem("id").getNodeValue());
		nodeObject.setLat(firstNode.getAttributes().getNamedItem("lat").getNodeValue());
		nodeObject.setLon(firstNode.getAttributes().getNamedItem("lon").getNodeValue());
        
        
        for(int i=0; i<totalElementChild ; i++){
        	
        	Node tagORnd= childList.item(i);
    		if (tagORnd.getNodeName().equals("tag") && tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("highway") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("traffic_signals")){
        		intampeln +=1;

        		}
        	if (tagORnd.getNodeName().equals("tag") && tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("crossing") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("traffic_signals")){
        		intampeln +=1;
        		}
        
        	if (tagORnd.getNodeName().equals("tag") && tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("highway") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("bus_stop")){
        		haltestelle +=1;

        		}
        }
        }
    
    System.out.println("Total of ampeln: " + intampeln);
    }
   


private static void readWay(NodeList node){
	String k;
	int intampeln=0;
	int haltestelle=0;
	NodeObject nodeObject = new NodeObject();

	int totalElementWays = node.getLength();
    System.out.println("Total of ways: " + totalElementWays);

    for(int s=0; s<totalElementWays ; s++){

        Node firstNode = node.item(s);
        NodeList childList = firstNode.getChildNodes();
        
		String id = firstNode.getAttributes().getNamedItem("id").getNodeValue();
        int totalElementChild = childList.getLength();
        nodeObject.setId(firstNode.getAttributes().getNamedItem("id").getNodeValue());
		nodeObject.setLat(firstNode.getAttributes().getNamedItem("lat").getNodeValue());
		nodeObject.setLon(firstNode.getAttributes().getNamedItem("lon").getNodeValue());
        
        
        for(int i=0; i<totalElementChild ; i++){
        	
        	Node tagORnd= childList.item(i);
    		if (tagORnd.getNodeName().equals("tag") && tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("highway") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("traffic_signals")){
        		intampeln +=1;

        		}
        	if (tagORnd.getNodeName().equals("tag") && tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("crossing") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("traffic_signals")){
        		intampeln +=1;
        		}
        
        	if (tagORnd.getNodeName().equals("tag") && tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("highway") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("bus_stop")){
        		haltestelle +=1;

        		}
        	
        
        }
        }
    
    System.out.println("Total of ampeln: " + intampeln);
    }
    

	
	
}

