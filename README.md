# Strategic Assault Simulator
A 2d strategic room clearing game.  The objective is to provide the overall attack strategy for your assault team.  From there, your team must rely on their skill level and your guidance to carry out a simulated attack.  Your assault team and the occupying enemy force will both utilize genetic algorithms to tweak artificial neural networks which will be used to control the Agents during the simulation.

Graphics rely on ![LibGDX] (https://github.com/libgdx/libgdx).

This branch is the build used during App Demo day.  Currently Neuroph libraries are causing issues with internal logging libraries, so Nueral Net Agents are in trouble.

Visit the ![Desktop Trainer](https://github.com/dwaybright/SAS-DesktopTrainer) to see current method for neural network agent training!


Example of an Agent created with [Gimp](http://www.gimp.org/)

![Agent](android/assets/goodGuyDotArrow.png)

Example of a Level created with [Tiled](http://www.mapeditor.org/)

![Level 1](android/assets/MyCrappyMap.png)

Example of Interface Component in Action

![IC](android/assets/InterfaceComponent.png)

# Installation Instructions 
(![Android Studio](http://developer.android.com/sdk/index.html) 1.0.2 Build 135.1653844)

(1) To install in Android Studio, begin by cloning the repository to your computer.  

(2) From the "Welcome to Android Studio" splash screen, select Import Non-Android Studio project

(3) Browse to cloned repository directory on your computer and select the build.gradle file in the root directory.  

(4) This should build the project.  Once complete, run in emulator or attached device!

# Requirements
An Android 4.1+ device with a screen resolution of 1280x800.  The more Process/Ram available, the better the performance.
