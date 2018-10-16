# LedTest
### Android Things Projcet




##   app mouble
 实现了使用SeekBar UI控件来控制电路中LED等闪烁频率的功能，主要使用了GPIOAPI进行如下处理：
  使用PeripheralManager来打开一个连接到GPIO端口的LED连接；
  使用DIRECTION_OUT_INITIALLY_LOW配置端口；
  给setValue()方法传递getValue()相反的值来改变LED的状态；
  使用Handler来执行触发GPIO的事件，在一段时间后再次触发；
  当应用程序不在需要GPIO连接的时候，关闭Gpio资源；

 ![Image text][demo1-gif]


##   led button mouble
    更改以上的例子进行按钮操作，请将树莓派连接鼠标与显示器(或者触摸屏）
    点击开灯或关灯按钮进行开关操作。

![Image text][demo2-png]

# 注意事项
    使用此deomo请将led正极接入BCM6引脚




##   camera
     这个是使用摄像头进行拍照并在屏幕上显示的例子
### 材料
-  使用10k电阻
-     导线
-    按钮开关

 ![拍出来的图][demo3-png1]

接线连接图

 ![拍出来的图][demo3-png2]



 [demo1-gif]: img/demo1.gif
 [demo2-png]: /img/swbutton_led.png
  [demo3-png1]: /img/device-2018-10-16-164509.png
  [demo3-png2]: /img/demo3_link.png