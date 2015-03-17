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


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * The AgentModel class is extended by all actual Agents to be made.
 */
public abstract class AgentModel {


    /**
     * Tells the agent it was hit.
     */
    public abstract void agentHit();


    /**
     * Has the Agent perform its updates.
     */
    public abstract void updateAgentState();


    /**
     * Draws the Agent on the canvas.
     *
     * @param sb
     */
    public abstract void drawAgent(SpriteBatch sb);


    /**
     * Allows the Agent to draw lines if it wants.
     * Mostly a DEBUG feature?
     *
     * @param sr
     */
    public abstract void drawLines(ShapeRenderer sr);
}
