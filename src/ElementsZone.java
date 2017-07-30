import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class ElementsZone {
    private ArrayList<ArrayList<Integer>> elements;

    ElementsZone(BufferedReader br, int numElements) {
        elements = new ArrayList<>();
        try {
            for (int i = 0; i < numElements; i++) {
                String[] line = br.readLine().trim().split(" ");

                ArrayList<Integer> row = new ArrayList<>();
                for (String aLine : line) {
                    row.add(Integer.parseInt(aLine));
                }
                elements.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    int getSize() {
        return elements.size();
    }

    ArrayList<Integer> getElements(int i) {
        return elements.get(i);
    }
}
