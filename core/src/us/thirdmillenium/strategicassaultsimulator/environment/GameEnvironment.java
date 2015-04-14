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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.math.Vector2;
import org.neuroph.core.NeuralNetwork;

import us.thirdmillenium.strategicassaultsimulator.environment.Params;
import us.thirdmillenium.strategicassaultsimulator.agents.*;
import us.thirdmillenium.strategicassaultsimulator.ai.tile.TileAStarPathFinder;
import us.thirdmillenium.strategicassaultsimulator.ai.tile.TileNode;
import us.thirdmillenium.strategicassaultsimulator.graphics.GraphicsHelpers;
import us.thirdmillenium.strategicassaultsimulator.graphics.Line;


public class GameEnvironment extends Environment implements InputProcessor {
    // Debug Flag
    private boolean DEBUG = true;
    private boolean DRAW = true;

    private boolean PUPPET = false;
    private boolean NNET = true;
    //private float lastAngle = 0;

    // Bullet Tracker
    private Set<GreenBullet> BulletTracker;

    // Agent Trackers
    private Set<AgentModel> shooters;
    private Set<AgentModel> trainees;
    private AgentModel puppet;

    // OpenGL Camera for Orientation
    private OrthographicCamera Camera;

    // Collision lines
    private Set<Line> collisionLines;

    // The Tiled Map Assets for this Environment
    private TiledMap TiledMap;
    private TiledMapRenderer TiledMapRenderer;
    private float width;
    private float height;

    // A mapping of all Nodes that are traversible, and a ShapeRenderer to plot them
    private GraphPath<TileNode> TileNodeGraph;
    private ConcurrentHashMap<Integer, TileNode> TraverseNodes;

    // Shape Renderer
    private ShapeRenderer MapNodeSR;

    // A Renderer for the Agents
    private SpriteBatch SpriteBatchRenderer;

    // An A* Debug Renderer
    private ShapeRenderer LineRenderer;


    /**
     * The constructor takes in a "*.tmx" file, and converts to TileMap.
     * Also prepares LibGDX req'd graphical stuff.
     */
    public GameEnvironment(String nnetPath, Random random, int testLevelID) {
        // Screen width and height
        this.width  = 800;	//Gdx.graphics.getWidth();
        this.height = 1216;	//Gdx.graphics.getHeight();
        //Gdx.graphics.setDisplayMode((int)width, (int)height, false);

        // The test level to display
        String levelPath = Params.TileMapsPath + "TestLevel" + testLevelID + ".tmx";

        // Setup camera
        this.Camera = new OrthographicCamera();
        this.Camera.setToOrtho(false, width, height);
        this.Camera.update();

        // Setup map asset
        this.TiledMap = new TmxMapLoader().load(levelPath);
        this.TiledMapRenderer = new OrthogonalTiledMapRenderer(this.TiledMap);

        // Setup input (motion grabbing) processing
        Gdx.input.setInputProcessor(this);

        // Setup Rendering Objects
        this.MapNodeSR = new ShapeRenderer();
        this.SpriteBatchRenderer = new SpriteBatch();
        this.LineRenderer = new ShapeRenderer();

        // Setup Trackers
        this.BulletTracker  = Collections.newSetFromMap(new ConcurrentHashMap<GreenBullet, Boolean>());
        this.trainees       = Collections.newSetFromMap(new ConcurrentHashMap<AgentModel, Boolean>());
        this.shooters       = Collections.newSetFromMap(new ConcurrentHashMap<AgentModel, Boolean>());
        this.collisionLines = createCollisionLineSet(this.TiledMap);

        // Generate TileMap Objects
        this.TraverseNodes = new ConcurrentHashMap<Integer, TileNode>();
        this.TileNodeGraph = createGraphFromTileMap(this.TraverseNodes, (TiledMapTileLayer) this.TiledMap.getLayers().get(1));

        // Add a Training Shooter
        this.shooters.add(new TrainingShooter(190,  630, this.trainees, this.shooters, this.BulletTracker, random));
        this.shooters.add(new TrainingShooter(540,  700, this.trainees, this.shooters, this.BulletTracker, random));
        this.shooters.add(new TrainingShooter(510,   90, this.trainees, this.shooters, this.BulletTracker, random));
        this.shooters.add(new TrainingShooter( 65, 1090, this.trainees, this.shooters, this.BulletTracker, random));
        this.shooters.add(new TrainingShooter(740, 1090, this.trainees, this.shooters, this.BulletTracker, random));
        this.shooters.add(new TrainingShooter(410,  960, this.trainees, this.shooters, this.BulletTracker, random));

        int startX = 16;
        int startY = 16;
        int startAngle = 270;

        int sign = 1;







        switch(testLevelID) {
            case 1:
                startX = 16;
                startY = 16;
                sign = 1;
                startAngle = 270;

                break;
            case 2:
                startX = 784;
                startY = 16;
                sign = -1;
                startAngle = 180;
                break;
            case 3:
                startX = 784;
                startY = 1200;
                sign = -1;
                startAngle = 90;
                break;
            case 4:
                startX = 16;
                startY = 1200;
                sign = 1;
                startAngle = 225;
                break;
            case 5:
                startX = 16;
                startY = 16;
                sign = 1;
                startAngle = 270;
                break;
        }

        // Add the Trainee
        if( NNET ) { 
        	/* this.trainees.add(new TrainingAgent(testLevelID, NeuralNetwork.createFromFile(nnetPath), startX, startY,
                              this.TraverseNodes, random, this.TiledMap, this.trainees, this.shooters, this.BulletTracker)); */

            HashSet<TileNode> prefNodeTracker = new HashSet<TileNode>();
            GraphPath<TileNode> prefPath = GraphicsHelpers.getPrefPathTest(testLevelID, prefNodeTracker, this.TraverseNodes);

            this.trainees.add(new ConeAgent(new Vector2(startX, startY), startAngle, 100, 4 * Params.MapTileSize, 10, Params.TrainingAgentLivePNG,
                    random, this.collisionLines, Params.PathToBaseNN, prefPath, prefNodeTracker,
                    this.TiledMap, this.trainees, this.shooters, this.BulletTracker, this.TraverseNodes));
        }

        if( PUPPET ) {
            //this.puppet = new PuppetAgent(this.TiledMap, this.TraverseNodes, new TileAStarPathFinder(), startX, startY, this.BulletTracker, this.shooters);

            Vector2 startVector   = new Vector2(startX, startY);

            int degreeVision      = 100;
            int depthVision       = 4 * Params.MapTileSize;
            int health            = 20;

            //HashSet<TileNode> tracker = new HashSet<TileNode>();
            //prefPath = new DefaultGraphPath<TileNode>(); //GraphicsHelpers.getPrefPathTest(testLevelID, tracker, this.TraverseNodes);

            HashSet<TileNode> prefNodeTracker = new HashSet<TileNode>();
            GraphPath<TileNode> prefPath = GraphicsHelpers.getPrefPathTest(testLevelID, prefNodeTracker, this.TraverseNodes);

            this.puppet = new ConePuppetAgent(startVector, startAngle, degreeVision, depthVision, health, Params.TrainingAgentLivePNG, true, random,
                    this.collisionLines, prefPath, prefNodeTracker, this.TraverseNodes, this.TiledMap, this.trainees, this.shooters, this.BulletTracker);

            this.trainees.add(this.puppet);

            // Add Team members

            HashSet<TileNode> prefNodeTracker2 = new HashSet<TileNode>();
            GraphPath<TileNode> prefPath2 = GraphicsHelpers.getPrefPathTest(testLevelID, prefNodeTracker2, this.TraverseNodes);

            HashSet<TileNode> prefNodeTracker3 = new HashSet<TileNode>();
            GraphPath<TileNode> prefPath3 = GraphicsHelpers.getPrefPathTest(testLevelID, prefNodeTracker3, this.TraverseNodes);

            HashSet<TileNode> prefNodeTracker4 = new HashSet<TileNode>();
            GraphPath<TileNode> prefPath4 = GraphicsHelpers.getPrefPathTest(testLevelID, prefNodeTracker4, this.TraverseNodes);


            trainees.add(new ConePuppetAgent(new Vector2(startX + (sign * 40), startY), startAngle, degreeVision, depthVision, health, Params.TrainingAgentLivePNG, true, random,
                    this.collisionLines, prefPath2, prefNodeTracker2, this.TraverseNodes, this.TiledMap, this.trainees, this.shooters, this.BulletTracker));



            trainees.add(new ConePuppetAgent(new Vector2(startX + (sign * 80), startY), startAngle, degreeVision, depthVision, health, Params.TrainingAgentLivePNG, true, random,
                    this.collisionLines, prefPath3, prefNodeTracker3, this.TraverseNodes, this.TiledMap, this.trainees, this.shooters, this.BulletTracker));



            trainees.add(new ConePuppetAgent(new Vector2(startX + (sign * 120), startY), startAngle, degreeVision, depthVision, health, Params.TrainingAgentLivePNG, true, random,
                    this.collisionLines, prefPath4, prefNodeTracker4, this.TraverseNodes, this.TiledMap, this.trainees, this.shooters, this.BulletTracker));
        }
    }


    @Override
    public void simulate() { /* Do Nothing */ }


    @Override
    public void simulate(float deltaTime) {
        // Compute time delta (max of frame speed)
        deltaTime = (float) Math.min(deltaTime, 1 / Params.FramesPerSecond);

        if( DRAW ) {
            // Clear Background
            Gdx.gl.glClearColor(1, 0, 0, 1);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Draw Map
            this.Camera.update();
            this.TiledMapRenderer.setView(this.Camera);
            this.TiledMapRenderer.render();
        }


        // Test if Bullets Intersected with Anything
        MapObjects wallMapObjects = this.TiledMap.getLayers().get(2).getObjects();
        Iterator<GreenBullet> bullets = this.BulletTracker.iterator();
        this.SpriteBatchRenderer.setProjectionMatrix(this.Camera.combined);
        this.SpriteBatchRenderer.begin();

        while(bullets.hasNext()) {
            // Collect a Bullet to consider
            GreenBullet currentBullet = bullets.next();

            if( DRAW ) { currentBullet.drawSprite(this.SpriteBatchRenderer); }

            currentBullet.updateBullet(deltaTime);

            // If bullet is off-screen, remove it.
            if( currentBullet.getBulletVector().x < 0 ||
                    currentBullet.getBulletVector().x > this.width ||
                    currentBullet.getBulletVector().y < 0 ||
                    currentBullet.getBulletVector().y > this.height)
            {
                this.BulletTracker.remove(currentBullet);
            } else {
                // Compare with all Agents
                Rectangle agentBound;

                Iterator<AgentModel> shootItr = this.shooters.iterator();

                while(shootItr.hasNext()) {
                    AgentModel currShooter = shootItr.next();

                    if( !currentBullet.thisAgentShotMe(currShooter) && Intersector.overlapConvexPolygons(GraphicsHelpers.convertRectangleToPolygon(currShooter.getBoundingRectangle()), currentBullet.getBulletPath())) {
                        currShooter.agentHit();
                        this.BulletTracker.remove(currentBullet);
                    }
                }


                Iterator<AgentModel> agentItr = this.trainees.iterator();

                while(agentItr.hasNext()) {
                    AgentModel currAgent = agentItr.next();

                    if( !currentBullet.thisAgentShotMe(currAgent) && Intersector.overlapConvexPolygons(GraphicsHelpers.convertRectangleToPolygon(currAgent.getBoundingRectangle()), currentBullet.getBulletPath())) {
                        currAgent.agentHit();
                        this.BulletTracker.remove(currentBullet);
                    }
                }


                // Compare with all Wall Boundaries
                for( int i = 0; i < wallMapObjects.getCount(); i++) {
                    Object rectangleMapObject = wallMapObjects.get(i);

                    // Make sure this is a Rectangle from Tiled describing a wall.
                    if( rectangleMapObject.getClass() == RectangleMapObject.class ) {
                        Rectangle wallRectangle = ((RectangleMapObject)rectangleMapObject).getRectangle();
                        Polygon polyBound = GraphicsHelpers.convertRectangleToPolygon(wallRectangle);

                        // Terminate when hitting a wall
                        if( Intersector.overlapConvexPolygons(polyBound, currentBullet.getBulletPath())) {
                            this.BulletTracker.remove(currentBullet);
                        }
                    }
                }
            }
        }

        this.SpriteBatchRenderer.end();


        // Draw DEBUG information
        if( DEBUG && DRAW) {
            // Draw Map Nodes
            /*this.MapNodeSR.setProjectionMatrix(this.Camera.combined);
            this.MapNodeSR.setColor(Color.OLIVE);
            this.MapNodeSR.begin(ShapeRenderer.ShapeType.Filled);

            if (this.TraverseNodes != null) {
                for (Integer key : this.TraverseNodes.keySet()) {
                    this.MapNodeSR.circle(this.TraverseNodes.get(key).getPixelX(), this.TraverseNodes.get(key).getPixelY(), 10);
                }
            }

            this.MapNodeSR.end();*/

            // Draw Overlay Lines
            this.LineRenderer.setProjectionMatrix(this.Camera.combined);
            this.LineRenderer.begin(ShapeRenderer.ShapeType.Filled);

            // For each Agent.  Different Colors?
            this.LineRenderer.setColor(Color.BLACK);
            
            /*if( PUPPET ) {
            	this.puppet.drawPath(this.LineRenderer);
            }*/

            Iterator<AgentModel> agentItr = this.trainees.iterator();

            while(agentItr.hasNext()) {
                AgentModel currAgent = agentItr.next();
                currAgent.drawPath(this.LineRenderer);
            }

            this.LineRenderer.end();
        }


        // Draw Agent Sprites

        this.SpriteBatchRenderer.begin();
        
        /*if( PUPPET ) {
        	this.puppet.updateAgent(deltaTime);
        	this.puppet.drawAgent(this.SpriteBatchRenderer);
        }*/

        Iterator<AgentModel> shootItr = this.shooters.iterator();

        while(shootItr.hasNext()) {
            AgentModel currShooter = shootItr.next();

            currShooter.updateAgent(deltaTime);
            if( DRAW ) { currShooter.drawAgent(this.SpriteBatchRenderer); }
        }


        Iterator<AgentModel> agentItr = this.trainees.iterator();

        while(agentItr.hasNext()) {
            AgentModel currAgent = agentItr.next();

            currAgent.updateAgent(deltaTime);

            if (DRAW) {
                currAgent.drawAgent(this.SpriteBatchRenderer);
            }
        }

        this.SpriteBatchRenderer.end();


        if( DEBUG ) {
            Iterator<AgentModel> agentItr2 = this.trainees.iterator();

            ShapeRenderer visionCone = new ShapeRenderer();
            visionCone.setProjectionMatrix(this.Camera.combined);
            visionCone.begin(ShapeRenderer.ShapeType.Line);
            visionCone.setColor(Color.YELLOW);

            while (agentItr2.hasNext()) {
                AgentModel currAgent = agentItr2.next();

                currAgent.drawVision(visionCone);
            }

            visionCone.end();
        }

        /*// Test Draw the Collision Boxes
        if( DEBUG && DRAW ) {
            ShapeRenderer anotherShapeRenderer = new ShapeRenderer();

	        anotherShapeRenderer.setProjectionMatrix(this.Camera.combined);
	        anotherShapeRenderer.begin(ShapeRenderer.ShapeType.Line);

	        bullets = this.BulletTracker.iterator();

	        while(bullets.hasNext()) {
	        	GreenBullet currentBullet = bullets.next();
	        	anotherShapeRenderer.polygon(currentBullet.getBulletPath().getTransformedVertices());
	        }

	        for(int i = 0; i < wallMapObjects.getCount(); i++ ){
	        	Object obj = wallMapObjects.get(i);

	        	if( obj.getClass() == RectangleMapObject.class ) {
	        		Rectangle boundary = ((RectangleMapObject)obj).getRectangle();
	        		anotherShapeRenderer.rect(boundary.x, boundary.y, boundary.width, boundary.height);

	        		float[] vertices = {
	        			boundary.x, boundary.y,
	        			boundary.x + boundary.width, boundary.y,
	        			boundary.x + boundary.width, boundary.y + boundary.height,
	        			boundary.x, boundary.y + boundary.height
	        		};

	        		//Polygon polyBound = new Polygon(vertices);
	        		anotherShapeRenderer.setColor(Color.BLUE);
	        		anotherShapeRenderer.polygon(vertices);
	        	}
	        }

	        anotherShapeRenderer.end();
        }*/


    }

    @Override
    public long getScore() {
        return 0;
    }

    @Override
    public void dispose() {
        this.MapNodeSR.dispose();
        this.SpriteBatchRenderer.dispose();
        this.LineRenderer.dispose();
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

        System.out.println("Screen X: " + (screenX*2) + ", Y: " + (screenY*2));
        System.out.println("Game   X: " + (screenX*2) + ", Y: " + (this.height - (screenY*2)));
        System.out.println();

        if( PUPPET ) {
            this.puppet.setPathToGoal((2 * screenX), this.height - (2 * screenY));
        }

        //TrainingAgent ta = new TrainingAgent(null, null, (screenX*2), ((38*32)-(screenY*2)), this.trainees, this.shooters, this.BulletTracker);
        //this.trainees.add(ta);

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
