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
import android.content.res.Resources;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

import us.thirdmillenium.strategicassaultsimulator.pathselect.PathSelection;
import us.thirdmillenium.strategicassaultsimulator.simulation.ai.tile.TileNode;
import us.thirdmillenium.strategicassaultsimulator.simulation.environment.Environment;
import us.thirdmillenium.strategicassaultsimulator.simulation.environment.GameEnvironment;
import us.thirdmillenium.strategicassaultsimulator.simulation.environment.Params;
import us.thirdmillenium.strategicassaultsimulator.startup.Startup;


public class StrategicAssaultSimulator extends ApplicationAdapter {
    // State Control
    private GAMESTATE state;
    private Startup startup;
    private PathSelection pathselect;
    private Environment MyEnvironment;



    // Startup Answers
    private int levelSelect;
    private int playerSelect;
    private int enemySelect;

    // Other Variables
    private Random random;
    private AssetManager assman;
    private Resources res;
    private int nnID;


    public StrategicAssaultSimulator(AssetManager assman, Resources res, int nnID) {
        this.assman = assman;
        this.res = res;
        this.nnID = nnID;
    }


	@Override
	public void create () {
        this.random = new Random();
        this.state = GAMESTATE.LOGINSCREEN;
        this.startup = new Startup(this.assman);
	}


	@Override
	public void render () {

        switch(this.state) {
            case LOGINSCREEN:
                this.startup.draw();

                if( this.startup.isReadyForPath() ) {
                    this.state = GAMESTATE.SELECTPATH;

                    // Collect Startup Settings and Kill object
                    this.levelSelect  = this.startup.getLevelSelected() + 1;
                    this.playerSelect = this.startup.getPlayerController();
                    this.enemySelect  = this.startup.getEnemyController();

                    this.startup.dispose();

                    // Create a new PathSelection object
                    this.pathselect = new PathSelection(this.levelSelect);
                }

                break;

            case SELECTPATH:
                this.pathselect.draw();

                if( this.pathselect.isReturnToStartScreen() ) {
                    this.state = GAMESTATE.LOGINSCREEN;

                    // Make new startup object
                    this.startup = new Startup(this.assman);

                    this.pathselect.dispose();
                }

                if( this.pathselect.isReadyToSimulate() ) {
                    this.state = GAMESTATE.SIMULATE;

                    // Collect Path
                    Array<TileNode> prefPath = this.pathselect.getPrefPath();
                    int startLocID = this.pathselect.getStartLocID();

                    // Create Environment
                    this.MyEnvironment = new GameEnvironment(Params.PathToBaseNN, this.random, this.assman,
                                                             this.levelSelect, prefPath, this.res, this.nnID,
                                                             this.playerSelect, this.enemySelect, startLocID);

                    // Dispose of Enviro
                    this.pathselect.dispose();
                }

                break;

            case SIMULATE:
                this.MyEnvironment.simulate(1 / (float)10);

                if( this.MyEnvironment.isEndSimulation() ) {
                    this.state = GAMESTATE.LOGINSCREEN;

                    this.startup = new Startup(this.assman);

                    this.MyEnvironment.dispose();
                }

                break;

            default:
                if(this.startup != null) {
                    this.startup.dispose();
                }

                this.startup = new Startup(this.assman);

                this.state = GAMESTATE.LOGINSCREEN;

                break;
        }
	}
}
