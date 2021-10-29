
import javax.print.DocFlavor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Structure {
    private String name;
    private String type;
    String nameofindex;
    int level;
    int id;
    int unique_id;

    int location;


    public Structure(String name, String type, int location,int unique_id) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.unique_id = unique_id;
    }

    public Structure(int id,String nameofindex,String type,int level){
        this.id = id;
        this.nameofindex = nameofindex;
        this.type = type;
        this.level = level;
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

    public static boolean isFile(String FindType) {
        if (FindType.contains(".doc") || FindType.contains(".txt") || FindType.contains(".docx")
                || FindType.contains(".html") || FindType.contains(".htm") || FindType.contains(".odt") ||
                FindType.contains(".pdf") || FindType.contains(".xls") || FindType.contains(".xlsx") ||
                FindType.contains(".ods") || FindType.contains(".ppt") ||
                FindType.contains(".pptx") || FindType.contains(".jpg")) {
            return true;

        } else {
            return false;
        }

    }

    public static void main(String[] args) throws FileNotFoundException {
        try {
            String jdbcURL="jdbc:mysql://localhost:3386/recursivedb";
            String username="root";
            String password="password";
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/recursivedb","root","Ilker.ozturk.1907");
            Connection con_child=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/recursivedb","root","Ilker.ozturk.1907");
            Statement stmt=con.createStatement();
            Statement stmt_child=con_child.createStatement();
            //stmt.executeUpdate("INSERT into objects(id,name,level,children)VALUES(1,'ilker',2,3)");
            BufferedReader buf = new BufferedReader(new FileReader("test.txt"));
            ArrayList<String> words = new ArrayList<>();
            String lineJustFetched = null;
            String[] wordsArray;
            Structure[] objarr= new Structure[255];
            Stack<Structure> mystack = new Stack<>();
            int level = 0;
            int currentlevel = -1;
            int specific_id=0;
            int counter=0;
            String type;
            boolean checkme;
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
                    if (isFile(ad)) {
                        type = "File";
                    } else {
                        type = "Folder";
                    }
                    Structure add = new Structure(ad, type, level,specific_id);
                    specific_id++;
                    //checkme = isFile(add.name);
                    int last_level;
                    if (!mystack.empty()) {
                        last_level = mystack.lastElement().location;
                    } else {
                        last_level = 0;
                    }
                    //current_level is the current level in stack
                    //last_level is the last element's level in stack
                    if (currentlevel > level) {
                        ArrayList<Integer> childOfNode= new ArrayList<>();
                        int prev_last_level=-1;
                        while ((last_level > level) || (last_level == level)) {
                            if (last_level != level) {

                                if(prev_last_level>last_level){
                                    String parentname = mystack.lastElement().name;
                                    Structure dbObject = new Structure(specific_id,parentname,type,level);
                                    String sql_child = "INSERT INTO children_table VALUES (?, ?)";
                                    PreparedStatement pstmt_child = con_child.prepareStatement(sql_child);
                                    for (Integer num : childOfNode) {
                                        pstmt_child.setInt(1, num);
                                        pstmt_child.setInt(2, mystack.lastElement().unique_id);
                                        pstmt_child.executeUpdate();
                                    }
                                    childOfNode.removeAll(childOfNode);
                                }
                                childOfNode.add(mystack.lastElement().unique_id);
                                if(mystack.lastElement().location<last_level){
                                    String parentname = mystack.lastElement().name;
                                    Structure dbObject = new Structure(specific_id,parentname,type,level);
                                }
                                String parentname=mystack.lastElement().name;
                                int last_stack_id = mystack.lastElement().unique_id;
                                int stack_level = mystack.lastElement().location;
                                String stack_type = mystack.lastElement().type;
                                Structure dbObject = new Structure(last_stack_id,parentname,stack_type,level);
                                String sql = "INSERT INTO objects VALUES (?, ?,?,?)";

                                PreparedStatement pstmt = con.prepareStatement(sql);

                                pstmt.setInt(1,last_stack_id);
                                pstmt.setString(2,parentname);
                                pstmt.setInt(3,stack_level);
                                pstmt.setString(4,stack_type);
                                pstmt.executeUpdate();
                                mystack.pop();
                                prev_last_level = last_level;
                                last_level = mystack.lastElement().location;

                            } else {
                                String parentname = mystack.lastElement().name;
                                Structure dbObject = new Structure(specific_id,parentname,type,level);
                                String sql_child = "INSERT INTO children_table VALUES (?, ?)";
                                PreparedStatement pstmt_child = con_child.prepareStatement(sql_child);
                                for (Integer num : childOfNode) {
                                    pstmt_child.setInt(1, num);
                                    pstmt_child.setInt(2, mystack.lastElement().unique_id);
                                    pstmt_child.executeUpdate();
                                }

                                mystack.push(add);
                                currentlevel = level;
                                break;
                            }

                        }
                    } else {
                        mystack.push(add);
                        currentlevel = level;
                    }
                }
            }
            String sql_child = "INSERT INTO children_table VALUES (?, ?)";
            PreparedStatement pstmt_child = con_child.prepareStatement(sql_child);
            String sql = "INSERT INTO objects VALUES (?, ?,?,?)";
            int prev_stack_level = -1;
            ArrayList<Integer> stackArraychild= new ArrayList<>();
            int current_stack_level = mystack.lastElement().location;
            while(mystack.size()>0){
                if(prev_stack_level>current_stack_level){
                    for (Integer num : stackArraychild) {
                        pstmt_child.setInt(1, num);
                        pstmt_child.setInt(2, mystack.lastElement().unique_id);
                        pstmt_child.executeUpdate();
                    }
                    stackArraychild.removeAll(stackArraychild);
                }
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setInt(1,mystack.lastElement().unique_id);
                pstmt.setString(2,mystack.lastElement().name);
                pstmt.setInt(3,mystack.lastElement().location);
                pstmt.setString(4,mystack.lastElement().type);
                pstmt.executeUpdate();
                prev_stack_level=mystack.lastElement().location;
                stackArraychild.add(mystack.lastElement().unique_id);
                mystack.pop();
                if(!mystack.empty()){
                    current_stack_level=mystack.lastElement().location;
                }

            }
            buf.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

