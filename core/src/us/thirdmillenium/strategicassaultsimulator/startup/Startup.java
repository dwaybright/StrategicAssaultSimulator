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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Startup implements InputProcessor {
    // Sprite Drawing
    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;

    // Sprites for levels
    private Sprite level1;
    private Sprite level2;
    private Sprite level3;
    private Sprite level4;
    private Sprite level5;


    public Startup(AssetManager assman) {
        // Setup camera
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 1216);
        this.camera.update();

        // Setup SpriteBatch
        this.spriteBatch = new SpriteBatch();

        // Load Level Images
        this.level1 = new Sprite(new Texture("LevelImages/Small_Level1.png"));
        this.level2 = new Sprite(new Texture("LevelImages/Small_Level2.png"));
        this.level3 = new Sprite(new Texture("LevelImages/Small_Level3.png"));
        this.level4 = new Sprite(new Texture("LevelImages/Small_Level4.png"));
        this.level5 = new Sprite(new Texture("LevelImages/Small_Level5.png"));

        // Set Level Image Locations
        this.level1.setCenter(100, 1100);
        this.level2.setCenter(250, 1100);
        this.level3.setCenter(400, 1100);
        this.level4.setCenter(550, 1100);
        this.level5.setCenter(700, 1100);
    }


    public void draw() {
        // Clear Background, and update camera
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.camera.update();

        // Draw Level Thumb Images
        this.spriteBatch.setProjectionMatrix(this.camera.combined);
        this.spriteBatch.begin();

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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
