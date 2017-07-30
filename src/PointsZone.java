import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

class PointsZone {
    private FloatBuffer points;
    private int numVariables;
    private float min[], max[];

    PointsZone(BufferedReader br, int numNodes, int numVars) {
        numVariables = numVars;
        ByteBuffer vbb = ByteBuffer.allocateDirect(numNodes * numVars * 4);
        vbb.order(ByteOrder.nativeOrder()); // use the device hardware's native byte order
        points = vbb.asFloatBuffer();       // create a floating point buffer from the ByteBuffer

        min = new float[numVars];
        Arrays.fill(min, Float.POSITIVE_INFINITY);
        max = new float[numVars];
        Arrays.fill(max, Float.NEGATIVE_INFINITY);

        try {
            for (int i = 0; i < numNodes; i++) {
                String[] line = br.readLine().trim().split(" ");

                for (int j = 0; j < numVars; j++) {
                    float val = Float.parseFloat(line[j]);
                    points.put(val);

                    max[j] = Math.max(val, max[j]);
                    min[j] = Math.min(val, min[j]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    float getMin(int i) {
        return min[i];
    }

    float getMax(int i) {
        return max[i];
    }

    FloatBuffer getPoints(int i) {
        points.position(i * numVariables);
        return points;
    }

    float getVar(int i, int colourVar) {
        return points.get(i * numVariables + colourVar);
    }
}
