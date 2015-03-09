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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import us.thirdmillenium.strategicassaultsimulator.brains.Brain;
import us.thirdmillenium.strategicassaultsimulator.brains.PuppetBrain;


public class PuppetAgent extends AgentModel {
    // Game World
    TiledMapTileLayer CollisionLayer;
    Sprite Sprite;
    Texture Alive;
    Texture Dead;


    // Agent Physical Attributes
    private int Health;
    private float MovementSpeed;
    private float Eyesight;
    private float Hearing;
    private float Angle;


    // Agent Equipment
    //private WeaponModel MyWeapon;
    //private ArmorModel MyArmor;


    // Agent Mental Model
    private Brain Control;


    public PuppetAgent(TiledMapTileLayer collisionLayer)
    {
        // Setup game world parameters
        this.CollisionLayer = collisionLayer;
        this.Alive = new Texture("goodGuyDot.png");
        this.Dead = new Texture("deadDot.png");
        this.Sprite = new Sprite(Alive);

        // Set Basic Values
        this.Health = 50;
        this.MovementSpeed = 5;
        this.Eyesight = 10;
        this.Hearing = 20;
        this.Angle = 0;

        // Set Control
        this.Control = new PuppetBrain();
    }

    @Override
    public void agentHit() {

    }

    @Override
    public void updateAgentState() {

    }
}
