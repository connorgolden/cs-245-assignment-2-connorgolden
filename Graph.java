import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class Graph {

    public HashMap<String, Integer> names;
    public HashMap<Integer, String> index;
    public LinkedList<Integer>[] adjacents;

    //adjacency list.
    public Graph() {
        names = new HashMap<String, Integer>();
        index = new HashMap<Integer, String>();
        adjacents = new LinkedList[100000];

        Arrays.setAll(adjacents, i -> new LinkedList<>());
    }

    public LinkedList<Integer> findAdjacent(int index) {

        LinkedList<Integer> list = new LinkedList<Integer>();

        list.addAll(adjacents[index]);

        return list;
    }


    public void addEdges(Set<Integer> set) {
        for (int index : set) {
            adjacents[index].addAll(set);

            for (int temp : findAdjacent(index)) {
                if (temp == index) {
                    delEdge(index, temp);
                }
            }
        }
    }

    public void delEdge(int pos, Object index) {
        adjacents[pos].remove(index);
    }


}
