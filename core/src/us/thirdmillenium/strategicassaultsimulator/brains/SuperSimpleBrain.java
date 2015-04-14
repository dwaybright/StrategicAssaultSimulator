/*
 Copyright (C) 2015 Daniel Waybright, daniel.waybright@gmail.com

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program (located in root of GitHub folder); if not, visit:

    http://www.apache.org/licenses/LICENSE-2.0


 **** Special Thanks ****

 This project makes extensive use of the LibGDX library
 http://libgdx.badlogicgames.com/index.html

 */

package us.thirdmillenium.strategicassaultsimulator.brains;

import org.neuroph.core.NeuralNetwork;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class SuperSimpleBrain extends Brain {
    private NeuralNetwork myNN;


    public SuperSimpleBrain() throws FileNotFoundException {
        // Open the Neural Network
        InputStream fd = new FileInputStream("ANNs/SuperSimpleNN.nnet");
        this.myNN = NeuralNetwork.load(fd);
    }

    @Override
    public double[] brainFreeze(double...inputs) {
        // Set Inputs
        this.myNN.setInput(inputs);

        // Calculate Network
        this.myNN.calculate();

        // Return output
        return this.myNN.getOutput();
    }
}
