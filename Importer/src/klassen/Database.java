package klassen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
	public static Connection conn;
	public PreparedStatement prepared;
	private static Database instance = null;
	
	public Connection getConn() {
		return conn;
	}


	public void setConn(Connection conn) {
		Database.conn = conn;
	}


	public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
	
	private void checkDBAvailable() throws SQLException {
		
		prepared = conn.prepareStatement("select count(*) from pg_catalog.pg_database where datname = 'dbprak'", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = prepared.executeQuery();
		rs.next();

		if (rs.getInt("count")==0)
		{
			createDatabase();
			createTables();
		}
		prepared.close();
	}


	private void createTables() throws SQLException {
		conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/dbprak","postgres","db");
		setConn(conn);
		
		prepared = conn.prepareStatement("CREATE TABLE ampel(id integer,position point) WITH (OIDS=FALSE); ALTER TABLE ampel OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE bruecke(verlauf path,name character varying(100),maxv integer,id integer) WITH (OIDS=FALSE); ALTER TABLE bruecke OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE eisenbahn(verlauf path,name character varying(100),id integer) WITH (OIDS=FALSE); ALTER TABLE eisenbahn OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE fluss(verlauf path,name character varying(100),id integer) WITH (OIDS=FALSE); ALTER TABLE fluss OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE haltestelle(position point,haltestellentyp character varying(10),name character varying(100),id integer) WITH (OIDS=FALSE); ALTER TABLE haltestelle OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE haus(umriss polygon,ort character varying(100),plz character varying(10),nutzung character varying(100),id integer,hausnummer character(20),strasse character(100)) WITH (OIDS=FALSE); ALTER TABLE haus OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE landnutzung(umriss polygon,id integer) WITH (OIDS=FALSE); ALTER TABLE landnutzung OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE nodes(id integer,lat double precision,lon double precision) WITH (OIDS=FALSE); ALTER TABLE nodes OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE park(umriss polygon,name character varying(100),id integer) WITH (OIDS=FALSE); ALTER TABLE park OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE parkplatz(name character varying(100),id integer,umriss polygon) WITH (OIDS=FALSE); ALTER TABLE parkplatz OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE see(umriss polygon,name character varying(100),id integer) WITH (OIDS=FALSE); ALTER TABLE see OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE spielplatz(umriss polygon,name character varying(100),id integer) WITH (OIDS=FALSE); ALTER TABLE spielplatz OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE strasse(verlauf path, strassentyp character varying(20),name character varying(100),maxv integer,id integer) WITH (OIDS=FALSE); ALTER TABLE strasse OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE strassenbahn(verlauf path,id integer) WITH (OIDS=FALSE); ALTER TABLE strassenbahn OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
		
		prepared = conn.prepareStatement("CREATE TABLE tunnel(verlauf path,name character varying(100),maxv integer,id integer) WITH (OIDS=FALSE); ALTER TABLE tunnel OWNER TO postgres;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
	}


	private void createDatabase() throws SQLException {
		prepared = conn.prepareStatement("CREATE DATABASE dbprak WITH OWNER = postgres ENCODING = 'UTF8' TABLESPACE = pg_default LC_COLLATE = 'German, Germany' LC_CTYPE = 'German, Germany' CONNECTION LIMIT = -1;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		prepared.execute();
		prepared.close();
	}


	private void cleanDatabase() {
		PreparedStatement prepared;
		try {
			prepared = conn.prepareStatement("Delete from strassenbahn");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from nodes");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from ampel");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from bruecke");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from eisenbahn");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from fluss");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from haltestelle");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from haus");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from landnutzung");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from park");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from see");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from parkplatz");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from spielplatz");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from strasse");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from strassenbahn");
			prepared.execute();
			prepared.close();
			
			prepared = conn.prepareStatement("Delete from tunnel");
			prepared.execute();
			prepared.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void openDatabase () throws ClassNotFoundException, SQLException
	{
		
		Class.forName("org.postgresql.Driver");
		String url= "jdbc:postgresql://localhost:5432/dbprak";
		try {
			conn= DriverManager.getConnection(url,"postgres","db");
			setConn(conn);
			checkDBAvailable();
		} catch (SQLException e) 
		{			 
		System.out.println("Connection Failed! Check output console");
		e.printStackTrace();
		
		return;
		}
		
	}
}
