import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.*;
import mil.nga.mgrs.MGRS;
import mil.nga.grid.features.Point;

import static java.lang.Math.atan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.sqrt;
import static java.lang.Math.pow;



public class SDZ {
    public static void main(String[] args) throws IOException, ParseException {
        //class variables
        int maxRange = 600;
        //Process grids and generate nameList and gridList
        FileReader fr=null;
        try
        {
            fr = new FileReader("sdzReady.txt");
        }
        catch (FileNotFoundException fe)
        {
            System.out.println("File not found");
        }
        int ch;
        String wpn;
        ArrayList<String> wpnList = new ArrayList<>();
        String fp;
        ArrayList<String> fpList = new ArrayList<>();
        String tgt;
        ArrayList<String> tgtList = new ArrayList<>();
        while ((ch=fr.read())!=-1){
            if((char)ch == '<') {
                wpn = "";
                ch=fr.read();
                while ((char)ch != '>') {
                    wpn = wpn + (char)ch;
                    ch=fr.read();
                }
                wpnList.add(wpn);
            }
            if((char)ch =='(') {
                fp = "";
                ch= fr.read();
                while ((char)ch != ')') {
                    fp = fp + (char)ch;
                    ch=fr.read();
                }
                fpList.add(fp);
            }
            if((char)ch =='[') {
                tgt = "";
                ch= fr.read();
                while ((char)ch != ']') {
                    tgt = tgt + (char)ch;
                    ch=fr.read();
                }
                tgtList.add(tgt);
            }
        }


        //convert fp coordinates to UTM
        ArrayList<Point> convertedFpList = new ArrayList<>();
        for(int i = 0; i < fpList.size(); i++){
            MGRS mgrs = MGRS.parse(fpList.get(i));
            Point point = mgrs.toPoint();
            convertedFpList.add(point);
        }
        //convert tgt coordinates to UTM
        ArrayList<Point> convertedTgtList = new ArrayList<>();
        for(int i = 0; i < tgtList.size(); i++){
            MGRS mgrs = MGRS.parse(tgtList.get(i));
            Point point = mgrs.toPoint();
            convertedTgtList.add(point);
        }

        // TODO: Implement
        //Create list of coordinates for each fp
        ArrayList<LineString> sdzList = new ArrayList<>();
        for(int i=0; i< fpList.size(); i++){
            //adding path for gtl
            LineString line = new LineString();
            line.addToCoordinates(convertedFpList.get(i).getLongitude(),convertedFpList.get(i).getLatitude());
            line.addToCoordinates(convertedTgtList.get(i).getLongitude(),convertedTgtList.get(i).getLatitude());
            sdzList.add(line);
            //adding path for right dispersion line
            LineString line1 = new LineString();
            line1.addToCoordinates(convertedFpList.get(i).getLongitude(),convertedFpList.get(i).getLatitude());
            fpList.get(i)
        }

        //Creating KML file
        File output = new File("sdz.kml");
        final Kml kml = KmlFactory.createKml();
        Document document = kml.createAndSetDocument();
        for(int i = 0; i < wpnList.size(); i++) {
            document.createAndAddPlacemark()
                    .withName(wpnList.get(i))
                    .createAndSetLineString().addToCoordinates();
        }
        //marshals to console
        //marshals into file
        kml.setFeature(document);
        kml.marshal();
        kml.marshal(output);
    }

    //Todo: Implement
    //Calculates the end grids for a path from given fp with a given azimuth deviation from GTL
    public static Point calculateGrid(Point fp, Point tgt, int deviation) {
        double diffN = fp.getLatitude() - tgt.getLatitude();
        double diffE = fp.getLongitude() - tgt.getLongitude();
        double azimuth = toDegrees(atan(diffN/diffE));
        azimuth = azimuth + deviation;
        double distance = sqrt(pow(diffN,2) - pow(diffE,2));
        return(fp);
    }
}
