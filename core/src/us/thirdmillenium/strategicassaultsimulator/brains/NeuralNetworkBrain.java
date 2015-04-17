package us.thirdmillenium.strategicassaultsimulator.brains;

import android.content.res.AssetManager;
import android.net.Uri;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.io.InputStream;
import java.util.Random;

import org.neuroph.core.NeuralNetwork;

@SuppressWarnings("rawtypes")
public class NeuralNetworkBrain extends Brain {
    private NeuralNetwork myNN;


    public NeuralNetworkBrain(NeuralNetwork nnet) {
        this.myNN = nnet;
    }

    public NeuralNetworkBrain(String nnetPath) {
        //this.myNN = NeuralNetwork.createFromFile(nnetPath);
//        FileHandle file = Gdx.files.local(nnetPath);
//        String locRoot = ;
//        boolean localStore = Gdx.files.isLocalStorageAvailable();
//        //Gdx.files.
//        FileHandle test2 = Gdx.files.local("data/data/us.thirdmillenium.strategicassaultsimulator.Cone100_MLP4.nnet");
//        File exit = test2.file();

        //FileHandle[] files = Gdx.files.local("data/").list();
        //FileHandle handle = Gdx.files.internal("data/badGuyDot.png");
        FileHandle h2 = Gdx.files.internal("data/Cone100_MLP4.nnet");
        //File file = h2.file();

        File file = new File("file:///android_asset/Cone100_MLP4.nnet");


        boolean exists = file.exists();
        boolean read = file.canRead();
        boolean isFile = file.isFile();

        this.myNN = NeuralNetwork.createFromFile(file);
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
