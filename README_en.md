<div align=center>

# Klotski Puzzle

**🔖 中文 | English**

**CS109 Introduction to Computer Programming (JavaA) 25Spring Project Report**

</div>

## Overview

### Introduction

### Members

## Project Structure

```
Klotski Puzzle
├── .idea/
├── resources/
│   ├── levels/
│   │   ├── easy/
│   │   ├── hard/
│   │   └── medium/
│   └── images/
├── src/
│   ├── controller/
│   │   ├── GameController.java 
│   │   ├── UserDataController.java
│   │   └── UserManager.java
│   ├── model/
│   │   ├── Direction.java
│   │   ├── MapModel.java
│   │   └── User.java
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
├── LICENSE
└── README.md
```


## Features List

- **Login and game-start frames**
    - [x] User login
    - [x] Reset password for users
    - [x] Forbid guests to save game
    - [x] User data will be saved after exit
    - [x] Game-start frame
    - [x] Levels choose frame

- **Game frame and operations**
    - [x] Username display
    - [x] Best record (time, steps) display
    - [x] Exit (victory condition) display
    - [x] Step counting
    - [x] Time counting down
    - [x] Buttons control movement
    - [x] Keyboard control movement
    - [x] Movement record (in the output window, also saved)
    - [x] Undo function

- **Victory / Failure condition**
    - [x] Victory interface display (including time and steps)
    - [ ] Failure interface display (countdown runs out)

- **Save and load game**
    - [x] Save the previous game by button
    - [x] Load game available at game-start frame
    - [x] Timed auto-save (1 min)
    - [x] Auto-save on exit
    - [x] [add] Auto-save for each step
    - [ ] Save file error handling

- **Advanced features**
    - [ ] User Interface (UI) beautification
    - [x] Multi-Level design
    - [ ] AI algorithm to solve automatically
    - [ ] Animation effects
    - [ ] Sound effects and background music
    - [x] Time attack mode
    - [ ] Props and obstacles
    - [ ] Online spectating


## Q & A

- When a player enters the game, their Username will be displayed as "Guest", but this does not cause any conflict. According to... If the user also names themselves as "Guest", since passwords need to be stored along with it, the game will recognize the user and all functions such as saving and loading the game can be used normally.

- If there is no username, we will default it to "admin" for you, and the password will also be "admin".

- Defensive programming: Even when the level resources are attacked or damaged, the default map can still be launched.
