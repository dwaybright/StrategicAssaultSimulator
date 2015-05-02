/*
 Copyright (C) 2015 Daniel Waybright, daniel.waybright@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package us.thirdmillenium.strategicassaultsimulator.environment;



public interface Params {

    /*
     *      Random Math Constants
     */

    static final float SQRT2 = 1.4142135f;


    /** Path to the base NN to be used by GA. */
    static final String PathToBaseNN    = "data/Cone100_MLP4.nnet";


    /*
	 * 	Movement Scoring
	 */

    /** Score for moving to new Tile */
    static final int ScoreMoveNewTile = 0;

    /** Score for moving to new Tile on Path */
    static final int ScoreMoveToPrefPathTile = 10;


	/*
	 * Game Simulation Parameters
	 */

    /** All TileMap levels reside in this directory */
    static final String TileMapsPath            = ""; //"core/assets/";

    /** The path to the level to train NN on */
    static final String TileMapLevelPath        = "MyCrappyMap2.tmx"; //"core/assets/MyCrappyMap2.tmx";

    /** Texture (*.png) for Training Agent. */
    static final String TrainingAgentLivePNG    = "goodGuyDotArrow.png";

    /** Texture (*.png) for Shooting Agent. */
    static final String ShootingAgentLivePNG    = "badGuyDotArrow.png";

    /** Texture for a deceased dot. */
    static final String DeadAgentPNG            = "deadDot.png";


    /** Pixel Size for Tiles */
    static final int MapTileSize = 32;

    /** Pixel Size for Agents */
    static final int AgentTileSize = 20;

    /** The squared radius for an Agent - Bounding Circle */
    static final float AgentRadiusSquared = (AgentTileSize * AgentTileSize) / 4f;

    /** Pixel radius for Agent (Bounding Circle) */
    static final int AgentCircleRadius = 10;

    /** Cells in Y direction */
    static final int NumCellsY = 38;

    /** Cells in X direction */
    static final int NumCellsX = 25;


    /** The number of simulation steps to take. Assume ~30 FPS, then 1800 is ~1 minute. */
    //static final int SimulationTimeSteps = 1000;

    /** Assumed FPS rate. */
    static final double FramesPerSecond = 30;


    /** Bullet Velocity (pixels/frame). */
    static final float BulletVelocity = 20;

    /** Bullet Path vertices at Origin (0,0) */
    static final float[] BulletPathOriginVertices = {
            -2.5f, 0,
            2.5f, 0,
            2.5f, BulletVelocity,
            -2.5f, BulletVelocity
    };

    /** Agent Rotation Modification Per Time Step */
    static final float[] AgentRotationModArray = { -1f, -0.666667f, -0.333333f, 0.001f, 0.333333f, 0.666667f, 1f };

    /** Agent Velocity Modification Per Time Step */
    static final float[] AgentVelocityModArray = { -0.8f, -0.5f, -0.1f, 0.1f, 0.2f, 0.6f, 1f };


    /** Agent Velocity (pixels/frame). */
    static final float AgentMaxMovement = 10f;

    /** How Far an Agent can Rotate in a frame. */
    static final float AgentMaxTurnAngle = 15f;

    /** Agent Fire Rate (fire/sec) */
    static final float AgentFireRate = 4f;

    static final int AgentFireRateCooldown = (int)FramesPerSecond / 2;


    /** Shooting Agent hit points */
    static final short EnemyAgentHitPoints = 5;

    /** Shooting Agent hit points */
    static final short TeamAgentHitPoints = 200;

    /** Shooting Agent Accuracy */
    static final float ShootingAgentFireAccuracy = 30f;


    /*
     *      Cone Agent Vision Settings
     */

    static final float ConeVisNorm = 4;

    static final float ConeVisEmpty = 0;

    static final float ConeVisWall = 1 / ConeVisNorm;

    static final float ConeVisTeam = 2 / ConeVisNorm;

    static final float ConeVisPath = 3 / ConeVisNorm;

    static final float ConeVisEnemy = 4 / ConeVisNorm;

}
