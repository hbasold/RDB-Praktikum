package klassen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.w3c.dom.Node;

public class SQLSession  {

	public Connection conn;
	public SQLSession(Connection conn) {
	
		this.conn = conn;
	}
	
	public SQLSession(){
		
	}
	public void writeNodesInDB(Node readNode) {
		try {
			
			NodeObject nodeObject = new NodeObject();
			nodeObject.setId(Integer.valueOf(readNode.getAttributes().getNamedItem("id").getNodeValue()));
			nodeObject.setLat(Double.valueOf(readNode.getAttributes().getNamedItem("lat").getNodeValue()));
			nodeObject.setLon(Double.valueOf(readNode.getAttributes().getNamedItem("lon").getNodeValue()));
			
			PreparedStatement prepared_check = conn.prepareStatement("Select * from nodes where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			
			
			if (getRowCount(rs)==0) {
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO NODES (id,lat,lon) values(?,?,?)");
				prepared.setInt(1, nodeObject.getId());
				prepared.setDouble(2, nodeObject.getLat());
				prepared.setDouble(3, nodeObject.getLon());
				
				prepared.executeUpdate();
			}
		} catch (Exception e) {
			
		}
	}

	public void writeAmpelInDB(NodeObject nodeObject) {

		try {

		
			PreparedStatement prepared_check = conn.prepareStatement("Select * from Ampel where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			
			
			if (getRowCount(rs)==0) {
			
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO AMPEL (id,position) values(?,?)");
				prepared.setInt(1, nodeObject.getId());
				prepared.setObject(2, nodeObject.getPoint());
				
				int re = prepared.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde " + re + " Eintrag in die Tabelle Ampel vorgenommen!");		}
				prepared.close();
			}
			else
			{
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim schreiben in writeAmpelInDB()");
		}
	}
	
	
	public void writeHaltestelleInDB(NodeObject nodeObject) {
		try {
			//stmt = conn.createStatement();
			
			PreparedStatement prepared_check = conn.prepareStatement("Select * from haltestelle where id=?;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			
 
			if (getRowCount(rs)==0) {
			
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO Haltestelle (id,position, name, haltestellentyp) values(?,?,?,?)");
				prepared.setInt (1, nodeObject.getId());
				prepared.setObject(2, nodeObject.getPoint());
				prepared.setString(3, nodeObject.getName());
				prepared.setString(4, nodeObject.getHaltestellentyp());
				
				int re = prepared.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Haltestelle vorgenommen!");		}
				prepared.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim schreiben in writeHaltestelleInDB()");
		}
	}
		

	public void writeParkplatzInDB(NodeObject nodeObject) {
			try {
				//stmt = conn.createStatement();
				
				PreparedStatement prepared_check = conn.prepareStatement("Select * from Parkplatz where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				prepared_check.setInt(1, nodeObject.getId());
				ResultSet rs = prepared_check.executeQuery();
				

				if (getRowCount(rs)==0) {
				
					PreparedStatement prepared = conn.prepareStatement("INSERT INTO Parkplatz (id,umriss,name) values(?,?,?)");
					prepared.setInt (1, nodeObject.getId());
					prepared.setObject(2, nodeObject.getPolygon());
					prepared.setString(3, nodeObject.getName());
						
					
					int re = prepared.executeUpdate();
					if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Parkplatz vorgenommen!");		}
					prepared.close();
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Fehler beim schreiben in writeParkplatz()");
			}
		}
		
	public void writeParkInDB(NodeObject nodeObject) {
		try {
			
			PreparedStatement prepared_check = conn.prepareStatement("Select * from Park where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			

			if (getRowCount(rs)==0) {
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO Park (id,umriss,name) values(?,?,?)");
				prepared.setInt(1, nodeObject.getId());
				prepared.setObject(2, nodeObject.getPolygon());
				prepared.setString(3, nodeObject.getName());
				
				int re = prepared.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Park vorgenommen!");		}
				prepared.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim schreiben in writeParkInDB()");
		}
	}

	public void writeSeeInDB(NodeObject nodeObject) {
		try {
			PreparedStatement prepared_check = conn.prepareStatement("Select * from See where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			

			if (getRowCount(rs)==0) {
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO See (id,umriss,name) values(?,?,?)");
				prepared.setInt(1, nodeObject.getId());
				prepared.setObject(2, nodeObject.getPolygon());
				prepared.setString(3, nodeObject.getName());
				
				int re = prepared.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle See vorgenommen!");		}
				prepared.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Schreiben in writeSeeInDB()");
		}
	}

	public void writeLandnutzungInDB(NodeObject nodeObject) {
		try {
			PreparedStatement prepared_check = conn.prepareStatement("Select * from Landnutzung where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			

			if (getRowCount(rs)==0) {
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO Landnutzung (id,umriss) values(?,?)");
				prepared.setInt(1, nodeObject.getId());
				prepared.setObject(2, nodeObject.getPolygon());
				
				int re = prepared.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle See vorgenommen!");		}
				prepared.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Schreiben in writeLandnutzungInDB()");
		}
	}

	public void writeHausInDB(NodeObject nodeObject) {
		try {
			PreparedStatement prepared_check = conn.prepareStatement("Select * from haus where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			
			
			if (getRowCount(rs)==0) {
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO Haus (id,umriss, hausnummer, strasse, ort, plz, nutzung) values(?,?,?,?,?,?,?)");
				prepared.setInt(1, nodeObject.getId());
				prepared.setObject(2, nodeObject.getPolygon());
				prepared.setString(3, nodeObject.getHausnummer());
		
				prepared.setString(4, nodeObject.getStrasse());
				prepared.setString(5, nodeObject.getOrt());
				prepared.setString(6, nodeObject.getPlz());
				prepared.setString(7, nodeObject.getNutzung());
				
				
				int re = prepared.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Haus vorgenommen!");		}
				prepared.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Schreiben in writeHausInDB()");
		}
	}

	public void writeSpielplatzInDB(NodeObject nodeObject) {
		try {
			PreparedStatement prepared_check = conn.prepareStatement("Select * from Spielplatz where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			

			if (getRowCount(rs)==0) {
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO Spielplatz (id,umriss,name) values(?,?,?)");
				prepared.setInt(1, nodeObject.getId());
				prepared.setObject(2, nodeObject.getPolygon());
				prepared.setString(3, nodeObject.getName());
				
				int re = prepared.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Spielplatz vorgenommen!");		}
				prepared.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Schreiben in writeSpielplatzInDB()");
		}
	}

	public void writeBrueckeInDB(NodeObject nodeObject) {
		try {
			PreparedStatement prepared_check = conn.prepareStatement("Select * from Bruecke where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			

			if (getRowCount(rs)==0) {
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO Bruecke (id,verlauf,name,maxV) values(?,?,?,?)");
				prepared.setInt(1, nodeObject.getId());
				prepared.setObject(2, nodeObject.getVerlauf());
				prepared.setString(3, nodeObject.getName());
				prepared.setInt(4, nodeObject.getMaxV());

				int re = prepared.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Bruecke vorgenommen!");		}
				prepared.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Schreiben in writeBrueckeInDB()");
		}
	}

	public  void writeTunnelInDB(NodeObject nodeObject) {
		try {
			PreparedStatement prepared_check = conn.prepareStatement("Select * from Tunnel where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			

			if (getRowCount(rs)==0) {
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO Tunnel (id,verlauf,name,maxV) values(?,?,?,?)");
				prepared.setInt(1, nodeObject.getId());
				prepared.setObject(2, nodeObject.getVerlauf());
				prepared.setString(3, nodeObject.getName());
				prepared.setInt(4, nodeObject.getMaxV());

				int re = prepared.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Tunnel vorgenommen!");		}
				prepared.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Schreiben in writeTunnelInDB()");
		}
	}

	public void writeStrasseInDB(NodeObject nodeObject) {
		try {
			PreparedStatement prepared_check = conn.prepareStatement("Select * from strasse where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			
			
			if (getRowCount(rs)==0) {
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO Strasse (id,verlauf,strassentyp,name,maxV) values(?,?,?,?,?)");
				prepared.setInt(1, nodeObject.getId());
				prepared.setObject(2, nodeObject.getVerlauf());
				prepared.setString(3, nodeObject.getStrassentyp());
				prepared.setString(4, nodeObject.getName());
				prepared.setInt(5, nodeObject.getMaxV());
				
				int re = prepared.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Strasse vorgenommen!");		}
				prepared.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Schreiben in writeStrasseInDB()");
		}
	}

	public void writeFlussInDB(NodeObject nodeObject) {
		try {
			PreparedStatement prepared_check = conn.prepareStatement("Select * from strasse where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			
			
			if (getRowCount(rs)==0) {
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO Fluss (id,verlauf,name) values(?,?,?)");
				prepared.setInt(1, nodeObject.getId());
				prepared.setObject(2, nodeObject.getVerlauf());
				prepared.setString(3, nodeObject.getName());
		
				
		
				int re = prepared.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Fluss vorgenommen!");		}
				prepared.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Schreiben in writeFlussInDB()");
		}
	}

	public void writeEisenbahnInDB(NodeObject nodeObject) {
		try {
			PreparedStatement prepared_check = conn.prepareStatement("Select * from eisenbahn where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			
			
			if (getRowCount(rs)==0) {
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO Eisenbahn (id,verlauf,name) values(?,?,?)");
				prepared.setInt(1, nodeObject.getId());
				prepared.setObject(2, nodeObject.getVerlauf());
				prepared.setString(3, nodeObject.getName());
				
				int re = prepared.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Eisenbahn vorgenommen!");		}
				prepared.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Schreiben in writeEisenbahnInDB()");
		}
	}

	public void writeStrassenbahnInDB(NodeObject nodeObject) {
		try {
			PreparedStatement prepared_check = conn.prepareStatement("Select * from strassenbahn where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check.executeQuery();
			
			
			if (getRowCount(rs)==0) {
				PreparedStatement prepared = conn.prepareStatement("INSERT INTO Strassenbahn (id,verlauf) values(?,?)");
				prepared.setInt(1, nodeObject.getId());
				prepared.setObject(2, nodeObject.getVerlauf());
		
				
				int re = prepared.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Strassenbahn vorgenommen!");		}
				prepared.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Schreiben in writeStrassenbahnInDB()");
		}
	}
	
	public int getRowCount(ResultSet resultSet) throws SQLException {

		  int oldPosition = resultSet.getRow();
		  if(!resultSet.last())
		    return 0;
		  int rowCount = resultSet.getRow();
		  resultSet.absolute(oldPosition);
		  return rowCount;

		}
	
	public NodeObject getRefFromDB(String ref, NodeObject nodeObject) {
		
		try {
			PreparedStatement prepared_check = conn.prepareStatement("Select lat,lon from nodes where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			prepared_check.setInt(1, Integer.valueOf(ref));
			ResultSet rs = prepared_check.executeQuery();
			
			
			if (getRowCount(rs)>0) {
				rs.next();
				nodeObject.setLat(rs.getDouble(1));
				nodeObject.setLon(rs.getDouble(2));
			}
		} catch (Exception e) {
				e.printStackTrace();
		}
		
		return nodeObject;
	}
}
