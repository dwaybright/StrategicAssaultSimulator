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

package us.thirdmillenium.strategicassaultsimulator.pathselect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.pfa.GraphPath;
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
import com.badlogic.gdx.utils.Array;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import us.thirdmillenium.strategicassaultsimulator.simulation.ai.tile.TileAStarPathFinder;
import us.thirdmillenium.strategicassaultsimulator.simulation.ai.tile.TileHeuristic;
import us.thirdmillenium.strategicassaultsimulator.simulation.ai.tile.TileNode;
import us.thirdmillenium.strategicassaultsimulator.simulation.environment.Environment;
import us.thirdmillenium.strategicassaultsimulator.simulation.graphics.GraphicsHelpers;


public class PathSelection implements InputProcessor {
    // Other Variables
    private OrthographicCamera camera;
    private int pathSelectionState = 0;
    private int levelID;

    // Path Variables
    private Array<TileNode> prefPath;
    private HashSet<TileNode> prefPathHashNodes;
    private GraphPath<TileNode> prefGraphPath;

    // The Tiled Map Assets for this Environment
    private TiledMap TiledMap;
    private TiledMapRenderer TiledMapRenderer;
    private ConcurrentHashMap<Integer, TileNode> TraverseNodes;
    private GraphPath<TileNode> TileNodeGraph;
    private TileAStarPathFinder pathFinder;
    private TileHeuristic heuristic;

    // Sprite Variables
    private Sprite pickStartCornerText;
    private Sprite topRightSprite;
    private Sprite topLeftSprite;
    private Sprite bottomRightSprite;
    private Sprite bottomLeftSprite;
    private Sprite startSprite;

    // Draw Variables
    private ShapeRenderer shapeRender;
    private SpriteBatch spriteBatch;



    public PathSelection(int levelID) {
        this.levelID = levelID;

        // Setup camera
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 1216);
        this.camera.update();

        // Setup Renderers
        this.shapeRender = new ShapeRenderer();
        this.spriteBatch = new SpriteBatch();

        // Setup input (motion grabbing) processing
        Gdx.input.setInputProcessor(this);

        // The test level to display
        String levelPath = "TestLevel" + levelID + ".tmx";
        this.TiledMap = new TmxMapLoader().load(levelPath);
        this.TiledMapRenderer = new OrthogonalTiledMapRenderer(this.TiledMap);

        // Generate TileMap Objects
        this.TraverseNodes = new ConcurrentHashMap<Integer, TileNode>();
        this.TileNodeGraph = Environment.createGraphFromTileMap(this.TraverseNodes, (TiledMapTileLayer) this.TiledMap.getLayers().get(1));

        // Initialize Preferred Path
        this.prefPath = new Array<TileNode>();
        //this.prefPath.add(GraphicsHelpers.findTileNodeByScreenTouch(16, 1200, this.TraverseNodes));

        // Sprites Setup
        this.pickStartCornerText = new Sprite(new Texture("PathSelect/SelectCornerYellow.png"));
        this.topRightSprite      = new Sprite(new Texture("PathSelect/startPoint.png"));
        this.topLeftSprite       = new Sprite(new Texture("PathSelect/startPoint.png"));
        this.bottomRightSprite   = new Sprite(new Texture("PathSelect/startPoint.png"));
        this.bottomLeftSprite    = new Sprite(new Texture("PathSelect/startPoint.png"));

        this.pickStartCornerText.setCenter(400, 606);
        this.topLeftSprite.setCenter    ( 32, 1184);
        this.topRightSprite.setCenter   (768, 1184);
        this.bottomLeftSprite.setCenter ( 32,   32);
        this.bottomRightSprite.setCenter(768,   32);
    }


    public void draw() {
        // Clear Background
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw Map
        this.camera.update();
        this.TiledMapRenderer.setView(this.camera);
        this.TiledMapRenderer.render();

        switch( this.pathSelectionState ) {
            case 0:
                this.spriteBatch.setProjectionMatrix(this.camera.combined);
                this.spriteBatch.begin();

                this.pickStartCornerText.draw(this.spriteBatch);
                this.topLeftSprite.draw(this.spriteBatch);
                this.bottomLeftSprite.draw(this.spriteBatch);

                if(this.levelID < 5) {
                    this.topRightSprite.draw(this.spriteBatch);
                    this.bottomRightSprite.draw(this.spriteBatch);
                }

                this.spriteBatch.end();
                break;

            case 1:
                // Draw the Current Preferred Path
                this.shapeRender.setProjectionMatrix(this.camera.combined);
                this.shapeRender.begin(ShapeRenderer.ShapeType.Filled);
                this.shapeRender.setColor(Color.CYAN);

                if (this.prefPath.size > 1) {
                    for (int i = 0; i < this.prefPath.size - 1; i++) {
                        this.shapeRender.rectLine(this.prefPath.get(i + 1).getPixelX(), this.prefPath.get(i + 1).getPixelY(),
                                this.prefPath.get(i).getPixelX(), this.prefPath.get(i).getPixelY(), 4);
                    }
                }

                this.shapeRender.end();


                // Draw Start Graphic
                this.spriteBatch.setProjectionMatrix(this.camera.combined);
                this.spriteBatch.begin();

                this.startSprite.draw(this.spriteBatch);

                this.spriteBatch.end();

                break;
        }
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

        switch( this.pathSelectionState ) {
            case 0:
                int x = screenX;
                int y = 1216 - screenY;
                boolean startSelected = false;

                // Store Starting
                if( x < 100 && y < 100 ) {
                    this.startSprite = this.bottomLeftSprite;
                    startSelected = true;
                } else if( x < 100 && y > 1116 ) {
                    this.startSprite = this.topLeftSprite;
                    startSelected = true;
                } else if( x > 700 && y < 100 && this.levelID < 5) {
                    this.startSprite = this.bottomRightSprite;
                    startSelected = true;
                } else if( x > 700 && y > 1116 && this.levelID < 5) {
                    this.startSprite = this.topRightSprite;
                    startSelected = true;
                }


                // Once selected, add first TileNode to preferred path, then change state
                if(startSelected) {
                    this.prefPath.add(GraphicsHelpers.findTileNodeByScreenTouch(screenX, screenY, this.TraverseNodes));
                    this.pathSelectionState = 1;
                    return true;
                }

                break;

            case 1:
                this.pathFinder = new TileAStarPathFinder(null);
                this.heuristic = new TileHeuristic();

                TileNode lastPoint = this.prefPath.get(this.prefPath.size - 1);
                TileNode touch = GraphicsHelpers.findTileNodeByScreenTouch(screenX, screenY, this.TraverseNodes);

                Array<TileNode> partialPath = new Array<TileNode>(100);
                this.pathFinder.searchNodePath2(lastPoint, touch, this.heuristic, partialPath);

                for (int i = 1; i < partialPath.size; i++) {
                    this.prefPath.add(partialPath.get(i));
                }
                break;
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
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
