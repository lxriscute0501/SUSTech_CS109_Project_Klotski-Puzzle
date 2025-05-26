<div align=center>

# Klotski Puzzle 华容道

**📖 Language: 中文 | [English](https://github.com/lxriscute0501/SUSTech_CS109_Project_Klotski-Puzzle/blob/main/README_en.md)**

**CS109 计算机程序设计基础(JavaA) 25春 项目报告**

</div>

## 概览

### 项目介绍

华容道是一款传统的中国益智游戏，在游戏中，玩家需要将“曹操”这个方块从棋盘的出口处移出。游戏通常在带有不同尺寸和形状方块的矩形棋盘上进行，在此项目中，我们固定了出口、棋盘的尺寸以及各个方块的尺寸与数量。

更多信息请参照[项目文档](https://github.com/lxriscute0501/SUSTech_CS109_Project_Klotski-Puzzle/blob/main/Klotski%20Puzzle.pdf)。

### 小组成员


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
│   │   ├── GameController.java         # 游戏基本逻辑，包含检验移动合法性、更新方块位置、检验胜利条件、工具使用、计时等
│   │   ├── UserDataController.java     # 存储与加载游戏信息
│   │   ├── UserManager.java            # 登录时验证与加载用户数据
│   │   └── User.java                   # 存储用户信息，包含密码、最佳时间与步数等
│   ├── model/
│   │   ├── BackgroundMusic.java        # 背景音乐（全程运行）
│   │   ├── Direction.java              # 枚举类，上下左右四个方向
│   │   ├── MapModel.java               # 设置与复制地图，存储地图信息
│   │   └── SoundEffect.java            # 移动音效与胜利鼓掌音效
│   ├── view/
│   │   ├── game/
│   │   │    ├── BoxComponent.java      # 设置每个方块的位置与图案
│   │   │    ├── GameFrame.java         # 设置游戏界面的各个标签与按键
│   │   │    ├── GamePanel.java         # 对方块选择与移动进行响应，设置工具按键，更新步数与时间标签，步数计数也在此
│   │   │    └── ListenerPanel.java     # 实现对键盘与鼠标的响应
│   │   ├── login/
│   │   │    ├── LevelFrame.java        # 开始新游戏后选择关卡，各难度3张图随机选择
│   │   │    ├── LoginFrame.java        # 设置四个按钮功能，验证并存储用户信息
│   │   │    ├── RegisterFrame.java     # 新用户注册
│   │   │    ├── ResetFrame.java        # 老用户修改密码
│   │   │    └── StartGameFrame.java    # 登录后游戏开始页面，同时在此加载游戏
│   │   └── FrameUtil.java              # 创建标签、密码、按键的基本要素
│   └── Main.java
├── .gitignore
├── CS109_Project_Klotski-Puzzle.iml
├── Klotski Puzzle.pdf                  # 项目文档
├── LICENSE
├── README.md
└── README_en.md
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
    - [x] [高级] 撤回操作
    - [x] [高级] 倒计时显示
    - [x] [高级] 方块移动时的流畅动画
    - [x] [高级] 锤子按键，可移除士兵 
    - [x] [高级] 障碍按键，可设置禁区

- **胜利/失败条件**
    - [x] 胜利页面显示（包含所用时间、步数）
    - [x] 倒计时用尽，失败页面显示

- **保存与加载游戏**
    - [x] 手动保存游戏（各用户最近一次）
    - [x] 手动加载游戏（游戏开始页面）
    - [x] 定时自动保存（1分钟）
    - [x] 退出时自动保存
    - [x] 不加载被损坏文件

- **高级功能**
    - [x] 美化UI（按键、方块、背景）
    - [x] 多等级关卡设计
    - [ ] AI自动求解
    - [x] 方块移动的流畅动画效果
    - [x] 背景音乐
    - [x] 倒计时功能
    - [x] 添加道具与障碍物
    - [ ] 允许多个用户同时登录，可观赛等


## 问题与解答

- *Q: 游客进入游戏后用户名会显示为`Guest`，是否会与用户名也为`Guest`的用户产生冲突？*
- ✅ 不会。如果用户也起名为`Guest`，其信息存储在`User`中，其中的`isGuest`值为`false`，且`user.config`中也会存储其用户名及密码。因此游戏会识别出用户，保存及加载游戏等功能均可正常使用。

- 💡 `saveGame`方法中，保存的路径为`data/username/data.txt`，其格式为：
```
[level]
[steps]
[used time]
[best steps]
[best time]
[map data (4*5)]
```

- 💡 使用锤子、障碍工具后的`Undo`与`Restart`功能：
    * `Restart`后，所有被删除的士兵与设置的禁区都会被恢复；
    * 使用工具的操作是不可逆的，因此若`Undo`操作的士兵已被删除，`Undo`不会执行，并有错误提示；
    * 同理，若原位置已被设为禁区，`Undo`也不会执行，并有错误提示。



