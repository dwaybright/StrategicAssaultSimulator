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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

import us.thirdmillenium.strategicassaultsimulator.ai.TileAStarPathFinder;
import us.thirdmillenium.strategicassaultsimulator.ai.TileHeuristic;
import us.thirdmillenium.strategicassaultsimulator.ai.TileNode;


public class StrategicAssaultSimulator extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Texture img;
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;

    // PathFinding
    PathFinder<TileNode> indexedPathFinder;
    IndexedGraph<TileNode> mapNodes;
    HashMap<Integer, TileNode> nodeTracker;
    GraphPath<TileNode> path;
    int tileSize;

    SpriteBatch sb;
    Texture texture;
    Sprite sprite;
    ShapeRenderer sr;
    ShapeRenderer nodeSR;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Setup camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();

        // Setup map
        tiledMap = new TmxMapLoader().load("MyCrappyMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Setup input processing
        Gdx.input.setInputProcessor(this);

        // Setup line
        sr = new ShapeRenderer();
        nodeSR = new ShapeRenderer();

        // Setup a sprite
        sb = new SpriteBatch();
        texture = new Texture("goodGuyDotArrow.png");
        sprite = new Sprite(texture);

        // Create IndexedAStarPathFinder Object for this Game Level
        this.tileSize = 32;
        this.mapNodes = createIndexedGraph(w, h, this.tileSize);
        this.indexedPathFinder = new TileAStarPathFinder(this.mapNodes);
        this.path = new DefaultGraphPath<TileNode>();
	}

    /**
     * Generates a mapping of all tiles that are traversible by an agent
     * @return an IndexedGraph for IndexedAStarPathFinder()
     */
    private IndexedGraph<TileNode> createIndexedGraph(float width, float height, int tilePixel) {
        try {
            this.nodeTracker = new HashMap<Integer, TileNode>();
            Array<TileNode> myNodes = new Array<TileNode>();
            TileNode temp;

            int halfTilePixel = tilePixel / 2;
            int numCellY = (int) height / 32;
            int numCellX = (int) width / 32;

            TiledMapTileLayer wallLayer = (TiledMapTileLayer) this.tiledMap.getLayers().get(1);

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
                        nodeTracker.put(key, temp);
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
                    node = findIndex(idTag, this.nodeTracker);

                    /*
                     *      Check eight surrounding cells
                     */

                    if( node != null ) {

                        // Top Left
                        int topLeftX = cellX - 1;
                        int topLeftY = cellY + 1;
                        int topLeftID = (topLeftX * numCellY) + topLeftY;

                        if (topLeftX >= 0 && topLeftY < numCellY) {
                            temp = findIndex(topLeftID, nodeTracker);

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
                            temp = findIndex(leftID, nodeTracker);

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
                            temp = findIndex(botLeftID, nodeTracker);

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
                            temp = findIndex(topID, nodeTracker);

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
                            temp = findIndex(botID, nodeTracker);

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
                            temp = findIndex(topRightID, nodeTracker);

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
                            temp = findIndex(rightID, nodeTracker);

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
                            temp = findIndex(botRightID, nodeTracker);

                            // If it exists, add to connection list for the TileNode being considered.
                            if (temp != null) {
                                node.addConnection(temp);
                            }
                        }
                    }

                }
            }

            return new DefaultIndexedGraph<TileNode>(myNodes);

        } catch(Exception ex)
        {
            return null;
        }
    }

    /**
     * A helper function that searches for the TileNode index provided.
     * @param index
     */
    private TileNode findIndex(Integer index, HashMap<Integer, TileNode> nodeTracker) {
        TileNode temp = null;

        if(nodeTracker.containsKey(index)) {
            temp = nodeTracker.get(index);
        }

        return temp;
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();



        // Draw Map Nodes
        nodeSR.setProjectionMatrix(camera.combined);
        nodeSR.setColor(Color.OLIVE);
        nodeSR.begin(ShapeRenderer.ShapeType.Filled);

        if( this.nodeTracker != null ) {
            for(Integer key : this.nodeTracker.keySet()) {
                nodeSR.circle(this.nodeTracker.get(key).getPixelX(), this.nodeTracker.get(key).getPixelY(), 10);
            }
        }

        nodeSR.end();


        // Draw Path
        sr.setColor(Color.BLACK);
        sr.setProjectionMatrix(this.camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        for( int i = 1; i < path.getCount(); i++ ) {
            sr.rectLine(path.get(i - 1).getPixelX(), path.get(i - 1).getPixelY(),
                    path.get(i).getPixelX(), path.get(i).getPixelY(), 5);
        }

        sr.end();

        // Draw Sprite
        sb.setProjectionMatrix(camera.combined);
        sb.begin();
        sprite.draw(sb);
        sb.end();
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
        // Determine Click coordinates
        Vector3 clickCoordinates = new Vector3(screenX, screenY, 0);
        Vector3 position = camera.unproject(clickCoordinates);

        this.path = getPathToTouch(position.x, position.y);

        sprite.setPosition(position.x, position.y);


        return true;
    }

    private GraphPath<TileNode> getPathToTouch(float touchX, float touchY) {
        // Generate Cell X and Y positions
        int numCellY = Gdx.graphics.getHeight() / this.tileSize;

        float spriteX = this.sprite.getX();
        float spriteY = this.sprite.getY();

        int spriteCellX = (int) (spriteX / this.tileSize);
        int spriteCellY = (int) (spriteY / this.tileSize);

        int touchCellX = (int) (touchX / this.tileSize);
        int touchCellY = (int) (touchY / this.tileSize);

        // Start node and connections
        //TileNode startNode = new TileNode(spriteX,spriteY,spriteCellX,spriteCellY,950);
        TileNode startNode = findIndex((spriteCellX * numCellY) + spriteCellY, this.nodeTracker);

        // End node and connections
        //TileNode endNode = new TileNode(touchX, touchY, touchCellX, touchCellY, 951);
        TileNode endNode = findIndex((touchCellX * numCellY) + touchCellY, this.nodeTracker);

        // The returned path once computed
        //GraphPath<Connection<TileNode>> path = new DefaultGraphPath<Connection<TileNode>>();
        GraphPath<TileNode> path = new DefaultGraphPath<TileNode>();

        // Compute Path!
        //this.indexedPathFinder.searchConnectionPath(startNode, endNode, new TileHeuristic(), path);
        try {
            this.indexedPathFinder.searchNodePath(startNode, endNode, new TileHeuristic(), path);
        } catch( Exception ex) {
            String msg = ex.toString();
        }

        return path;
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
}
