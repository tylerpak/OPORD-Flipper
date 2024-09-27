import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.KmlFactory;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import mil.nga.mgrs.MGRS;
import mil.nga.grid.features.Point;


public class MapBuilder {
	public static void main(String[] args) throws IOException, ParseException {
		//Process grids and generate nameList and gridList
		FileReader fr=null;
		try
		{
			fr = new FileReader("gridsReady.txt");
		}
		catch (FileNotFoundException fe)
		{
			System.out.println("File not found");
		}
		int ch;
		String name;
		ArrayList<String> nameList = new ArrayList<>();
		String grid;
		ArrayList<String> gridList = new ArrayList<>();
		while ((ch=fr.read())!=-1){
			if((char)ch == '<') {
				name = "";
				ch=fr.read();
				while ((char)ch != '>') {
					name = name + (char)ch;
					ch=fr.read();
				}
				nameList.add(name);
			}
			if((char)ch =='(') {
				grid = "";
				ch= fr.read();
				while ((char)ch != ')') {
					grid = grid + (char)ch;
					ch=fr.read();
				}
				gridList.add(grid);
			}
		}


		//convert coordinates to UTM
		ArrayList<Point> convertedGridList = new ArrayList<>();
		for(int i = 0; i < gridList.size(); i++){
			MGRS mgrs = MGRS.parse(gridList.get(i));
			Point point = mgrs.toPoint();
			convertedGridList.add(point);
		}
		

		//Creating KML file
		File output = new File("opord.kml");
		final Kml kml = KmlFactory.createKml();
		Document document = kml.createAndSetDocument();
		for(int i = 0; i < nameList.size(); i++) {
			document.createAndAddPlacemark()
					.withName(nameList.get(i))
					.createAndSetPoint().addToCoordinates(convertedGridList.get(i).getLongitude(), convertedGridList.get(i).getLatitude());
		}
		//marshals to console
		//marshals into file
		kml.setFeature(document);
		kml.marshal();
		kml.marshal(output);
	}
}
