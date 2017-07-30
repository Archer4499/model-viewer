import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Zones {
    private ArrayList<PointsZone> pointsZones;
    private ArrayList<ElementsZone> elementsZones;
    private ArrayList<String> variables;
    private float[] min, max;

    /**
     * Loads all the points and elements in all the zones from "jet_surface.dat"
     */
    Zones(String filename) {
        String line;
        Pattern varPatt = Pattern.compile(".*?\"(.*?)\"");
        // Possibly a bit too strict
        Pattern zonePatt = Pattern.compile("ZONE T=\".*?\", Nodes=(\\d+), Elements=(\\d+)");
        Matcher m;
        int numNodes, numElements;
        variables = new ArrayList<>();
        pointsZones = new ArrayList<>();
        elementsZones = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine(); // Get rid of the title

            // Capture variables
            line = br.readLine();
            m = varPatt.matcher(line);
            while (m.find()) {
                variables.add(m.group(1));
            }
            int numVars = variables.size();

            min = new float[numVars];
            Arrays.fill(min, Float.POSITIVE_INFINITY);
            max = new float[numVars];
            Arrays.fill(max, Float.NEGATIVE_INFINITY);

            // While there are zones remaining add a pointsZone and elementsZone
            while ((line = br.readLine()) != null) {
                m = zonePatt.matcher(line);
                if (!m.find()) {
                    throw new IllegalStateException();
                }
                numNodes = Integer.parseInt(m.group(1));
                numElements = Integer.parseInt(m.group(2));

                PointsZone pointsZone = new PointsZone(br, numNodes, numVars);

                pointsZones.add(pointsZone);
                elementsZones.add(new ElementsZone(br, numElements));

                // Calculate the max and min over all the zones for each variable
                for (int i = 0; i < numVars; i++) {
                    max[i] = Math.max(pointsZone.getMax(i), max[i]);
                    min[i] = Math.min(pointsZone.getMin(i), min[i]);
                }
            }
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    PointsZone getPointsZone(int i) {
        return pointsZones.get(i);
    }

    ElementsZone getElementsZone(int i) {
        return elementsZones.get(i);
    }

    int getElementsZonesSize() {
        return elementsZones.size();
    }

    ArrayList<String> getVariables() {
        return variables;
    }

    float getMin(int i) {
        return min[i];
    }

    float getMax(int i) {
        return max[i];
    }
}
