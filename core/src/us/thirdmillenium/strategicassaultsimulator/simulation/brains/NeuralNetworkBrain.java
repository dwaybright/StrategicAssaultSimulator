package us.thirdmillenium.strategicassaultsimulator.simulation.brains;


import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.Random;

import org.neuroph.core.NeuralNetwork;

@SuppressWarnings("rawtypes")
public class NeuralNetworkBrain extends us.thirdmillenium.strategicassaultsimulator.simulation.brains.Brain {
    private NeuralNetwork myNN;


    public NeuralNetworkBrain(String nnetPath, AssetManager assman) {
        try {
            // Get Neural Network Input Stream
            InputStream nnetInStream = assman.open(nnetPath);

            // Read in stream
            this.myNN = NeuralNetwork.load(nnetInStream);

            // Close the Stream
            nnetInStream.close();
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
