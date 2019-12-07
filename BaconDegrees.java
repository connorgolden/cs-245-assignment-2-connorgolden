
import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BaconDegrees {

    private int count = 0;

    private int readCSV(String file, Graph graph) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
            e.printStackTrace();
            System.exit(1);
        }

        String temp;
        int position = 0;

        JSONParser parser = new JSONParser();
        JSONArray jsonArray;

        Set<Integer> set = new HashSet<Integer>(); //Actors in same movie

        try {
            reader.readLine(); //skip line 1

            while ((temp = reader.readLine()) != null) {
                //Loop through all lines in the file

                if (temp.contains("cast_id")) {
                    //if the line has cast_id we will try and pull the json out.

                    //Get the actors out
                    temp = temp.substring(temp.indexOf("\"[") + 1, temp.lastIndexOf("[") - 2).replaceAll("\"\"", "\"");

                    try {
                        //try and parse the json from the string we extracted.
                        jsonArray = (JSONArray) parser.parse(temp);

                        for (Object object : jsonArray) {

                            String name = (String) ((JSONObject) object).get("name");

                            if (!graph.names.containsKey(name)) { //check if value not already in graph
                                graph.names.put(name, position); //add actor
                                graph.index.put(position, name);
                                position++;
                            }

                            set.add(graph.names.get(name)); //adds all the actors of a single movie into a set
                        }
                        //set edges
                        graph.addEdges(set);
                        set.clear();

                    } catch (ParseException e) {
                        System.out.println("JSON Parse Exception on:");
                        System.out.println(temp);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return position;
    }

    private String capName(String name) {

        StringBuilder newName = new StringBuilder();

        //Everything to lowercase.
        name = name.toLowerCase();
        name = name.trim();

        //Split string of name on spaces, so we can capitalize each part of the name.
        String[] strArr = name.split(" ", 2);

        for (String str : strArr) {
            //Convert first letter of each substring, (Firstname Lastname) etc to caps.
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
            newName.append(str).append(" ");
        }

        return newName.toString().trim();
    }

    private void findPath(String name1, String name2, Graph graph) {

        int index1 = graph.names.get(name1);
        int index2 = graph.names.get(name2);

        if (graph.adjacents[index1].contains(index2)) { //check if names are in same movie
            System.out.println("Path between " + name1 + " and " + name2 + ": ");
            System.out.println(name1 + " -> " + name2);
            return;
        }

        boolean[] checkedInQueue = new boolean[graph.names.size()];

        Queue<Integer> queue = new LinkedList<Integer>();
        Map<Integer, Integer> path = new HashMap<>();

        queue.add(index1);
        checkedInQueue[index1] = true;

        while (!queue.isEmpty()) {

            int index = queue.poll();

            for (int curr : graph.findAdjacent(index)) {
                if (!checkedInQueue[curr]) {
                    checkedInQueue[curr] = true;
                    queue.add(curr);
                    path.put(curr, index); //track path
                }

                if (curr == index2) {
                    StringBuilder output = new StringBuilder();
                    while (path.get(curr) != null) {
                        output.insert(0, " -> " + graph.index.get(curr));
                        curr = path.get(curr);
                    }
                    System.out.println("Path between " + name1 + " and " + name2 + ": ");
                    System.out.println(name1 + output);
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {

        //Initialize main program as well as the graph for storing the data
        BaconDegrees bacon = new BaconDegrees();
        Graph graph = new Graph();

        //Read the data from the CSV to the graph.
        if (args[0] != null) {

            bacon.count = bacon.readCSV(args[0], graph);

        } else {
            System.out.println("Improper Usage BaconDegrees [filename]");
            System.out.println("Exiting...");
            System.exit(1);
        }

        Scanner input = new Scanner(System.in);

        while (true) {
            //Infinite Loop to keep program running so we dont have to reload graph every time we want to use it.

            String name1 = null;
            String name2 = null;

            boolean name1valid = false;
            boolean name2valid = false;

            while (!name1valid) {//keep asking until name 1 is valid
                System.out.print("Enter Actor 1: ");

                name1 = bacon.capName(input.nextLine());

                if (graph.names.containsKey(name1))
                    //the name is in the graph
                    name1valid = true;
                else
                    System.out.println("Actor not found in database. Please enter different name: ");

            }
            while (!name2valid) {//keep asking until name 2 is valid
                System.out.print("Enter Actor 2: ");
                name2 = input.nextLine();
                name2 = bacon.capName(name2);

                if (name2.equalsIgnoreCase(name1)) {
                    System.out.println("You entered the same actor twice, please try again: ");
                } else {
                    if (graph.names.containsKey(name2))
                        //the name is in the graph
                        name2valid = true;
                    else
                        System.out.println("Actor not found in database. Please enter different name: ");
                }

            }

            bacon.findPath(name1, name2, graph);

            System.out.println("----------------------------------------");
        }
    }
}