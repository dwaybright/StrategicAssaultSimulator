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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.HashMap;

import us.thirdmillenium.strategicassaultsimulator.ai.TileAStarPathFinder;
import us.thirdmillenium.strategicassaultsimulator.ai.TileHeuristic;
import us.thirdmillenium.strategicassaultsimulator.ai.TileNode;
import us.thirdmillenium.strategicassaultsimulator.brains.Brain;
import us.thirdmillenium.strategicassaultsimulator.brains.PuppetBrain;


public class PuppetAgent extends AgentModel {
    // Game World
    private TiledMapTileLayer CollisionLayer;
    private Sprite Sprite;
    private Texture Alive;
    private Texture Dead;
    private int TileSize;
    private HashMap<Integer, TileNode> MapNodes;
    private TileAStarPathFinder PathFinder;

    // Agent location
    private GraphPath<TileNode> CurrentPath;
    private float Pixel_X;
    private float Pixel_Y;
    private int Cell_X;
    private int Cell_Y;
    private float Angle;

    // Agent Physical Attributes
    private int Health;
    private float MovementSpeed;
    private float Eyesight;
    private float Hearing;

    // Agent Equipment
    //private WeaponModel MyWeapon;
    //private ArmorModel MyArmor;

    // Agent Mental Model
    private Brain Control;



    public PuppetAgent(TiledMapTileLayer collisionLayer, HashMap<Integer, TileNode> mapNodes,
                       TileAStarPathFinder pathFinder,
                       int pixelX, int pixelY, int tileSize)
    {
        // Setup game world parameters
        this.CollisionLayer = collisionLayer;
        this.TileSize = tileSize;
        this.MapNodes = mapNodes;
        this.PathFinder = pathFinder;

        // Setup Graphics
        this.Alive = new Texture("goodGuyDot.png");
        this.Dead = new Texture("deadDot.png");
        this.Sprite = new Sprite(Alive);

        // Setup Start Location


        // Set Basic Values
        this.Health = 50;
        this.MovementSpeed = 5;
        this.Eyesight = 10;
        this.Hearing = 20;
        this.Angle = 0;

        // Set Control
        this.Control = new PuppetBrain();
    }

    @Override
    public void agentHit() {

    }

    @Override
    public void updateAgentState() {

    }


    private GraphPath<TileNode> getPathToGoal(float goalX, float goalY) {
        // Generate Cell X and Y positions
        int numCellY = Gdx.graphics.getHeight() / this.TileSize;

        float spriteX = this.Sprite.getX();
        float spriteY = this.Sprite.getY();

        int spriteCellX = (int) (spriteX / this.TileSize);
        int spriteCellY = (int) (spriteY / this.TileSize);

        int touchCellX = (int) (goalX / this.TileSize);
        int touchCellY = (int) (goalY / this.TileSize);

        // Start node and connections
        //TileNode startNode = new TileNode(spriteX,spriteY,spriteCellX,spriteCellY,950);
        TileNode startNode = findIndex((spriteCellX * numCellY) + spriteCellY, this.MapNodes);

        // End node and connections
        //TileNode endNode = new TileNode(touchX, touchY, touchCellX, touchCellY, 951);
        TileNode endNode = findIndex((touchCellX * numCellY) + touchCellY, this.MapNodes);

        // The returned path once computed
        //GraphPath<Connection<TileNode>> path = new DefaultGraphPath<Connection<TileNode>>();
        this.CurrentPath = new DefaultGraphPath<TileNode>();

        // Compute Path!
        //this.indexedPathFinder.searchConnectionPath(startNode, endNode, new TileHeuristic(), path);
        this.PathFinder.searchNodePath(startNode, endNode, new TileHeuristic(), this.CurrentPath);

        return this.CurrentPath;
    }

    private TileNode findIndex(Integer index, HashMap<Integer, TileNode> nodeTracker) {
        TileNode temp = null;

        if(nodeTracker.containsKey(index)) {
            temp = nodeTracker.get(index);
        }

        return temp;
    }
}
