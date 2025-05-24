<div align=center>

# Klotski Puzzle

**CS109 Introduction to Computer Programming (JavaA) 25Spring Project Report**

</div>

## Overview

### Introduction

Klotski is a traditional Chinese puzzle game in which players need to move the "Cao Cao" block out of the board's exit. The game is typically played on a rectangular board with blocks of different sizes and shapes. In this project, we suppose the exit, the size of game panel and each block's size and number are fixed.

### Members

|Members|ID|Work|
|:---:|:---:|:---:|

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
│   │   ├── GameController.java 
│   │   ├── UserDataController.java
│   │   ├── UserManager.java
│   │   └── User.java
│   ├── model/
│   │   ├── BackgroundMusic.java
│   │   ├── Direction.java
│   │   ├── MapModel.java
│   │   └── SoundEffect.java
│   ├── view/
│   │   ├── game/
│   │   │    ├── BoxComponent.java
│   │   │    ├── GameFrame.java
│   │   │    ├── GamePanel.java
│   │   │    └── ListenerPanel.java
│   │   ├── login/
│   │   │    ├── LevelFrame.java
│   │   │    ├── LoginFrame.java
│   │   │    ├── RegisterFrame.java
│   │   │    ├── ResetFrame.java
│   │   │    └── StartGameFrame.java
│   │   └── FrameUtil.java
│   └── Main.java
├── .gitignore
├── CS109_Project_Klotski-Puzzle.iml
├── Klotski Puzzle.pdf
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
    - [x] Undo function
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
    - [ ] Save file error handling

- **Advanced features**
    - [ ] UI beautification (buttons, blocks, backgrounds)
    - [x] Multi-Level design
    - [ ] AI algorithm to solve automatically
    - [x] Animation effects
    - [x] Sound effects and background music
    - [x] Time attack mode
    - [ ] Props and obstacles
    - [ ] Online spectating


## Q & A

- When a player enters the game, their Username will be displayed as "Guest", but this does not cause any conflict. According to... If the user also names themselves as "Guest", since passwords need to be stored along with it, the game will recognize the user and all functions such as saving and loading the game can be used normally.

- If there is no username, we will default it to "admin" for you, and the password will also be "admin".

- Defensive programming: Even when the level resources are attacked or damaged, the default map can still be launched.
