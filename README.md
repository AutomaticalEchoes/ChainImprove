# 锁链加强(ChainImprove)
[![Build Status](https://img.shields.io/badge/MinecraftForge-1.20.x-brightgreen)](https://github.com/MinecraftForge/MinecraftForge?branch=1.20.x)

_阅前提示：本人喜欢用<sub title="如果影响你观看就先给你道个歉啦！>-<" >**`注`**</sub>来添加注释。_
## 介绍
 本模组为锁链增加了一些功能：锁链方块可以攀爬，非潜行状态下会自动攀爬；加强锁链的放置效果，类比脚手架；锁链可以连接船，类似基岩版使用拴绳牵引船只；
## 使用
### 锁链放置
- ***潜行状态下***，放置锁链会触发特殊放置模式，手持锁链并且使用对象为锁链方块时，会在其***方向***上延长放置锁链方块，类比脚手架的放置。  
- 具体使用效果：假设有一个摆放轴向为Y轴的锁链方块，手持锁链并瞄准锁链的上半部分使用，会在该方块的高度向上（Y+）递增寻找最近的空气方块放置锁链方块；
   瞄准方块下半部分则为向下；通过这种方式新放置的锁链方块的轴向与使用目标一致；  
- 非潜行状态下与一般方块放置无异；
### 锁链攀爬
- 放置的锁链方块无论轴向如何，都可以攀爬；
- ***悬空状态下接触锁链方块***或者***脚踩锁链***方块时，与方块锁链轴向不同的速度会被修正（减少）。  
  例如：实体向XZ正向移动，踩上一个轴向为Z轴的锁链时，它在X轴上的分速度会变为原来的十分之一，运动方向变为偏向Z轴；
- 放置的锁链方块为垂直方向（轴向为Y）时，接触会自动攀爬，攀爬的方向以玩家初动方向为准。  
  例如：下坠状态中途碰到垂直锁链，此时会延锁链向下滑行。
- 潜行状态可以停止自动攀爬，静止于锁链上，但是由于取消潜行后玩家受重力作用，初动向下，会向下滑行；
### 牵引船只
- 类似基岩版使用拴绳迁移船只，改为使用锁链牵引；
- 锁链物品只能牵引船只，不能牵引其他生物，不是拴绳的平替；
- 区别于基岩版拴绳，锁链使用头尾接的方式连接各船只，以满足一次牵引复数船只的需求；
  即：船B链接船A，船A再链接玩家，玩家一次牵引A，以此再牵引B。（A和B一般不会同时链接玩家。
- 因为是铁链，所以链接断开的距离判定很长（24+，受速度补正，速度越快这个距离越长，最短24格）。
___
**非专业moder,望大家多多海涵.  
如果你发现了什么问题或者有什么建议,可以发邮件给我.~~回不回复随缘~~  
email:AutomaticalEchoes@outlook.com.**