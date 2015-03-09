# Strategic Assault Simulator
An Android simulation game that relies on Neural Network controlled Agents, with extensive use of LibGDX (http://libgdx.badlogicgames.com/).

The interface component branch is a code turn-in for class.  

This interface component is a LibGDX window showing a level screen and a dot representing a character in the game.  In addition, a mapping of circles are overlayed which show tiles which a character could move.  Upon tapping the screen, a black line indicates the path returned by A* for how the character would move to where you tapped the screen.

Example of an Agent created with Gimp (http://www.gimp.org/)

![Agent](android/assets/goodGuyDotArrow.png)

Example of a Level created with Tiled (http://www.mapeditor.org/)

![Level 1](android/assets/MyCrappyMap.png)

Example of Interface Component in Action

![IC](android/assets/InterfaceComponent.png)

# Installation Instructions 
(Android Studio 1.0.2 Build 135.1653844)

(1) To install in Android Studio, begin by cloning the repository to your computer.  

(2) From the "Welcome to Android Studio" splash screen, select Import Non-Android Studio project

(3) Browse to cloned repository directory on your computer and select the build.gradle file in the root directory.  

(4) This should build the project.  Once complete, run in emulator or attached device!
