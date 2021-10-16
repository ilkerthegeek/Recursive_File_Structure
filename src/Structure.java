
import javax.print.DocFlavor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Structure {
    private String name;
    private String type;
    List myList;

    {
        myList = new ArrayList();
    }

    public Structure(String name, String type, List list) {
        this.name = name;
        this.type = type;
        this.myList = list;
    }

    public static int countTabs(String countMe) {
        int counter = 0;
        char[] letters = new char[255];
        letters = countMe.toCharArray();
        for (int j = 0; j < letters.length; j++) {
            if (letters[j] == '\t') {
                counter++;
            }
        }
        return counter;
    }

    public boolean isFile(String FindType) {
        if (FindType.contains(".doc") || FindType.contains(".txt") || FindType.contains(".docx")
                || FindType.contains(".html") || FindType.contains(".htm") || FindType.contains(".odt") ||
                FindType.contains(".pdf") || FindType.contains(".xls") || FindType.contains(".xlsx") ||
                FindType.contains(".ods") || FindType.contains(".ppt") ||
                FindType.contains(".pptx")) {
            return true;

        } else {
            return false;
        }

    }

    public static void main(String[] args) throws FileNotFoundException {
        try {
            BufferedReader buf = new BufferedReader(new FileReader("test.txt"));
            ArrayList<String> words = new ArrayList<>();
            String lineJustFetched = null;
            String[] wordsArray;
            Stack<Structure> mystack = new Stack<>();
            int level;
            List id = new ArrayList();
            while (true) {
                lineJustFetched = buf.readLine();

                if (lineJustFetched == null) {
                    break;
                } else {
                    level = countTabs(lineJustFetched);
                    wordsArray = lineJustFetched.split("\t");
                    for (String each : wordsArray) {
                        if (!"".equals(each)) {
                            words.add(each);
                        }
                    }
                    String ad = words.get(words.size() - 1);
                    Structure add = new Structure(ad, "Folder", id);

                    mystack.push(add);
                }
            }

            for (String each : words) {
                System.out.println(each);
            }

            buf.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

