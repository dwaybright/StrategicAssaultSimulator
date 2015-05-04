package us.thirdmillenium.strategicassaultsimulator.startup;

import android.content.res.AssetManager;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by danielwaybright on 5/4/15.
 */
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
    }


    public void draw() {
        
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
