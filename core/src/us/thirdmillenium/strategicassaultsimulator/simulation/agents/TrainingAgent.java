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

package us.thirdmillenium.strategicassaultsimulator.simulation.agents;

import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import us.thirdmillenium.strategicassaultsimulator.simulation.environment.Params;
import us.thirdmillenium.strategicassaultsimulator.simulation.ai.tile.TileNode;
import us.thirdmillenium.strategicassaultsimulator.simulation.brains.Brain;
import us.thirdmillenium.strategicassaultsimulator.simulation.brains.NeuralNetworkBrain;
import us.thirdmillenium.strategicassaultsimulator.simulation.graphics.GraphicsHelpers;
import us.thirdmillenium.strategicassaultsimulator.simulation.environment.GreenBullet;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class TrainingAgent extends AgentModel {
    // Agent Position
    private Vector2 position;
    private float rotation;

    // Agent Sprite
    private Sprite sprite;

    // The preferred path for training
    private GraphPath<TileNode> preferredPath;
    private int preferredPathIndex;
    private HashSet<TileNode> prefPathNodeTracker;

    // The controller to decide the Agents next moves
    private Brain brain;
    private Random random;
    private long counter;

    // Trackers from the Environment
    private Set<AgentModel> trainees;
    private Set<AgentModel> shooters;
    private Set<GreenBullet> bullets;
    private ConcurrentHashMap<Integer, TileNode> traverseNodes;
    private TiledMap tileMap;

    // Score Trackers
    private long score;
    private int currentCellIndex;


    public TrainingAgent(int testLevelID, String nnetPath, int PixelX, int PixelY, ConcurrentHashMap<Integer, TileNode> traverseNodes, Random random,
                         TiledMap tileMap, Set<AgentModel> trainees, Set<AgentModel> shooters, Set<GreenBullet> bullets, AssetManager assman, Resources res, int nnID) {
        // Set Position and Location
        this.position = new Vector2(PixelX, PixelY);
        this.rotation = 270;

        // Load Neural Network
        this.brain = new NeuralNetworkBrain(nnetPath, assman, res, nnID);
        //this.brain.randomWeights(random);
        this.random = random;

        // Setup Preferred Path
        this.traverseNodes = traverseNodes;
        this.prefPathNodeTracker = new HashSet<TileNode>(500);

        //this.preferredPath = GraphicsHelpers.getPrefPathOne(this.prefPathNodeTracker, this.traverseNodes);
        this.preferredPath = GraphicsHelpers.getPrefPathTest(testLevelID, this.prefPathNodeTracker, this.traverseNodes);

        this.preferredPathIndex = 0;

        // Setup Sprite
        this.sprite = new Sprite(new Texture(Params.TrainingAgentLivePNG));
        this.sprite.setCenter(PixelX, PixelY);
        this.sprite.rotate(this.rotation);

        // Setup Environment Trackers
        this.trainees = trainees;
        this.shooters = shooters;
        this.bullets = bullets;
        this.tileMap = tileMap;
        this.counter = 0;

        // Setup Movement Score Tracking
        this.score = 0;
        this.currentCellIndex = -1; //GraphicsHelpers.getCurrentCellIndex(PixelX, PixelY);
    }

    @Override
    public void updateAgent(float deltaTime) {
        // Vars
        double[] inputs = new double[this.brain.getNumInputs()];
        double gridNorm = 5;
        double angleNorm = 360;
		
		/*
		 *  Collect information into double[] array for brain crunching
		 */

        // Current Rotation
        inputs[0] = this.rotation / angleNorm;

        // Angle to Bullets within 3 tiles (Max of 2)
        int count = 1;

        Iterator<GreenBullet> bulletITR = this.bullets.iterator();

        while( bulletITR.hasNext() && count < 3 ) {
            GreenBullet currBullet = bulletITR.next();

            if( this.position.dst(currBullet.getBulletVector()) < (Params.MapTileSize * 3) ) {
                Vector2 direction = currBullet.getBulletVector().cpy().sub(this.position).nor();
                Vector2 unitVec = new Vector2(0,1);

                inputs[count++] = unitVec.angle(direction) / angleNorm;
            }
        }

        // When no bullets, its just 0.
        inputs[1] = (count > 1) ? inputs[1] : 1;
        inputs[2] = (count > 2) ? inputs[2] : 1;

        // Feed in the 7x7 array of values
        int cellX = (int)(this.position.x / Params.MapTileSize);
        int cellY = (int)(this.position.y / Params.MapTileSize);

        int currentCellIndex = (cellX * Params.NumCellsY) + cellY;
        int gridYCount = 0;
        int gridXCount = 0;

        // Compute from Bottom Left to Top Right - note that it is (Col,Row)
        for( int gridY = cellY - 3; gridY <= cellY + 3; gridY++ ) {

            if( gridY >= 0 && gridY < Params.NumCellsY ) {
                gridXCount = 0;

                for( int gridX = cellX - 3; gridX <= cellX + 3; gridX++ ) {

                    if( gridX >= 0 && gridX < Params.NumCellsX ) {
                        // Compute indexes
                        int tileIndex = (gridX * Params.NumCellsY) + gridY;
                        int inputIndex = ((6-gridYCount)*7) + gridXCount + 3;  //((6-colCount)*7)+rowCount+1;

                        // Check if cell is traversable
                        if( this.traverseNodes.containsKey(tileIndex)) {
                            //TileNode tester = this.traverseNodes.get(tileIndex);

                            // Its traversable, so at least a 1
                            inputs[inputIndex] = 1 / gridNorm;

                            // Check if player is currently located there
                            if( tileIndex == currentCellIndex ) {
                                inputs[inputIndex] = 2 / gridNorm;
                            }

                            // Check if contains a preferred path node
                            if(this.prefPathNodeTracker.contains(this.traverseNodes.get(tileIndex)) ) {
                                inputs[inputIndex] = 3 / gridNorm;
                            }

                            // Check if contains a friendly
                            Iterator<AgentModel> friendlyITR = this.trainees.iterator();

                            while(friendlyITR.hasNext()) {
                                AgentModel tempFriendly = friendlyITR.next();

                                if( tempFriendly != this && tileIndex == tempFriendly.getTraverseNodeIndex() ) {
                                    inputs[inputIndex] = 4 / gridNorm;
                                }
                            }

                            // Check if contains an enemy
                            Iterator<AgentModel> shooterITR = this.shooters.iterator();

                            while(shooterITR.hasNext()) {
                                AgentModel tempShooter = shooterITR.next();

                                if( tileIndex == tempShooter.getTraverseNodeIndex() ) {
                                    inputs[inputIndex] = 5 / gridNorm;
                                }
                            }
                        }
                    }

                    gridXCount++;
                }
            }

            gridYCount++;
        }
		
		
		/*
		 *  Get Brain Response
		 */
        double[] outputs = this.brain.brainCrunch(inputs);
		
		
		/*
		 *  Update Agent based on Response
		 */

        // Collect output groupings
        double[] rotationOutput = new double[7];
        double[] gunRotationOutput = new double[7];
        double[] velocityOutput = new double[7];
        double shoot = outputs[outputs.length-1];

        for(int i = 0; i < 7; i++) {
            rotationOutput[i] = outputs[i];
            gunRotationOutput[i] = outputs[i+7];
            velocityOutput[i] = outputs[i+14];
        }

        // Update
        updatePosition(rotationOutput, velocityOutput, deltaTime);
        updateShooting(gunRotationOutput, shoot);



        // Debug
        //if( ++this.counter % 1000 == 0 ) { System.out.println("Counter = " + this.counter + " Score: " + this.score); }
    }

    private float sumArray(double[] vec) {
        float weight = 0;

        for( int i =0; i < vec.length; i++) {
            weight += vec[i];
        }

        return weight;
    }


    /**
     * This method computes the new angle and position for the Agent from NN output.
     *
     * @param rotation
     * @param velocity
     * @param deltaTime
     */
    private void updatePosition(double[] rotation, double[] velocity, float deltaTime) {
        // Collect how many degrees to adjust the current rotation, and adjust sprite
        //float deltaRot = getWeightedChange(rotation, TrainingParams.AgentRotationModArray) * TrainingParams.AgentMaxTurnAngle;
        //this.rotation += getWeightedChange(rotation, TrainingParams.AgentRotationModArray) * TrainingParams.AgentMaxTurnAngle;

        float angleChange = Math.max(-1, Math.min(1, sumArray(rotation))) * Params.AgentMaxTurnAngle;
        this.rotation += angleChange;

        if(this.rotation < 0   ) { this.rotation += 360; }
        if(this.rotation > 360 ) { this.rotation -= 360; }

        this.sprite.setRotation(this.rotation);

        // Collect wanted velocity, then change position accordingly
        //float agentMovement = getWeightedChange(velocity, TrainingParams.AgentVelocityModArray) * TrainingParams.AgentMaxMovement;

        float agentMovement = Math.max(-1, Math.min(1, sumArray(velocity))) * Params.AgentMaxMovement;

        Vector2 newPosition = this.position.cpy().mulAdd((new Vector2(0,1)).rotate(this.rotation), agentMovement);

        //unitVec.mulAdd(new Vector2(0,1), agentMovement);
        //this.sprite.setPosition(newPosition.x, newPosition.y);


        this.position = boundaryCheckNewPosition(newPosition);

        this.sprite.setPosition(this.position.x, this.position.y);


        // Compute Movement Score
        int inThisCell = GraphicsHelpers.getCurrentCellIndex((int)this.position.x, (int)this.position.y);

        if( this.currentCellIndex != inThisCell ) {

            // The cell is on the path
            if( this.preferredPathIndex < this.preferredPath.getCount() &&
                    this.prefPathNodeTracker.contains( this.traverseNodes.get(inThisCell))) {

                int index = this.preferredPath.getCount();

                // Find the index for this cell
                for( int i = 0; i < this.preferredPath.getCount(); i++) {
                    TileNode tempTileNode = this.preferredPath.get(i);

                    if( tempTileNode == this.traverseNodes.get(inThisCell) ) {
                        index = i;
                        break;
                    }
                }

                // Increase score and next index that provides bonus
                if( index >= this.preferredPathIndex ) {
                    this.score += Params.ScoreMoveToPrefPathTile;
                    this.preferredPathIndex = index + 1;
                }

            }

            this.score += Params.ScoreMoveNewTile;

            this.currentCellIndex = inThisCell;
        }
    }


    /**
     * Need to check that the Agent doesn't run through walls or off the map.
     *
     * @param newPosition
     */
    private Vector2 boundaryCheckNewPosition(Vector2 newPosition) {
        Rectangle boundRect = this.sprite.getBoundingRectangle();

        // Check World Map Boundaries First, just fudge back in if needed
        if( boundRect.x < 0 ) {
            newPosition.x += Math.abs((int)boundRect.x);
        } else if( boundRect.x > (Params.MapTileSize * Params.NumCellsX) - (Params.AgentTileSize) ) {
            newPosition.x = (float) ((Params.MapTileSize * Params.NumCellsX) - (Params.AgentTileSize));
        }

        if( boundRect.y < 0 ) {
            newPosition.y += Math.abs((int)boundRect.y);
        } else if( boundRect.y > (Params.MapTileSize * Params.NumCellsY) - (Params.AgentTileSize) ) {
            newPosition.y = (float) ((Params.MapTileSize * Params.NumCellsY) - (Params.AgentTileSize));
        }

        // Check Walls Second
        MapObjects wallMapObjects = this.tileMap.getLayers().get(2).getObjects();
        Polygon spriteAsPoly = GraphicsHelpers.convertRectangleToPolygon(boundRect);

        for( int i = 0; i < wallMapObjects.getCount(); i++) {
            Object rectangleMapObject = wallMapObjects.get(i);
            Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

            // Make sure this is a Rectangle from Tiled describing a wall.
            if( rectangleMapObject.getClass() == RectangleMapObject.class ) {
                Rectangle wallRectangle = ((RectangleMapObject)rectangleMapObject).getRectangle();
                Polygon polyBound = GraphicsHelpers.convertRectangleToPolygon(wallRectangle);

                // If hitting a wall, don't allow movement at all
                if( Intersector.overlapConvexPolygons(spriteAsPoly, polyBound, mtv)) {
                    newPosition.x += mtv.depth * mtv.normal.x;
                    newPosition.y += mtv.depth * mtv.normal.y;
                }
            }
        }

        return newPosition;
    }


    private void updateShooting(double[] gunRotation, double shoot) {

        if( shoot > 0.6 ) {

        }

    }

    @Override
    public long getScore() { return this.score; }


    /**
     * This method takes a NN output chunk and a modifier array, and computes the weighted average based on highest scored output.
     * @param output
     * @param modifier
     * @return
     */
    private float getWeightedChange(double[] output, float[] modifier) {
        // Find highest output node
        int bestIndex = -1;
        double max = 0;
        int index;

        for(index = 0; index < output.length; index++) {
            if( output[index] > max ) {
                bestIndex = index;
                max = output[index];
            }
        }

        double weightedChange;

        if( bestIndex == 0 ) {
            weightedChange = ((modifier[0] * output[0]) + (modifier[1] * output[1])) / 2;
        } else if( bestIndex == 6) {
            weightedChange = ((modifier[6] * output[6]) + (modifier[5] * output[5])) / 2;
        } else {
            int left = bestIndex - 1;
            int right = bestIndex + 1;

            if( output[left] > output[right] ) {
                weightedChange = ((modifier[bestIndex] * output[bestIndex]) + (modifier[left] * output[left])) / 2;
            } else {
                weightedChange = ((modifier[bestIndex] * output[bestIndex]) + (modifier[right] * output[right])) / 2;
            }
        }

        return (float)weightedChange;
    }


    @Override
    public void agentHit() {

    }

    /**
     * Draws Agent to supplied SpriteBatch.
     * @param sb
     */
    @Override
    public void drawAgent(SpriteBatch sb) {
        this.sprite.draw(sb);
    }

    @Override
    public void drawPath(ShapeRenderer sr) {
        // Draws the CurrentPath.
        if( this.preferredPath != null) {
            for (int i = this.preferredPathIndex + 1; i < this.preferredPath.getCount(); i++) {
                sr.rectLine(this.preferredPath.get(i - 1).getPixelX(), this.preferredPath.get(i - 1).getPixelY(),
                        this.preferredPath.get(i).getPixelX(), this.preferredPath.get(i).getPixelY(), 5);
            }
        }
    }

    @Override
    public void drawVision(ShapeRenderer sr) {
        return;
    }

    @Override
    public void setPathToGoal(float goalX, float goalY) {

    }


    @Override
    public int getTraverseNodeIndex() {
        return GraphicsHelpers.getCurrentCellIndex((int)this.position.x, (int)this.position.y);
    }

    @Override
    public Vector2 getPosition() { return this.position; }

    @Override
    public Rectangle getBoundingRectangle() {
        return this.sprite.getBoundingRectangle();
    }
}
