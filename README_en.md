<div align=center>

# Klotski Puzzle

**CS109 Introduction to Computer Programming (JavaA) 25Spring Project Report**

</div>

## Overview

### Introduction

Klotski is a traditional Chinese puzzle game in which players need to move the "Cao Cao" block out of the board's exit. The game is typically played on a rectangular board with blocks of different sizes and shapes. In this project, we suppose the exit, the size of game panel and each block's size and number are fixed.

### Members

|           Members           |Login|Game Logic|GUI|Time|Levels|Tools|Save & Load|Sound|
|:---------------------------:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
|  🇨🇳 12312110 Li Xuanran   ||✔️|||✔️|✔️|✔️||
|     🇮🇩 12412262 Tisha     |✔️||✔️|✔️||||✔️|

## Project Structure

```
Klotski Puzzle
├── .idea/
├── resources/
│   ├── levels/
│   │   ├── easy/
│   │   ├── hard/
│   │   └── medium/
│   ├── images/
│   │   ├── backgrounds/
│   │   ├── blocks/
│   │   └── buttons/
│   └── sound/
├── src/
│   ├── controller/
│   │   ├── GameController.java         # Basic game logic, including checking the legality of movements, updating the position of the blocks, checking victory conditions, using tools, and timing
│   │   ├── User.java                   # Stores user information, including password, best time, and steps, etc.
│   │   ├── UserDataController.java     # Stores and loads game information
│   │   └── UserManager.java            # Verifies and loads user data during login
│   ├── model/
│   │   ├── BackgroundMusic.java        # Background Music (running throughout)
│   │   ├── Direction.java              # Enum class, four directions: up, down, left, right
│   │   ├── FireworksEffect.java        # Set fireworks when win the game
│   │   ├── MapModel.java               # Setting and copying of the map, storing map information
│   │   └── SoundEffect.java            # Movement sound effect and victory clapping sound effect 
│   ├── view/
│   │   ├── game/
│   │   │    ├── BackgroundPanel.java   # Add background picture
│   │   │    ├── BoxComponent.java      # Set the position and pattern of each block
│   │   │    ├── GameFrame.java         # Configure various labels and buttons of the game interface
│   │   │    ├── GamePanel.java         # Respond to block selection and movement, sets tool buttons, updates the step count and time labels, and also counts the steps here
│   │   │    ├── ListenerPanel.java     # Implement responses to keyboard and mouse inputs
│   │   │    └── ObstacleComponent.java # Set obstacle blocks
│   │   ├── login/
│   │   │    ├── LevelFrame.java        # Select level after starting a new game, randomly choose 3 images from each difficulty level
│   │   │    ├── LoginFrame.java        # Set the functions of four buttons, verify and store user information
│   │   │    ├── RegisterFrame.java     # New user registration
│   │   │    ├── ResetFrame.java        # Old user password modification
│   │   │    └── StartGameFrame.java    # Login page for starting the game, and load the game here simultaneously
│   │   └── FrameUtil.java              # Create basic elements for labels, passwords, and buttons 
│   └── Main.java
├── .gitignore
├── CS109_Project_Klotski-Puzzle.iml
├── Klotski Puzzle.pdf                  # Project Document 
├── LICENSE
├── README.md
└── README_en.md
```


## Features List

- **Login and game-start frames**
    - [x] Previous users login
    - [x] Reset passwords for users
    - [x] New users registeration
    - [x] Guest mode
    - [x] Game-start frame
    - [x] [advance] Level-choose frame

- **Game frame and operations**
    - [x] Username display
    - [x] Best (Least) time display
    - [x] Best (Least) steps display
    - [x] Block pictures and exit drawing
    - [x] Step counting
    - [x] Buttons control movement
    - [x] Keyboard control movement
    - [x] Restart function
    - [x] [advance] Undo function
    - [x] [advance] Time counting down
    - [x] [advance] Animation effects when moving

- **Victory / Failure condition**
    - [x] Victory interface display (including time and steps)
    - [x] Failure interface display (countdown runs out)

- **Save and load game**
    - [x] Save the latest game by button
    - [x] Load game (at game-start frame)
    - [x] Timed auto-save (1 min)
    - [x] Auto-save when exiting
    - [x] Save file error handling
    - [ ] [advance] Detect and maintain the legitimacy of the save data

- **Advanced features**
    - [x] UI beautification (buttons, blocks, backgrounds)
    - [x] Multi-Level design
    - [ ] AI algorithm to solve automatically
    - [x] Animation effects
    - [x] Sound effects and background music
    - [x] Time attack mode
    - [x] Props and obstacles
    - [ ] Online spectating


## Q & A

- *Q: After a guest enters the game, the username will be displayed as `Guest`. Will this cause any conflict with users whose usernames are also `Guest`?*
- ✅ No. If the user is also named `Guest`, their information will be stored in the `User` section, where the `isGuest` value is `false`, and their username and password will also be stored in `user.config`. Therefore, the game will be able to recognize the user, and all functions such as saving and loading the game can operate normally.

- *Q: When choosing levels, what if all the map resources have been destroyed?*
- ✅ At each level, one map resource is randomly selected from three options. If the map we choose do not have the size of $4*5$, we will load the default map, and users can play the game normally.

- 💡 In the `saveGame` method, the saved path is `data/username/data.txt`, and its format is: 
```
[level]
[steps]
[used time]
[best steps]
[best time]
[map data (4*5)]
```

- 💡 In the `loadGame` method, the errors we can detect are:
    * `level` string is not `Easy`, `Medium` or `Hard`;
    * Time has been used up;
    * The size of map data is not $4*5$;
    * Missing or extra lines.

- 💡 The `Undo` and `Restart` functions after using the hammer and obstacle tools:
    * After performing `Restart`, all the deleted soldiers and the designated restricted areas will be restored;
    * The operations using the tools are irreversible. Therefore, if the soldiers deleted by the `Undo` operation have already been removed, the `Undo` operation will not be executed and an error message will be displayed;
    * Similarly, if the original position has been set as a restricted area, the `Undo` operation will not be executed either, and an error message will be displayed.
