import org.apache.commons.codec.binary.StringUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class OpordReader {

    public static void main(String[] args) throws IOException {
        String fileName = "OPORD.docx";
        ArrayList<String> nameList = new ArrayList();
        ArrayList<String> gridList = new ArrayList();
        try (XWPFDocument doc = new XWPFDocument(
                Files.newInputStream(Paths.get(fileName)))) {
            XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(doc);
            String docText = xwpfWordExtractor.getText();
            for (int start = 0, end = 6; end <= docText.length(); start++, end++) {
                String substring = docText.substring(start, end);
                if (substring.equals("13S EC")) {
                    gridList.add(substring + docText.substring(start + 6, end + 12));
                    int newStart = start;
                    for (int i = start; (Character.compare(docText.charAt(i), '.') != 0) && (Character.compare(docText.charAt(i), '\n') != 0); i--) {
                        newStart = i;
                    }
                    int newEnd = end;
                    for (int i = end; (Character.compare(docText.charAt(i), '.') != 0) && (Character.compare(docText.charAt(i), '\n') != 0); i++) {
                        newEnd = i;
                    }
                    nameList.add(docText.substring(newStart, newEnd));
                } else {
                    try {
                        int x = Integer.parseInt(substring);
                        gridList.add(substring + docText.substring(start + 6, end + 12));
                        int newStart = start;
                        for (int i = start; (Character.compare(docText.charAt(i), '.') != 0) && (Character.compare(docText.charAt(i), '\n') != 0); i--) {
                            newStart = i;
                        }
                        int newEnd = end;
                        for (int i = end; (Character.compare(docText.charAt(i), '.') != 0) && (Character.compare(docText.charAt(i), '\n') != 0); i++) {
                            newEnd = i;
                        }
                        nameList.add(docText.substring(newStart, newEnd));
                    } catch (NumberFormatException nfe) {

                    }
                }
            }
            System.out.print(gridList.size());
            String out = new String();
            if (nameList.size() > 0) {
                for (int i = 0; i < gridList.size(); i++) {
                    System.out.print(nameList.get(i));
                    out = out + nameList.get(i);
                    System.out.print(" - " + gridList.get(i) + "\n");
                    out = out + " - " + gridList.get(i) + "\r";
                }
            }
            try {
                FileWriter myWriter = new FileWriter("grids.txt");
                myWriter.write(out);
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }
}
