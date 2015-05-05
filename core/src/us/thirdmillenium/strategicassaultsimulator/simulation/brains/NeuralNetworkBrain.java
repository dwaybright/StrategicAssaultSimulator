package us.thirdmillenium.strategicassaultsimulator.simulation.brains;


import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.neuroph.core.NeuralNetwork;

@SuppressWarnings("rawtypes")
public class NeuralNetworkBrain extends us.thirdmillenium.strategicassaultsimulator.simulation.brains.Brain {
    private NeuralNetwork myNN;


    public NeuralNetworkBrain(String nnetPath, AssetManager assman, Resources res, int nnID) {
        try {
            // Get Neural Network Input Stream
            //InputStream nnetInStream = assman.open(nnetPath);
            /*byte[] buffer = new byte[nnetInStream.available()];
            nnetInStream.read(buffer);

            // Create Temp File
            //File outputDir = context.getCacheDir(); // context being the Activity pointer
            File outputFile = File.createTempFile("tempNN", "nnet", cacheDir);
            OutputStream outStream = new FileOutputStream(outputFile);
            outStream.write(buffer);*/

            InputStream in = res.openRawResource(nnID);

            // Read in Temp File
            //NeuralNetwork.l
            this.myNN = NeuralNetwork.load(in);


            // Read in stream
            //this.myNN = NeuralNetwork.load(nnetInStream);

            // Close the Stream
            in.close();
            //nnetInStream.close();
            //afd.close();
        } catch (Exception ex) {
            System.err.println("Unable to load neural network\n" + ex.toString());
            System.exit(5500);
        }
    }

    @Override
    public double[] brainCrunch(double[] inputs) {
        // Set Inputs
        this.myNN.setInput(inputs);

        // Calculate Network
        this.myNN.calculate();

        // Return Output
        return this.myNN.getOutput();
    }

    @Override
    public int getNumInputs() { return this.myNN.getInputsCount(); }

    @Override
    public int getOutputCounts() { return this.myNN.getOutputsCount(); }

    @Override
    public void randomWeights(Random random) {
        this.myNN.randomizeWeights(random);
    }
}
