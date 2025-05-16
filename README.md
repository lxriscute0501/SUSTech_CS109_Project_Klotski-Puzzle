<div align=center>

# Klotski Puzzle 华容道

**🔖 中文 | English**

**CS109 计算机程序设计基础(JavaA) 25春 项目报告**

</div>

## 概览

### 项目介绍

### 小组成员


## 项目结构

### 结构图

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

### 各程序具体功能


## 功能列表

- **登录与开始游戏页面**
    - [x] 老用户登录
    - [x] 重置用户名与密码
    - [x] 禁止游客保存游戏
    - [x] 退出后用户数据保存，下次仍能登录
    - [x] 游戏开始页面
    - [x] 难度选择页面

- **游戏页面与操作**
    - [x] 用户名显示
    - [x] 历史最佳（时间、步数）显示
    - [x] 出口（胜利条件）显示
    - [x] 当前步数计数
    - [x] 倒计时
    - [x] 按钮控制移动
    - [x] 键盘控制移动
    - [x] 移动状态记录（在输出窗口）

- **胜利/失败条件**
    - [x] 胜利页面显示（包含所用时间、步数）
    - [x] 倒计时用尽，失败页面显示

- **保存与加载游戏**
    - [x] 保存最近一次游戏
    - [x] 在游戏开始页面选择加载游戏
    - [x] 定时自动保存
    - [x] 退出时自动保存
    - [x] 每步移动自动保存
    - [ ] 不加载被损坏文件

### 高级功能
- [ ] 美化图形用户界面GUI
- [x] 多等级关卡设计
- [ ] AI自动解决问题
- [ ] 方块移动的流畅动画效果
- [ ] 背景音乐
- [x] 倒计时功能
    - [x] 页面倒计时
    - [x] 记录用时
    - [x] 存储用户的最佳用时
- [ ] 添加道具与障碍物
- [ ] 允许多个用户同时登录，可观赛等


## 问题与解答

- 游客进入游戏后会显示其Username为Guest，但这并不会产生冲突，根据， 如果用户也起名为Guest，因为要随之存储密码，游戏会识别出用户，保存及加载游戏等功能均可正常使用。
- 如果没有用户名，我们会为您默认为admin，密码也为admin
