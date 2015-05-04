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

package us.thirdmillenium.strategicassaultsimulator.simulation.graphics;

import com.badlogic.gdx.math.Vector2;


public class Line {
    public Vector2 start;
    public Vector2 end;

    public Line(Vector2 a, Vector2 b) {
        this.start = a;
        this.end = b;
    }
}
