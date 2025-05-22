<div align=center>

# Klotski Puzzle 华容道

**📖 中文 | [English (professional)](https://github.com/lxriscute0501/SUSTech_CS109_Project_Klotski-Puzzle/blob/main/README_en.md)**

**CS109 计算机程序设计基础(JavaA) 25春 项目报告**

**独立完成！**

</div>

## 概览

### 项目介绍

华容道是一款传统的中国益智游戏，在游戏中，玩家需要将“曹操”这个方块从棋盘的出口处移出。游戏通常在带有不同尺寸和形状方块的矩形棋盘上进行，在此项目中，我们固定了出口、棋盘的尺寸以及各个方块的尺寸与数量。

更多信息请参照项目文档。

### 小组成员
|成员|学号|分工|
|:---:|:---:|:---:|


## 项目结构

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
│   │   ├── GameController.java         # 
│   │   ├── UserDataController.java     # 
│   │   └── UserManager.java            # 
│   ├── model/
│   │   ├── BackgroundMusic.java        # 背景音乐（全程运行）
│   │   ├── Direction.java              #
│   │   ├── MapModel.java               #
│   │   ├── SoundEffect.java            # 移动音效与胜利鼓掌音效
│   │   └── User.java                   #
│   ├── view/
│   │   ├── game/
│   │   │    ├── BoxComponent.java      #
│   │   │    ├── GameFrame.java         # 选择关卡后
│   │   │    ├── GamePanel.java         #
│   │   │    └── ListenerPanel.java     #
│   │   ├── login/
│   │   │    ├── LevelFrame.java        # 开始新游戏后选择关卡，各难度3张图随机选择
│   │   │    ├── LoginFrame.java        # 设置四个按钮功能，验证并存储用户信息
│   │   │    ├── RegisterFrame.java     # 新用户注册
│   │   │    ├── ResetFrame.java        # 老用户修改密码
│   │   │    └── StartGameFrame.java    # 登录后游戏开始页面，同时在此加载游戏
│   │   └── FrameUtil.java              # 
│   └── Main.java
├── .gitignore
├── CS109_Project_Klotski-Puzzle.iml
├── LICENSE
└── README.md
```

## 功能列表

- **登录与开始游戏页面**
    - [x] 老用户登录
    - [x] 重置密码
    - [x] 新用户注册
    - [x] 游客模式
    - [x] 游戏开始页面
    - [x] [高级] 难度选择页面

- **游戏页面与操作**
    - [x] 用户名显示
    - [x] 历史最佳用时显示
    - [x] 历史最佳步数显示
    - [x] 方块图片与出口绘制
    - [x] 当前步数计数
    - [x] 按钮控制移动
    - [x] 键盘控制移动
    - [x] 重新开始操作
    - [x] 撤回操作
    - [x] [高级] 倒计时显示
    - [x] [高级] 方块移动时的流畅动画

- **胜利/失败条件**
    - [x] 胜利页面显示（包含所用时间、步数）
    - [x] 倒计时用尽，失败页面显示

- **保存与加载游戏**
    - [x] 手动保存游戏（各用户最近一次）
    - [x] 手动加载游戏（游戏开始页面）
    - [x] 定时自动保存（1分钟）
    - [x] 退出时自动保存
    - [ ] 不加载被损坏文件

- **高级功能**
    - [x] 美化UI（按键、方块、背景图）
    - [x] 多等级关卡设计
    - [ ] AI自动解决
    - [x] 方块移动的流畅动画效果
    - [x] 背景音乐
    - [x] 倒计时功能
    - [ ] 添加道具与障碍物
    - [ ] 允许多个用户同时登录，可观赛等


## 问题与解答

- 游客进入游戏后会显示其Username为Guest，但这并不会产生冲突，根据， 如果用户也起名为Guest，因为要随之存储密码，游戏会识别出用户，保存及加载游戏等功能均可正常使用。
- 如果没有用户名，我们会为您默认为admin，密码也为admin
- 
