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

package us.thirdmillenium.strategicassaultsimulator.startup;

import android.content.res.AssetManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;


public class Startup implements InputProcessor {
    // Sprite Drawing
    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;

    // Level Variables
    private Rectangle[] levelSelectRects;
    private int chosenLevel = 1;
    private Sprite chooseLevelText;
    private Sprite level1;
    private Sprite level2;
    private Sprite level3;
    private Sprite level4;
    private Sprite level5;

    // Player Variables
    private Rectangle[] playerSelectRects;
    private int chosenPlayer = 0;
    private Sprite choosePlayerText;
    private Sprite playerPuppet;
    private Sprite playerNN;

    // Enemy Variables
    private Rectangle[] enemySelectRects;
    private int chosenEnemy = 0;
    private Sprite chooseEnemyText;
    private Sprite enemyStationary;
    private Sprite enemyPuppet;
    private Sprite enemyNN;

    // Path Variables
    private boolean readyForPath = false;
    private Rectangle[] pathSelectRects;
    private Sprite readyPathText;



    public Startup(AssetManager assman) {
        // Setup camera
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 1216);
        this.camera.update();

        // Setup input (motion grabbing) processing
        Gdx.input.setInputProcessor(this);

        // Setup SpriteBatch
        this.spriteBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        // Load Level Images
        this.chooseLevelText = new Sprite(new Texture("LevelImages/ChooseLevel.png"));
        this.level1 = new Sprite(new Texture("LevelImages/Small_Level1.png"));
        this.level2 = new Sprite(new Texture("LevelImages/Small_Level2.png"));
        this.level3 = new Sprite(new Texture("LevelImages/Small_Level3.png"));
        this.level4 = new Sprite(new Texture("LevelImages/Small_Level4.png"));
        this.level5 = new Sprite(new Texture("LevelImages/Small_Level5.png"));

        // Set Level Image Locations
        this.chooseLevelText.setX(50);
        this.chooseLevelText.setY(1100);
        this.level1.setCenter(100, 1000);
        this.level2.setCenter(250, 1000);
        this.level3.setCenter(400, 1000);
        this.level4.setCenter(550, 1000);
        this.level5.setCenter(700, 1000);

        // Create Level Selection Rectangles
        this.levelSelectRects = new Rectangle[5];

        for( int i = 0; i < 5; i ++ )
            this.levelSelectRects[i] = new Rectangle();

        this.levelSelectRects[0].set( 30, 904, 140, 192);
        this.levelSelectRects[1].set(180, 904, 140, 192);
        this.levelSelectRects[2].set(330, 904, 140, 192);
        this.levelSelectRects[3].set(480, 904, 140, 192);
        this.levelSelectRects[4].set(630, 904, 140, 192);

        // Load Player Images
        this.choosePlayerText = new Sprite(new Texture("LevelImages/choosePlayer.png"));
        this.playerPuppet = new Sprite(new Texture("LevelImages/puppet.png"));
        this.playerNN = new Sprite(new Texture("LevelImages/nnetPic.png"));

        // Set Player Image Locations
        this.choosePlayerText.setX(50);
        this.choosePlayerText.setY(800);
        this.playerPuppet.setCenter(150, 670);
        this.playerNN.setCenter(400, 670);

        // Setup Player Boxes
        this.playerSelectRects = new Rectangle[2];

        for( int i = 0; i < 2; i ++ )
            this.playerSelectRects[i] = new Rectangle();

        this.playerSelectRects[0].set(30, 550, 240, 240);
        this.playerSelectRects[1].set(280, 550, 240, 240);

        // Load Enemy Images
        this.chooseEnemyText = new Sprite(new Texture("LevelImages/chooseEnemy.png"));
        this.enemyStationary = new Sprite(new Texture("LevelImages/stationary.png"));
        this.enemyPuppet = new Sprite(new Texture("LevelImages/puppet.png"));
        this.enemyNN = new Sprite(new Texture("LevelImages/nnetPic.png"));

        // Set Enemy Image Locations
        this.chooseEnemyText.setX(50);
        this.chooseEnemyText.setY(450);
        this.enemyStationary.setCenter(150, 340);
        this.enemyPuppet.setCenter(400, 340);
        this.enemyNN.setCenter(650, 340);

        // Setup Enemy Boxes
        this.enemySelectRects = new Rectangle[3];

        for( int i = 0; i < 3; i ++ )
            this.enemySelectRects[i] = new Rectangle();

        this.enemySelectRects[0].set(30, 220, 240, 240);
        this.enemySelectRects[1].set(280, 220, 240, 240);
        this.enemySelectRects[2].set(530, 220, 240, 240);

        // Setup Path Box
        this.readyPathText = new Sprite(new Texture("LevelImages/readyForPathText.png"));
        this.readyPathText.setX(50);
        this.readyPathText.setY(50);

        this.pathSelectRects = new Rectangle[1];
        this.pathSelectRects[0] = new Rectangle();
        this.pathSelectRects[0].set(50, 50, 700, 95);
    }


    public void draw() {
        // Clear Background, and update camera
        Gdx.gl.glClearColor(135 / 255f, 206 / 255f, 235 / 255f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.camera.update();

        // Draw Level Info
        drawLevelInfo();

        // Draw Player Type Selection
        drawPlayerInfo();

        // Draw Enemy Type Selection
        drawEnemyInfo();

        // Draw Ready for Path Selection
        drawPathInfo();
    }

    private void drawPathInfo() {
        // Draw Selection Rectangles
        this.shapeRenderer.setProjectionMatrix(this.camera.combined);
        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for(int i = 0; i < 1; i++ ) {
            this.shapeRenderer.setColor(Color.GREEN);

            this.shapeRenderer.rect(this.pathSelectRects[i].getX(),
                    this.pathSelectRects[i].getY(),
                    this.pathSelectRects[i].getWidth(),
                    this.pathSelectRects[i].getHeight());
        }

        this.shapeRenderer.end();

        // Draw Images
        this.spriteBatch.setProjectionMatrix(this.camera.combined);
        this.spriteBatch.begin();

        this.readyPathText.draw(this.spriteBatch);

        this.spriteBatch.end();
    }

    private void drawEnemyInfo() {
        // Draw Selection Rectangles
        this.shapeRenderer.setProjectionMatrix(this.camera.combined);
        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for(int i = 0; i < 3; i++ ) {

            if( this.chosenEnemy == i ) {
                this.shapeRenderer.setColor(Color.GREEN);
            } else {
                this.shapeRenderer.setColor(Color.BLACK);
            }

            this.shapeRenderer.rect(this.enemySelectRects[i].getX(),
                    this.enemySelectRects[i].getY(),
                    this.enemySelectRects[i].getWidth(),
                    this.enemySelectRects[i].getHeight());
        }

        this.shapeRenderer.end();

        // Draw Images
        this.spriteBatch.setProjectionMatrix(this.camera.combined);
        this.spriteBatch.begin();

        this.chooseEnemyText.draw(this.spriteBatch);
        this.enemyStationary.draw(this.spriteBatch);
        this.enemyPuppet.draw(this.spriteBatch);
        this.enemyNN.draw(this.spriteBatch);

        this.spriteBatch.end();
    }

    private void drawPlayerInfo() {
        // Draw Selection Rectangles
        this.shapeRenderer.setProjectionMatrix(this.camera.combined);
        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for(int i = 0; i < 2; i++ ) {

            if( this.chosenPlayer == i ) {
                this.shapeRenderer.setColor(Color.GREEN);
            } else {
                this.shapeRenderer.setColor(Color.BLACK);
            }

            this.shapeRenderer.rect(this.playerSelectRects[i].getX(),
                    this.playerSelectRects[i].getY(),
                    this.playerSelectRects[i].getWidth(),
                    this.playerSelectRects[i].getHeight());
        }

        this.shapeRenderer.end();

        // Draw Images
        this.spriteBatch.setProjectionMatrix(this.camera.combined);
        this.spriteBatch.begin();

        this.choosePlayerText.draw(this.spriteBatch);
        this.playerPuppet.draw(this.spriteBatch);
        this.playerNN.draw(this.spriteBatch);

        this.spriteBatch.end();
    }

    private void drawLevelInfo() {
        // Draw Level Selection Indicators
        this.shapeRenderer.setProjectionMatrix(this.camera.combined);
        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for(int i = 0; i < 5; i++ ) {

            if( this.chosenLevel == i ) {
                this.shapeRenderer.setColor(Color.GREEN);
            } else {
                this.shapeRenderer.setColor(Color.BLACK);
            }

            this.shapeRenderer.rect(this.levelSelectRects[i].getX(),
                                   this.levelSelectRects[i].getY(),
                                   this.levelSelectRects[i].getWidth(),
                                   this.levelSelectRects[i].getHeight());
        }

        this.shapeRenderer.end();

        // Draw Level Thumb Images and Text
        this.spriteBatch.setProjectionMatrix(this.camera.combined);
        this.spriteBatch.begin();

        this.chooseLevelText.draw(this.spriteBatch);
        this.level1.draw(this.spriteBatch);
        this.level2.draw(this.spriteBatch);
        this.level3.draw(this.spriteBatch);
        this.level4.draw(this.spriteBatch);
        this.level5.draw(this.spriteBatch);

        this.spriteBatch.end();
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

    private int pointInRectangleChecker(Rectangle[] array, int x , int y) {
        for(int i = 0; i < array.length; i++) {
            if( x > array[i].getX()
                    && x < array[i].getX() + array[i].getWidth()
                    && y > array[i].getY()
                    && y < array[i].getY() + array[i].getHeight())
            {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        System.out.println(screenX + ", " + screenY);

        int x = screenX;
        int y = 1216 - screenY;
        int value = -1;

        // Check for Level Select Collision
        value = pointInRectangleChecker(this.levelSelectRects, x, y);

        if( value >= 0 ) {
            this.chosenLevel = value;
            return true;
        }

        // Check for Player Type Selection
        value = pointInRectangleChecker(this.playerSelectRects, x, y);

        if( value >= 0 ) {
            this.chosenPlayer = value;
            return true;
        }


        // Check for Enemy Type Selection
        value = pointInRectangleChecker(this.enemySelectRects, x, y);

        if( value >= 0 ) {
            this.chosenEnemy = value;
            return true;
        }


        // Check for Go To Path Selection button
        value = pointInRectangleChecker(this.pathSelectRects, x, y);

        if( value >= 0 ) {
            this.readyForPath = true;
            return true;
        }


        return false;
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
