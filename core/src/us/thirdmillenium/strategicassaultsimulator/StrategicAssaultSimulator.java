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

import android.content.res.AssetManager;

import com.badlogic.gdx.ApplicationAdapter;

import java.util.Random;

import us.thirdmillenium.strategicassaultsimulator.simulation.environment.Environment;
import us.thirdmillenium.strategicassaultsimulator.simulation.environment.GameEnvironment;
import us.thirdmillenium.strategicassaultsimulator.simulation.environment.Params;
import us.thirdmillenium.strategicassaultsimulator.startup.Startup;


public class StrategicAssaultSimulator extends ApplicationAdapter {
    // State Control
    private GAMESTATE state;
    private Environment MyEnvironment;
    private Startup startup;

    // Variables
    private Random random;
    private AssetManager assman;


    public StrategicAssaultSimulator(AssetManager assman) {
        this.assman = assman;
    }


	@Override
	public void create () {
        this.random = new Random();
        this.state = GAMESTATE.LOGINSCREEN;
        this.startup = new Startup(this.assman);

        // this.MyEnvironment = new GameEnvironment(Params.PathToBaseNN, this.random, 3);
	}


	@Override
	public void render () {

        switch(this.state) {
            case LOGINSCREEN:
                this.startup.draw();

                break;

            case SELECTPATH:

                break;

            case SIMULATE:
                this.MyEnvironment.simulate(1 / (float)10);
                break;

            default:
                break;
        }
	}
}
