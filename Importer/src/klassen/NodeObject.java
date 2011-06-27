package klassen;

import org.postgresql.geometric.PGpath;
import org.postgresql.geometric.PGpoint;
import org.postgresql.geometric.PGpolygon;

public class NodeObject {

private int id,maxV;
private double lat;
private double lon;
private PGpoint point;
private PGpolygon umriss;
private PGpath verlauf;
private String haltestellentyp;
private String name;
private String hausnummer;
private String ort;
private String plz;
private String nutzung;
private String strasse,strassentyp;
private String beschreibung;




public void setId(int id) {
	this.id = id;
}
public int getId() {
	return id;
}
public void setLat(double lat) {
	this.lat = lat;
}
public double getLat() {
	return lat;
}
public void setLon(double lon) {
	this.lon = lon;
}
public double getLon() {
	return lon;
}
public void setPoint(PGpoint point) {
	this.point = point;
}
public PGpoint getPoint() {
	return point;
}
public void setName(String name) {
	this.name = name;
}
public String getName() {
	return name;
}
public void setHaltestellentyp(String haltestellentyp) {
	this.haltestellentyp = haltestellentyp;
}
public String getHaltestellentyp() {
	return haltestellentyp;
}
public void setPolygon(PGpolygon polygon) {
	this.umriss = polygon;
}
public PGpolygon getPolygon() {
	return umriss;
}
public void setNutzung(String nutzung) {
	this.nutzung = nutzung;
}
public String getNutzung() {
	return nutzung;
}
public void setPlz(String plz) {
	this.plz = plz;
}
public String getPlz() {
	return plz;
}
public void setOrt(String ort) {
	this.ort = ort;
}
public String getOrt() {
	return ort;
}
public void setHausnummer(String hausnummer) {
	this.hausnummer = hausnummer;
}
public String getHausnummer() {
	return hausnummer;
}
public void setStrasse(String strasse) {
	this.strasse = strasse;
}
public String getStrasse() {
	return strasse;
}
public void setMaxV(int maxV) {
	this.maxV = maxV;
}
public int getMaxV() {
	return maxV;
}
public void setVerlauf(PGpath verlauf) {
	this.verlauf = verlauf;
}
public PGpath getVerlauf() {
	return verlauf;
}
public void setStrassentyp(String strassentyp) {
	this.strassentyp = strassentyp;
}
public String getStrassentyp() {
	return strassentyp;
}
public void setBeschreibung(String beschreibung) {
	this.beschreibung = beschreibung;
}
public String getBeschreibung() {
	return beschreibung;
}
}
