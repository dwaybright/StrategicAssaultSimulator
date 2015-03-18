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

package us.thirdmillenium.strategicassaultsimulator.agents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import us.thirdmillenium.strategicassaultsimulator.ai.TileAStarPathFinder;
import us.thirdmillenium.strategicassaultsimulator.ai.TileHeuristic;
import us.thirdmillenium.strategicassaultsimulator.ai.TileNode;
import us.thirdmillenium.strategicassaultsimulator.brains.Brain;
import us.thirdmillenium.strategicassaultsimulator.brains.PuppetBrain;


public class PuppetAgent extends AgentModel {
    // Game World
    private TiledMap MyTiledMap;
    private Sprite Sprite;
    private Texture Alive;
    private Texture Dead;
    private int TileSize;
    private HashMap<Integer, TileNode> MapNodes;
    private TileAStarPathFinder PathFinder;
    private int AgentSize = 10;  // 20 x 20

    // Agent location
    private GraphPath<TileNode> CurrentPath;
    private int CurrentPathIndex;
    private float Pixel_X;
    private float Pixel_Y;
    private int Cell_X;
    private int Cell_Y;
    private float Angle;

    // Agent Physical Attributes
    private int Health;
    private float MovementSpeed;
    private float MovementSpeedScalar;
    private float Eyesight;
    private float Hearing;

    // Agent Equipment
    //private WeaponModel MyWeapon;
    //private ArmorModel MyArmor;

    // Agent Mental Model
    private Brain Control;



    public PuppetAgent(TiledMap myTiledMap, HashMap<Integer, TileNode> mapNodes,
                       TileAStarPathFinder pathFinder, int pixelX, int pixelY, int tileSize)
    {
        // Setup game world parameters
        this.MyTiledMap = myTiledMap;
        this.TileSize = tileSize;
        this.MapNodes = mapNodes;
        this.PathFinder = pathFinder;

        // Setup Graphics
        this.Alive = new Texture("goodGuyDotArrow.png");
        this.Dead = new Texture("deadDot.png");
        this.Sprite = new Sprite(Alive);

        // Setup Start Location
        this.CurrentPathIndex = -1;
        this.Pixel_X = pixelX;
        this.Pixel_Y = pixelY;

        // Set Basic Values
        this.Health = 50;
        this.MovementSpeed = 50;
        this.MovementSpeedScalar = 5;
        this.Eyesight = 10;
        this.Hearing = 20;
        this.Angle = 0;

        // Set Control
        this.Control = new PuppetBrain();
    }

    @Override
    public void agentHit() {
        throw new NotImplementedException();
    }

    @Override
    public void updateAgentState() {
        // Bounds Check - Do nothing
        if( this.CurrentPath == null) {
            return;
        } else if (this.CurrentPath.getCount() < 1 || this.CurrentPathIndex < 0) {
            return;
        }

        // Collect next intermediate node to move to
        TileNode tempTile = this.CurrentPath.get(this.CurrentPathIndex);

        // Calculate pixel distance between positions
        Vector2 currentPosition = new Vector2(this.Pixel_X, this.Pixel_Y);
        Vector2 nextPosition = tempTile.getPixelVector2();

        float distance = currentPosition.dst2(nextPosition);

        // Make sure to move as far as possible
        if( distance < this.MovementSpeed ) {

            if( this.CurrentPathIndex + 1 < this.CurrentPath.getCount() ) {
                this.CurrentPathIndex++;
                tempTile = this.CurrentPath.get(this.CurrentPathIndex);
                nextPosition = tempTile.getPixelVector2();
            } else {
                // We have arrived!
                this.Pixel_X = nextPosition.x;
                this.Pixel_Y = nextPosition.y;
                this.Sprite.setPosition(this.Pixel_X - this.AgentSize, this.Pixel_Y - this.AgentSize);

                // Clear Path
                this.CurrentPath = null;
                this.CurrentPathIndex = -1;

                return;
            }
        }

        // Update Position
        Vector2 direction = nextPosition.sub(currentPosition).nor();
        direction.mulAdd(direction, this.MovementSpeedScalar);

        this.Pixel_X += direction.x;
        this.Pixel_Y += direction.y;
        this.Sprite.setPosition(this.Pixel_X - this.AgentSize, this.Pixel_Y - this.AgentSize);

        // Update Rotation
        Vector2 unitVec = new Vector2(0,1);
        this.Sprite.setRotation( unitVec.angle(direction));
    }

    @Override
    public void drawAgent(SpriteBatch sb) {
        updateAgentState();

        this.Sprite.draw(sb);
    }

    @Override
    public void drawLines(ShapeRenderer sr) {

        // Draws the CurrentPath.
        if( this.CurrentPath != null) {
            for (int i = 1; i < this.CurrentPath.getCount(); i++) {
                sr.rectLine(this.CurrentPath.get(i - 1).getPixelX(), this.CurrentPath.get(i - 1).getPixelY(),
                        this.CurrentPath.get(i).getPixelX(), this.CurrentPath.get(i).getPixelY(), 5);
            }
        }
    }

    /**
     * Updates the Path to where you touch.
     * @param goalX
     * @param goalY
     */
    public void setPathToGoal(float goalX, float goalY) {
        // Reset Index Tracker
        this.CurrentPathIndex = 0;

        // Generate Cell X and Y positions
        int numCellY = Gdx.graphics.getHeight() / this.TileSize;

        // Current Sprite Location
        float spriteX = this.Sprite.getX();
        float spriteY = this.Sprite.getY();

        // Current Sprite Tile Location
        int spriteCellX = (int) (spriteX / this.TileSize);
        int spriteCellY = (int) (spriteY / this.TileSize);

        // Tile Location for Touch
        int touchCellX = (int) (goalX / this.TileSize);
        int touchCellY = (int) (goalY / this.TileSize);

        // Start node and connections
        TileNode startNode = findIndex((spriteCellX * numCellY) + spriteCellY, this.MapNodes);

        // End node and connections
        TileNode endNode = findIndex((touchCellX * numCellY) + touchCellY, this.MapNodes);

        // The returned path once computed
        this.CurrentPath = new DefaultGraphPath<TileNode>();

        // Compute Path!
        this.PathFinder.searchNodePath(startNode, endNode, new TileHeuristic(), this.CurrentPath);

        //this.CurrentPath.reverse();
    }

    /**
     * A helper function to find a TileNode in MapNodes.
     *
     * @param index
     * @param nodeTracker
     * @return
     */
    private TileNode findIndex(Integer index, HashMap<Integer, TileNode> nodeTracker) {
        TileNode temp = null;

        if(nodeTracker.containsKey(index)) {
            temp = nodeTracker.get(index);
        }

        return temp;
    }
}
