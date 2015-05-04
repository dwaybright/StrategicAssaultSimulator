package us.thirdmillenium.strategicassaultsimulator.simulation.environment;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import us.thirdmillenium.strategicassaultsimulator.simulation.agents.AgentModel;


public class GreenBullet {
    private Sprite bulletSprite;
    private Vector2 currentLocation;
    private Polygon bulletPath;
    private float degreeAngle;
    private AgentModel shotMe;


    public GreenBullet(Vector2 bulletLocation, float degreeAngle, AgentModel shotMe) {

        // Setup current bullet information
        this.currentLocation = bulletLocation;
        this.degreeAngle = degreeAngle;

        // Create, Rotate, and Translate the Bullet Path Bounding Box
        this.bulletPath = new Polygon(Params.BulletPathOriginVertices);
        this.bulletPath.setRotation(degreeAngle);
        this.bulletPath.translate(bulletLocation.x, bulletLocation.y);


        // Bullet Sprite
        Texture bulletTexture = new Texture("bullet1.png");
        this.bulletSprite = new Sprite(bulletTexture);
        this.bulletSprite.setCenter(bulletLocation.x, bulletLocation.y);
        this.bulletSprite.setRotation(degreeAngle);

        this.shotMe = shotMe;
    }

    /**
     * Given the time difference, updates location of Bullet in Environment.
     *
     * @param timeDelta
     */
    public void updateBullet(float timeDelta) {
        // Update Location
        Vector2 unitVec = new Vector2(0,1);
        unitVec.rotate(this.degreeAngle);

        unitVec.scl(Params.BulletVelocity);
        this.currentLocation.add(unitVec);

        // Update Bullet Path Location by Translating to new spot
        this.bulletPath.translate(unitVec.x, unitVec.y);

        // Update Sprite Location
        this.bulletSprite.setCenter(this.currentLocation.x, this.currentLocation.y);
    }

    /**
     * Draws the Bullet Sprite to a SpriteBatch.
     * @param sb
     */
    public void drawSprite(SpriteBatch sb) { this.bulletSprite.draw(sb); }

    /**
     * @return The Polygon describing the Bullet Path Bounding Box
     */
    public Polygon getBulletPath() { return this.bulletPath; }

    /**
     * @return The Vector2 describing Bullet location and rotation.
     */
    public Vector2 getBulletVector() { return this.currentLocation; }

    public boolean thisAgentShotMe(AgentModel someAgent) {
        return someAgent == this.shotMe;
    }
}
