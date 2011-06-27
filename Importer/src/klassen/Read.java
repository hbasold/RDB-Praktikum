package klassen;

import java.sql.SQLException;
import java.util.Map;

import org.postgresql.geometric.PGpath;
import org.postgresql.geometric.PGpoint;
import org.postgresql.geometric.PGpolygon;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Read {

	SQLSession sqlsession;

	public Read() throws SQLException {
		Database db = Database.getInstance();
		sqlsession = new SQLSession(db.getConn());
}

	public Map<String, Integer> getViolated(){
	    return sqlsession.getViolated();
	}

    public Map<String, Integer> getInserted() {
        return sqlsession.getInserted();
    }

	public void readWay(NodeList way) throws SQLException{
		int strassenbahn=0;
		int eisenbahn=0;
		PGpath verlauf = new PGpath();


		int totalElementNode = way.getLength();
	    System.out.println("Total of way: " + totalElementNode);

	    for(int s=0; s<totalElementNode ; s++){
            System.out.println(s + "/" + totalElementNode);

	    	NodeObject nodeObject = new NodeObject();
	        Node readNode = way.item(s);
	        NodeList childList = readNode.getChildNodes();
	        nodeObject.setId(Integer.valueOf(readNode.getAttributes().getNamedItem("id").getNodeValue()));


	        //Childliste z.B. ref, name usw.
	        verlauf = null;
	        PGpoint[] points = new PGpoint[childList.getLength()];
	        int countPoints=0;
			for(int i=1; i<childList.getLength() ; i++){

		    	Node tagORnd= childList.item(i);

		    	//unverst�ndlicher Eintrag: wird einfach �bersprungen
		    	if (tagORnd.getNodeName().equals("#text")){continue;}

		    	if (tagORnd.getNodeName().equals("nd")){
		    		String ref = tagORnd.getAttributes().getNamedItem("ref").getNodeValue();
		    		sqlsession.getRefFromDB(ref, nodeObject);
		    		points[countPoints] = new PGpoint(nodeObject.getLat(),nodeObject.getLon());
		    		countPoints +=1;
		    	}
		    	else{
		    		if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("name")){
		    			nodeObject.setName(tagORnd.getAttributes().getNamedItem("v").getNodeValue());
				    }
		    		if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("addr:city")){
		    			nodeObject.setOrt(tagORnd.getAttributes().getNamedItem("v").getNodeValue());
				    }
		    		if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("addr:postcode")){
		    			nodeObject.setPlz(tagORnd.getAttributes().getNamedItem("v").getNodeValue());
				    }
		    		if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("addr:street")){
		    			nodeObject.setStrasse(tagORnd.getAttributes().getNamedItem("v").getNodeValue());
				    }
		    		if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("addr:housenumber")){
		    			nodeObject.setHausnummer(tagORnd.getAttributes().getNamedItem("v").getNodeValue());
				    }
		    		if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("amenity")){
		    			nodeObject.setNutzung(tagORnd.getAttributes().getNamedItem("v").getNodeValue());
				    }
		    		if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("maxspeed")){
		    			nodeObject.setMaxV(Integer.valueOf(tagORnd.getAttributes().getNamedItem("v").getNodeValue()));
				    }
		    		if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("highway")){
		    			String strassentyp = tagORnd.getAttributes().getNamedItem("v").getNodeValue();
		    			if (strassentyp.equals("primary") || strassentyp.equals("secondary")|| strassentyp.equals("tertiary")|| strassentyp.equals("residential")|| strassentyp.equals("living_street")|| strassentyp.equals("unclassified")|| strassentyp.equals("service")){
		    				nodeObject.setStrassentyp(tagORnd.getAttributes().getNamedItem("v").getNodeValue());
		    			}
				    }

					//Stra�enbahn
			        if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("railway") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("tram")){

			        	strassenbahn +=1;
			        	nodeObject.setVerlauf(setArray(countPoints, points));
			        	nodeObject.setBeschreibung("Strassenbahn");

			        }

			    	//Eisenbahn
			        if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("railway") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("rail")){
			        	eisenbahn +=1;
			        	nodeObject.setVerlauf(setArray(countPoints, points));
			        	nodeObject.setBeschreibung("Eisenbahn");

			        }

			    	//Fluss
			        if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("waterway") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("river")){
			        	nodeObject.setVerlauf(setArray(countPoints, points));
			        	nodeObject.setBeschreibung("Fluss");

			        }

			    	//Parkplatz
		        	if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("amenity") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("parking")){
		        		nodeObject.setPolygon(setArrayPolygon(countPoints, points));
		        		nodeObject.setBeschreibung("Parkplatz");
		        	}

		        	//See
		        	if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("natural") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("water")){
		        		nodeObject.setPolygon(setArrayPolygon(countPoints, points));
		        		nodeObject.setBeschreibung("See");

		        	}

		          	//Landnutzung
		        	if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("landuse")){
		        		nodeObject.setPolygon(setArrayPolygon(countPoints, points));
		        		nodeObject.setBeschreibung("Landnutzung");
		        	}

		          	//Park
		        	if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("leisure") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("park")){
		        		nodeObject.setPolygon(setArrayPolygon(countPoints, points));
		        		nodeObject.setBeschreibung("Park");
		        	}

		          	//Spielplatz
		        	if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("leisure") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("playground")){
		        		nodeObject.setPolygon(setArrayPolygon(countPoints, points));
		        		nodeObject.setBeschreibung("Spielplatz");
		        	}

		          	//Tunnel
		        	if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("tunnel") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("yes")){
		        		nodeObject.setPolygon(setArrayPolygon(countPoints, points));
		        		nodeObject.setBeschreibung("Tunnel");
		        	}

		          	//Br�cke
		        	if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("bridge") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("yes")){
		        		nodeObject.setPolygon(setArrayPolygon(countPoints, points));
		        		nodeObject.setBeschreibung("Br�cke");
		        	}

			        //H�user
			        if(tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("building")&& tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("yes")){

			        	//Array points besitzt null Objekte, welche im neuen Array points_new aussortiert werden
			        	PGpoint[] points_new = new PGpoint[countPoints];
			        	for (int p=0; p<countPoints;p++){
			        		points_new[p] = points[p];
			        	}


			        	nodeObject.setPolygon(setArrayPolygon(countPoints, points));
			        	sqlsession.writeHausInDB(nodeObject);
			        }


		    	}
			}
			if (nodeObject.getBeschreibung() != null){
				if (nodeObject.getBeschreibung().equals("Br�cke")) {sqlsession.writeBrueckeInDB(nodeObject);}
				if (nodeObject.getBeschreibung().equals("Tunnel")) {sqlsession.writeTunnelInDB(nodeObject);}
				if (nodeObject.getBeschreibung().equals("Spielplatz")) {sqlsession.writeSpielplatzInDB(nodeObject);}
				if (nodeObject.getBeschreibung().equals("Park")) {sqlsession.writeParkInDB(nodeObject);}
				if (nodeObject.getBeschreibung().equals("Landnutzung")) {sqlsession.writeLandnutzungInDB(nodeObject);}
				if (nodeObject.getBeschreibung().equals("See")) {sqlsession.writeSeeInDB(nodeObject);}
				if (nodeObject.getBeschreibung().equals("Parkplatz")) {sqlsession.writeParkplatzInDB(nodeObject);}
				if (nodeObject.getBeschreibung().equals("Fluss")) {sqlsession.writeFlussInDB(nodeObject);}
				if (nodeObject.getBeschreibung().equals("Eisenbahn")) {sqlsession.writeEisenbahnInDB(nodeObject);}
				if (nodeObject.getBeschreibung().equals("Eisenbahn")) {sqlsession.writeStrassenbahnInDB(nodeObject);}

			}
			if (nodeObject.getStrassentyp() != null){
			 	//Strassen
		    	//Array points besitzt null Objekte, welche im neuen Array points_new aussortiert werden
		    	PGpoint[] points_new = new PGpoint[countPoints];
		    	for (int p=0; p<countPoints;p++){
		    		points_new[p] = points[p];
		    	}

		    	verlauf = new PGpath(points_new,true);
		    	nodeObject.setVerlauf(verlauf);

		    	sqlsession.writeStrasseInDB(nodeObject);
	    	}
	    }
	}

	public void readNode(NodeList node) throws SQLException{
		int intampeln=0;
		int haltestelle=0;



		int totalElementNode = node.getLength();
	    System.out.println("Total of nodes: " + totalElementNode);

	    for(int s=0; s<totalElementNode ; s++){

	        Node readNode = node.item(s);
	        NodeList childList = readNode.getChildNodes();
	        NodeObject nodeObject = new NodeObject();
	        System.out.println(s + "/" + totalElementNode);
	        //Node Eintrag --> id, lat, lon werden in Tabelle Node gespeichert
	        sqlsession.writeNodesInDB(readNode);

			for(int i=1; i<childList.getLength() ; i++){

		    	Node tagORnd= childList.item(i);

		    	if (tagORnd.getNodeName().equals("#text")){continue; 	}

			    	if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("name")){
			    		nodeObject.setName(tagORnd.getAttributes().getNamedItem("v").getNodeValue());
			    	}

					nodeObject.setId(Integer.valueOf(readNode.getAttributes().getNamedItem("id").getNodeValue()));
					nodeObject.setLat(Double.valueOf(readNode.getAttributes().getNamedItem("lat").getNodeValue()));
					nodeObject.setLon(Double.valueOf(readNode.getAttributes().getNamedItem("lon").getNodeValue()));
		    		if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("highway") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("traffic_signals")){
		        		intampeln +=1;

		        		//Ampel
		        		PGpoint point = new PGpoint(nodeObject.getLat(), nodeObject.getLon());
		        		nodeObject.setPoint(point);
		        		sqlsession.writeAmpelInDB(nodeObject);
		        		}

		        	if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("crossing") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("traffic_signals")){
		        		intampeln +=1;
		        		//Ampel
		        		PGpoint point = new PGpoint(nodeObject.getLat(), nodeObject.getLon());
		        		nodeObject.setPoint(point);
		        		sqlsession.writeAmpelInDB(nodeObject);
		        		}

		        	//Bushaltestelle
		        	if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("highway") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("bus_stop")){
		        		haltestelle +=1;

		        		nodeObject.setHaltestellentyp("Bus");
		        		sqlsession.writeHaltestelleInDB(nodeObject);
		        	}

		        	//Tramhaltestelle
		        	if (tagORnd.getAttributes().getNamedItem("k").getNodeValue().equals("railway") && tagORnd.getAttributes().getNamedItem("v").getNodeValue().equals("tram_stop")){
		        		haltestelle+=1;
						nodeObject.setHaltestellentyp("Tram");
						sqlsession.writeHaltestelleInDB(nodeObject);
		        	}
			}
	    }
	}

	private static PGpolygon setArrayPolygon(int countPoints, PGpoint[] points) {
		//Array points besitzt null Objekte, welche im neuen Array points_new aussortiert werden
		PGpoint[] points_new = new PGpoint[countPoints];
		for (int p=0; p<countPoints;p++){
			points_new[p] = points[p];
		}

		PGpolygon polygon = new PGpolygon(points_new);
		return polygon;
	}


	private static PGpath setArray(int countPoints, PGpoint[] points) {
		//Array points besitzt null Objekte, welche im neuen Array points_new aussortiert werden
		PGpoint[] points_new = new PGpoint[countPoints];
		for (int p=0; p<countPoints;p++){
			points_new[p] = points[p];
		}

		PGpath verlauf = new PGpath(points_new,true);
		return verlauf;
	}
}
