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

package us.thirdmillenium.strategicassaultsimulator.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

import us.thirdmillenium.strategicassaultsimulator.agents.AgentModel;
import us.thirdmillenium.strategicassaultsimulator.agents.PuppetAgent;
import us.thirdmillenium.strategicassaultsimulator.ai.TileAStarPathFinder;
import us.thirdmillenium.strategicassaultsimulator.ai.TileNode;


public class Environment implements InputProcessor{
    // Debug Flag
    private boolean DEBUG = true;

    // OpenGL Camera for Orientation
    private OrthographicCamera Camera;

    // The Tiled Map Assets for this Environment
    private TiledMap TiledMap;
    private TiledMapRenderer TiledMapRenderer;
    private int TileSize;

    // A mapping of all Nodes that are traversible, and a ShapeRenderer to plot them
    private IndexedGraph<TileNode> TileNodeGraph;
    private TileAStarPathFinder PathFinder;
    private HashMap<Integer, TileNode> TraverseNodes;
    private ShapeRenderer MapNodeSR;

    // Agent Stuff
    private SpriteBatch SpriteBatchRenderer;
    private AgentModel myAgent;

    // An A* Debug Renderer
    private ShapeRenderer LineRenderer;


    /**
     * The constructor takes in a "*.tmx" file, and converts to TileMap.
     * Also prepares LibGDX req'd graphical stuff.
     *
     * @param levelMapName
     */
    public Environment(String levelMapName, int tileSize) {
        // Screen width and height
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Setup camera
        this.Camera = new OrthographicCamera();
        this.Camera.setToOrtho(false, w, h);
        this.Camera.update();

        // Setup map asset
        this.TiledMap = new TmxMapLoader().load(levelMapName);
        this.TiledMapRenderer = new OrthogonalTiledMapRenderer(this.TiledMap);
        this.TileSize = tileSize;

        // Setup input (motion grabbing) processing
        Gdx.input.setInputProcessor(this);

        // Setup Rendering Objects
        this.MapNodeSR = new ShapeRenderer();
        this.SpriteBatchRenderer = new SpriteBatch();
        this.LineRenderer = new ShapeRenderer();

        // Generate TileMap Objects
        createGraphFromTileMap(w, h, tileSize);
        this.PathFinder = new TileAStarPathFinder(this.TileNodeGraph);

        // Init Agents
        try {
            this.myAgent = new PuppetAgent(this.TiledMap, this.TraverseNodes, this.PathFinder, 10, 10, 32);
        } catch( Exception ex ) {
            String msg = ex.getMessage();
        }
    }


    /**
     * Called from the Android "render", this method updates everything that
     * needs to be drawn.
     */
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        // Draw Map
        this.Camera.update();
        this.TiledMapRenderer.setView(this.Camera);
        this.TiledMapRenderer.render();


        // Draw DEBUG information
        if( DEBUG ) {
            // Draw Map Nodes
            this.MapNodeSR.setProjectionMatrix(this.Camera.combined);
            this.MapNodeSR.setColor(Color.OLIVE);
            this.MapNodeSR.begin(ShapeRenderer.ShapeType.Filled);

            if (this.TraverseNodes != null) {
                for (Integer key : this.TraverseNodes.keySet()) {
                    this.MapNodeSR.circle(this.TraverseNodes.get(key).getPixelX(), this.TraverseNodes.get(key).getPixelY(), 10);
                }
            }

            this.MapNodeSR.end();
        }


        // Draw Sprites
        this.SpriteBatchRenderer.setProjectionMatrix(this.Camera.combined);
        this.SpriteBatchRenderer.begin();

        this.myAgent.drawAgent(this.SpriteBatchRenderer);

        this.SpriteBatchRenderer.end();


        // Draw Overlay Lines
        this.LineRenderer.setProjectionMatrix(this.Camera.combined);
        this.LineRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // For each Agent.  Different Colors?
        this.LineRenderer.setColor(Color.BLACK);
        this.myAgent.drawLines(this.LineRenderer);

        this.LineRenderer.end();
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        float h = Gdx.graphics.getHeight();

        ((PuppetAgent) this.myAgent).setPathToGoal(screenX, h - screenY);

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


    /**
     * Generates a mapping of all tiles that are traversible by an Agent
     * @return an IndexedGraph for an A* implementation
     */
    private void createGraphFromTileMap(float width, float height, int tilePixel) {
        try {
            this.TraverseNodes = new HashMap<Integer, TileNode>();
            Array<TileNode> myNodes = new Array<TileNode>();
            TileNode temp;

            int halfTilePixel = tilePixel / 2;
            int numCellY = (int) height / 32;
            int numCellX = (int) width / 32;

            TiledMapTileLayer wallLayer = (TiledMapTileLayer) this.TiledMap.getLayers().get(1);

            // Step 1 : Collect all open tiles, create a TileNode for it, and add

            for (int cellX = 0; cellX < numCellX; cellX++) {

                // Calculate the pixel point for this X
                int x = halfTilePixel + (tilePixel * cellX);

                for (int cellY = 0; cellY < numCellY; cellY++) {

                    // Calculate the pixel point for this Y
                    // Need to reverse because OpenGL indexes opposite of Tiled
                    //int y = (numCellY * tilePixel) - (halfTilePixel + (tilePixel * cellY));
                    int y = halfTilePixel + (tilePixel * cellY);

                    if (!wallLayer.getCell(cellX, cellY).getTile().getProperties().containsKey("blocked")) {
                        // Generate a unique (sequential start at 0) ID for key, then generate TileNode
                        Integer key = new Integer( (cellX * numCellY) + cellY );
                        temp = new TileNode(x, y, cellX, cellY, key);

                        // Add node to the Array<> and the HashMap
                        myNodes.add(temp);
                        this.TraverseNodes.put(key, temp);
                    }
                }
            }

            // Step 2 : Build TileNode Connections

            TileNode node;
            int idTag;

            for( int cellX = 0; cellX < numCellX; cellX++) {

                for( int cellY = 0; cellY < numCellY; cellY++) {

                    // This cell
                    idTag = (cellX * numCellY) + cellY;
                    node = findIndex(idTag);

                    /*
                     *      Check eight surrounding cells
                     */

                    if( node != null ) {

                        // Top Left
                        int topLeftX = cellX - 1;
                        int topLeftY = cellY + 1;
                        int topLeftID = (topLeftX * numCellY) + topLeftY;

                        if (topLeftX >= 0 && topLeftY < numCellY) {
                            temp = findIndex(topLeftID);

                            // If it exists, add to connection list for the TileNode being considered.
                            if (temp != null) {
                                node.addConnection(temp);
                            }
                        }

                        // Left
                        int leftX = cellX - 1;
                        int leftY = cellY;
                        int leftID = (leftX * numCellY) + leftY;

                        if (leftX >= 0) {
                            temp = findIndex(leftID);

                            // If it exists, add to connection list for the TileNode being considered.
                            if (temp != null) {
                                node.addConnection(temp);
                            }
                        }

                        // Bottom Left
                        int botLeftX = cellX - 1;
                        int botLeftY = cellY - 1;
                        int botLeftID = (botLeftX * numCellY) + botLeftY;

                        if (botLeftX >= 0 && botLeftY >= 0) {
                            temp = findIndex(botLeftID);

                            // If it exists, add to connection list for the TileNode being considered.
                            if (temp != null) {
                                node.addConnection(temp);
                            }
                        }


                        // Top
                        int topX = cellX;
                        int topY = cellY + 1;
                        int topID = (topX * numCellY) + topY;

                        if (topY < numCellY) {
                            temp = findIndex(topID);

                            // If it exists, add to connection list for the TileNode being considered.
                            if (temp != null) {
                                node.addConnection(temp);
                            }
                        }

                        // Bottom
                        int botX = cellX;
                        int botY = cellY - 1;
                        int botID = (botX * numCellY) + botY;

                        if (botY >= 0) {
                            temp = findIndex(botID);

                            // If it exists, add to connection list for the TileNode being considered.
                            if (temp != null) {
                                node.addConnection(temp);
                            }
                        }

                        // Top Right
                        int topRightX = cellX + 1;
                        int topRightY = cellY + 1;
                        int topRightID = (topRightX * numCellY) + topRightY;

                        if (topRightX < numCellX && topRightY < numCellY) {
                            temp = findIndex(topRightID);

                            // If it exists, add to connection list for the TileNode being considered.
                            if (temp != null) {
                                node.addConnection(temp);
                            }
                        }

                        // Right
                        int rightX = cellX + 1;
                        int rightY = cellY;
                        int rightID = (rightX * numCellY) + rightY;

                        if (rightX < numCellX) {
                            temp = findIndex(rightID);

                            // If it exists, add to connection list for the TileNode being considered.
                            if (temp != null) {
                                node.addConnection(temp);
                            }
                        }

                        // Bottom Right
                        int botRightX = cellX + 1;
                        int botRightY = cellY - 1;
                        int botRightID = (botRightX * numCellY) + botRightY;

                        if (botRightX < numCellX && botRightY >= 0) {
                            temp = findIndex(botRightID);

                            // If it exists, add to connection list for the TileNode being considered.
                            if (temp != null) {
                                node.addConnection(temp);
                            }
                        }
                    }

                }
            }

            this.TileNodeGraph = new DefaultIndexedGraph<TileNode>(myNodes);

        } catch(Exception ex)
        {

        }
    }


    /**
     * A helper function that searches for the TileNode index provided.
     * @param index
     */
    private TileNode findIndex(Integer index) {
        TileNode temp = null;

        if(this.TraverseNodes.containsKey(index)) {
            temp = this.TraverseNodes.get(index);
        }

        return temp;
    }

}
