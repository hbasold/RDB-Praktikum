package klassen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

public class SQLSession  {

	public Connection conn;

	private PreparedStatement prepared_check_node;
    private PreparedStatement prepared_insert_node;

    private PreparedStatement prepared_check_ampel;
    private PreparedStatement prepared_insert_ampel;

    private PreparedStatement prepared_check_haltestelle;
    private PreparedStatement prepared_insert_haltestelle;

    private PreparedStatement prepared_check_parkplatz;
    private PreparedStatement prepared_insert_parkplatz;

    private PreparedStatement prepared_check_park;
    private PreparedStatement prepared_insert_park;

    private PreparedStatement prepared_check_see;
    private PreparedStatement prepared_insert_see;

    private PreparedStatement prepared_check_landnutzung;
    private PreparedStatement prepared_insert_landnutzung;

    private PreparedStatement prepared_check_haus;
    private PreparedStatement prepared_insert_haus;

    private PreparedStatement prepared_check_strassenbahn;
    private PreparedStatement prepared_insert_strassenbahn;

    private PreparedStatement prepared_check_strasse;
    private PreparedStatement prepared_insert_strasse;

    private PreparedStatement prepared_get_node_position;

    private Pattern assertionPattern = Pattern.compile(".* ASSERTION (\\w+) violated!");
	private HashMap<String, Integer> violatedAssertions = new HashMap<String, Integer>();

	private HashMap<String, Integer> inserted = new HashMap<String, Integer>();

	public SQLSession(Connection conn) throws SQLException {

		this.conn = conn;
		prepared_check_node = conn.prepareStatement("Select * from nodes where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        prepared_insert_node = conn.prepareStatement("INSERT INTO NODES (id,lat,lon) values(?,?,?)");
        prepared_check_ampel = conn.prepareStatement("Select * from Ampel where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        prepared_insert_ampel = conn.prepareStatement("INSERT INTO AMPEL (id,position) values(?,?)");
        prepared_check_haltestelle = conn.prepareStatement("Select * from haltestelle where id=?;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        prepared_insert_haltestelle = conn.prepareStatement("INSERT INTO Haltestelle (id,position, name, haltestellentyp) values(?,?,?,?)");
        prepared_check_parkplatz = conn.prepareStatement("Select * from Parkplatz where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        prepared_insert_parkplatz = conn.prepareStatement("INSERT INTO Parkplatz (id,umriss,name) values(?,?,?)");
        prepared_check_park = conn.prepareStatement("Select * from Park where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        prepared_insert_park = conn.prepareStatement("INSERT INTO Park (id,umriss,name) values(?,?,?)");
        prepared_check_see = conn.prepareStatement("Select * from See where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        prepared_insert_see = conn.prepareStatement("INSERT INTO See (id,umriss,name) values(?,?,?)");
        prepared_check_landnutzung = conn.prepareStatement("Select * from Landnutzung where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        prepared_insert_landnutzung = conn.prepareStatement("INSERT INTO Landnutzung (id,umriss) values(?,?)");
        prepared_check_haus = conn.prepareStatement("Select * from haus where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        prepared_insert_haus = conn.prepareStatement("INSERT INTO Haus (id,umriss, hausnummer, strasse, ort, plz, nutzung) values(?,?,?,?,?,?,?)");
        prepared_check_strassenbahn = conn.prepareStatement("Select * from strassenbahn where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        prepared_insert_strassenbahn = conn.prepareStatement("INSERT INTO Strassenbahn (id,verlauf) values(?,?)");
        prepared_check_strasse = conn.prepareStatement("Select * from strasse where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        prepared_insert_strasse = conn.prepareStatement("INSERT INTO Strasse (id,verlauf,strassentyp,name,maxV) values(?,?,?,?,?)");
        prepared_get_node_position = conn.prepareStatement("Select lat,lon from nodes where id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

   private String getAssertion(String message) {
        Matcher m = assertionPattern.matcher(message);
        if(m.find()){
            return m.group(1);
        }
        else{
            return "";
        }
    }

   private void updateAssertion(String assertion) {
       if(violatedAssertions.containsKey(assertion)){
           violatedAssertions.put(assertion, violatedAssertions.get(assertion) + 1);
       }
       else{
           violatedAssertions.put(assertion, 0);
       }
   }

   public Map<String, Integer> getViolated() {
       return violatedAssertions;
   }

   private void updateInserted(String relation) {
       if(inserted.containsKey(relation)){
           inserted.put(relation, inserted.get(relation) + 1);
       }
       else{
           inserted.put(relation, 0);
       }
   }

   public Map<String, Integer> getInserted() {
       return inserted;
   }

	public void writeNodesInDB(Node readNode) throws SQLException {
			NodeObject nodeObject = new NodeObject();
			nodeObject.setId(Integer.valueOf(readNode.getAttributes().getNamedItem("id").getNodeValue()));
			nodeObject.setLat(Double.valueOf(readNode.getAttributes().getNamedItem("lat").getNodeValue()));
			nodeObject.setLon(Double.valueOf(readNode.getAttributes().getNamedItem("lon").getNodeValue()));

			prepared_check_node.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check_node.executeQuery();

			if (getRowCount(rs)==0) {
				prepared_insert_node.setInt(1, nodeObject.getId());
				prepared_insert_node.setDouble(2, nodeObject.getLat());
				prepared_insert_node.setDouble(3, nodeObject.getLon());

				prepared_insert_node.executeUpdate();
			}
	}

	public void writeAmpelInDB(NodeObject nodeObject) {

		try {
			prepared_check_ampel.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check_ampel.executeQuery();

			if (getRowCount(rs)==0) {
				prepared_insert_ampel.setInt(1, nodeObject.getId());
				prepared_insert_ampel.setObject(2, nodeObject.getPoint());

				int re = prepared_insert_ampel.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde " + re + " Eintrag in die Tabelle Ampel vorgenommen!");		}
				else {
				    updateInserted("Ampel");
				}
			}
			else
			{
				rs.close();
			}
		} catch (SQLException e) {
	        String assertion = getAssertion(e.getMessage());
	        updateAssertion(assertion);
	        //System.err.println("Fehler beim Erstellen in Relation Ampel: " + e.getMessage());
		}
	}

    public void writeHaltestelleInDB(NodeObject nodeObject) {
		try {
			//stmt = conn.createStatement();

			prepared_check_haltestelle.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check_haltestelle.executeQuery();

			if (getRowCount(rs)==0) {
				prepared_insert_haltestelle.setInt (1, nodeObject.getId());
				prepared_insert_haltestelle.setObject(2, nodeObject.getPoint());
				prepared_insert_haltestelle.setString(3, nodeObject.getName());
				prepared_insert_haltestelle.setString(4, nodeObject.getHaltestellentyp());

				int re = prepared_insert_haltestelle.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Haltestelle vorgenommen!");		}
                else {
                    updateInserted("Haltestelle");
                }
			}

		} catch (SQLException e) {
            String assertion = getAssertion(e.getMessage());
            updateAssertion(assertion);
            //System.err.println("Fehler beim Erstellen in Relation Haltestelle: " + e.getMessage());
		}
	}


	public void writeParkplatzInDB(NodeObject nodeObject) {
			try {
				//stmt = conn.createStatement();

				prepared_check_parkplatz.setInt(1, nodeObject.getId());
				ResultSet rs = prepared_check_parkplatz.executeQuery();

				if (getRowCount(rs)==0) {
					prepared_insert_parkplatz.setInt (1, nodeObject.getId());
					prepared_insert_parkplatz.setObject(2, nodeObject.getPolygon());
					prepared_insert_parkplatz.setString(3, nodeObject.getName());

					int re = prepared_insert_parkplatz.executeUpdate();
					if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Parkplatz vorgenommen!");		}
	                else {
	                    updateInserted("Parkplatz");
	                }
				}

			} catch (SQLException e) {
	            String assertion = getAssertion(e.getMessage());
	            updateAssertion(assertion);
	            //System.err.println("Fehler beim Erstellen in Relation Parkplatz: " + e.getMessage());
			}
		}

	public void writeParkInDB(NodeObject nodeObject) {
		try {

			prepared_check_park.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check_park.executeQuery();

			if (getRowCount(rs)==0) {
				prepared_insert_park.setInt(1, nodeObject.getId());
				prepared_insert_park.setObject(2, nodeObject.getPolygon());
				prepared_insert_park.setString(3, nodeObject.getName());

				int re = prepared_insert_park.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Park vorgenommen!");		}
                else {
                    updateInserted("Park");
                }
			}
		} catch (SQLException e) {
            String assertion = getAssertion(e.getMessage());
            updateAssertion(assertion);
            //System.err.println("Fehler beim Erstellen in Relation Park: " + e.getMessage());
		}
	}

	public void writeSeeInDB(NodeObject nodeObject) {
		try {
			prepared_check_see.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check_see.executeQuery();

			if (getRowCount(rs)==0) {
				prepared_insert_see.setInt(1, nodeObject.getId());
				prepared_insert_see.setObject(2, nodeObject.getPolygon());
				prepared_insert_see.setString(3, nodeObject.getName());

				int re = prepared_insert_see.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle See vorgenommen!");		}
                else {
                    updateInserted("See");
                }
			}
		} catch (SQLException e) {
            String assertion = getAssertion(e.getMessage());
            updateAssertion(assertion);
            //System.err.println("Fehler beim Erstellen in Relation See: " + e.getMessage());
		}
	}

	public void writeLandnutzungInDB(NodeObject nodeObject) {
		try {
			prepared_check_landnutzung.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check_landnutzung.executeQuery();

			if (getRowCount(rs)==0) {
				prepared_insert_landnutzung.setInt(1, nodeObject.getId());
				prepared_insert_landnutzung.setObject(2, nodeObject.getPolygon());

				int re = prepared_insert_landnutzung.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle See vorgenommen!");		}
                else {
                    updateInserted("Landnutzung");
                }
			}
		} catch (SQLException e) {
            String assertion = getAssertion(e.getMessage());
            updateAssertion(assertion);
            //System.err.println("Fehler beim Erstellen in Relation Landnutzung: " + e.getMessage());
		}
	}

	public void writeHausInDB(NodeObject nodeObject) {
		try {
			prepared_check_haus.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check_haus.executeQuery();

			if (getRowCount(rs)==0) {
				prepared_insert_haus.setInt(1, nodeObject.getId());
				prepared_insert_haus.setObject(2, nodeObject.getPolygon());
				prepared_insert_haus.setString(3, nodeObject.getHausnummer());

				prepared_insert_haus.setString(4, nodeObject.getStrasse());
				prepared_insert_haus.setString(5, nodeObject.getOrt());
				prepared_insert_haus.setString(6, nodeObject.getPlz());
				prepared_insert_haus.setString(7, nodeObject.getNutzung());

				int re = prepared_insert_haus.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Haus vorgenommen!");		}
                else {
                    updateInserted("Haus");
                }
			}

		} catch (SQLException e) {
            String assertion = getAssertion(e.getMessage());
            updateAssertion(assertion);
			//System.err.println("Fehler beim Erstellen in Relation Haus: " + e.getMessage());
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
                else {
                    updateInserted("Spielplatz");
                }
				prepared.close();
			}

		} catch (SQLException e) {
            String assertion = getAssertion(e.getMessage());
            updateAssertion(assertion);
            //System.err.println("Fehler beim Erstellen in Relation Spielplatz: " + e.getMessage());
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
                else {
                    updateInserted("Bruecke");
                }
				prepared.close();
			}

		} catch (SQLException e) {
            String assertion = getAssertion(e.getMessage());
            updateAssertion(assertion);
            //System.err.println("Fehler beim Erstellen in Relation Bruecke: " + e.getMessage());
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
                else {
                    updateInserted("Tunnel");
                }
				prepared.close();
			}
		} catch (SQLException e) {
            String assertion = getAssertion(e.getMessage());
            updateAssertion(assertion);
            //System.err.println("Fehler beim Erstellen in Relation Tunnel: " + e.getMessage());
		}
	}

	public void writeStrasseInDB(NodeObject nodeObject) {
		try {
			prepared_check_strasse.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check_strasse.executeQuery();

			if (getRowCount(rs)==0) {
				prepared_insert_strasse.setInt(1, nodeObject.getId());
				prepared_insert_strasse.setObject(2, nodeObject.getVerlauf());
				prepared_insert_strasse.setString(3, nodeObject.getStrassentyp());
				prepared_insert_strasse.setString(4, nodeObject.getName());
				prepared_insert_strasse.setInt(5, nodeObject.getMaxV());

				int re = prepared_insert_strasse.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Strasse vorgenommen!");		}
                else {
                    updateInserted("Strasse");
                }
			}

		} catch (SQLException e) {
            String assertion = getAssertion(e.getMessage());
            updateAssertion(assertion);
            //System.err.println("Fehler beim Erstellen in Relation Strasse: " + e.getMessage());
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
                else {
                    updateInserted("Fluss");
                }
				prepared.close();
			}
		} catch (SQLException e) {
            String assertion = getAssertion(e.getMessage());
            updateAssertion(assertion);
            //System.err.println("Fehler beim Erstellen in Relation Fluss: " + e.getMessage());
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
                else {
                    updateInserted("Eisenbahn");
                }
				prepared.close();
			}
		} catch (SQLException e) {
            String assertion = getAssertion(e.getMessage());
            updateAssertion(assertion);
            //System.err.println("Fehler beim Erstellen in Relation Eisenbahn: " + e.getMessage());
		}
	}

	public void writeStrassenbahnInDB(NodeObject nodeObject) {
		try {
			prepared_check_strassenbahn.setInt(1, nodeObject.getId());
			ResultSet rs = prepared_check_strassenbahn.executeQuery();

			if (getRowCount(rs)==0) {
				prepared_insert_strassenbahn.setInt(1, nodeObject.getId());
				prepared_insert_strassenbahn.setObject(2, nodeObject.getVerlauf());

				int re = prepared_insert_strassenbahn.executeUpdate();
				if (re <= 0) {System.out.println("Es wurde kein Eintrag in die Tabelle Strassenbahn vorgenommen!");		}
                else {
                    updateInserted("Strassenbahn");
                }
			}

		} catch (SQLException e) {
            String assertion = getAssertion(e.getMessage());
            updateAssertion(assertion);
            //System.err.println("Fehler beim Erstellen in Relation Strassenbahn: " + e.getMessage());
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
			prepared_get_node_position.setInt(1, Integer.valueOf(ref));
			ResultSet rs = prepared_get_node_position.executeQuery();

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
