package us.thirdmillenium.strategicassaultsimulator.brains;


import android.content.Context;
import java.io.InputStream;


public class NeuralNetworkManager {
    private Context context;


    public NeuralNetworkManager(Context context) {
        this.context = context;
    }


    /**
     * Returns the InputStream to the requested Neural Network file
     *
     * @param filename
     * @return
     */
    public InputStream getNeuralNetwork(String filename) {
        try {
            return this.context.getAssets().open(filename);
        } catch(Exception ex) {
            System.err.println(ex.toString());
            System.exit(40010);
        }

        return null;
    }
}
