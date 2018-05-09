
## 无人超市安卓客户端开发日志

-------------------------------------------------

2018.05.07

1. 导入zxing项目</br>
	1. import module 导入com.goole.zxing.client.android项目为zxing
	2. 修改zxing module的build.gradle:
		1. 修改 `apply plugin` 为 `com.android.library`
		2. 去掉`applicationId "com.google.zxing.client.android`, 否则出现错误:
		>	 Error:Library projects cannot set applicationId. applicationId is set to 'com.google.zxing.client.android' in default config.
		
		3. build zxing module, 期间会显示错误:
		> case expressions must be constant expressions 
		原因是作为Lib工程的zxing不能在代码里面使用switch R.id.*, 由于R资源不是常量。
		解决方法是将`switch`换成`if-else`, 直接把鼠标移动到swit处会有提示，可以自动转换。
		
	写安卓真的好气，不是自己的东西，什么都要学，还特么是英文。
	
2. Activity 生命周期
	```cmd
	05-07 20:56:19.592 22577-22577/com.gthncz.qrcodescannner I/QrCodeScannerActivity: lifecircle --> onCreate
	05-07 20:56:19.654 22577-22577/com.gthncz.qrcodescannner I/QrCodeScannerActivity: lifecircle --> onStart
	05-07 20:56:19.654 22577-22577/com.gthncz.qrcodescannner I/QrCodeScannerActivity: lifecircle --> onResume
	05-07 20:56:23.649 22577-22577/com.gthncz.qrcodescannner I/QrCodeScannerActivity: lifecircle --> onPause
	05-07 20:56:24.194 22577-22577/com.gthncz.qrcodescannner I/QrCodeScannerActivity: lifecircle --> onStop
	05-07 20:56:24.196 22577-22577/com.gthncz.qrcodescannner I/QrCodeScannerActivity: lifecircle --> onDestroy
	```

3. java.util.concurrent.CountDownLatch 同步工具类
	countDownLatch.await(); // 阻塞调用线程
	countDownLatch.countDown();// 当前线程工作完成，减少线程数目

