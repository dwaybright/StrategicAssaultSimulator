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

package us.thirdmillenium.strategicassaultsimulator;

import com.badlogic.gdx.ApplicationAdapter;

import java.util.Random;

import us.thirdmillenium.strategicassaultsimulator.environment.Environment;
import us.thirdmillenium.strategicassaultsimulator.environment.GameEnvironment;


public class StrategicAssaultSimulator extends ApplicationAdapter {
    private Environment MyEnvironment;
    private Random random;

	
	@Override
	public void create () {
        this.random = new Random();

        this.MyEnvironment = new GameEnvironment("NNPath.nnet", this.random, 5);
	}

	@Override
	public void render () {

        this.MyEnvironment.simulate();

	}
}
