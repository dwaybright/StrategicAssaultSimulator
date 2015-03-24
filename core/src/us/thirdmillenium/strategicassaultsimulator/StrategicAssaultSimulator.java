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

import us.thirdmillenium.strategicassaultsimulator.environment.Environment;


public class StrategicAssaultSimulator extends ApplicationAdapter {
    private int tileSize;

    // Environment
    private Environment MyEnvironment;
	
	@Override
	public void create () {
        // Create IndexedAStarPathFinder Object for this Game Level
        this.tileSize = 32;
        this.MyEnvironment = new Environment("TestLevel5.tmx", this.tileSize);

        //this.mapNodes = createIndexedGraph(w, h, this.tileSize);
        //this.indexedPathFinder = new TileAStarPathFinder(this.mapNodes);
        //this.path = new DefaultGraphPath<TileNode>();
	}

	@Override
	public void render () {

        this.MyEnvironment.render();

/*        // Draw Map Nodes
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
        sb.end();*/
	}

/*  @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Determine Click coordinates
        Vector3 clickCoordinates = new Vector3(screenX, screenY, 0);
        Vector3 position = camera.unproject(clickCoordinates);

        //this.path = getPathToTouch(position.x, position.y);

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
    }*/
}
