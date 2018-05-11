
## 无人超市安卓客户端开发日志

-------------------------------------------------

2018.05.10 11:20

1. 根据扫描的结果，分派解决方法：
	1. market://action/token 格式在APP内部处理
	2. 其它类型弹窗选择复制到剪贴板

2. 完成了会员授权功能。
	1. 用户扫描二维码后提交服务器检查授权请求有效性
	2. 授权请求有效则显示确认授权界面
	3. 用户确认授权，提交服务器，结束授权界面。

-------------------------------------------------

2018.05.09 16:40

1. 完成了`qrcodescannner`模块。
	1. 新建Module qrcodescannner。
	2. 把zxing project的核心复制到qrcodescanner。
	3. 自定义`ScannnerView`,用于显示扫描框。
	4. QrScannerActivity的内部流程：
		1. 初始化UI控件， 获取外部传入的解码信息
		2. 初始化Surface, 初始化相机
		3. 开始预览，开启解码线程
		4. 开始业务逻辑，将相机预览帧回调数据送给解码Handler处理
		5. 解码Handler将解码后数据传给UIHandler展示
		6. 关闭相机，关闭解码线程
		7. 将解码数据传回调用Activity
	5. 使用`SurfaceView`显示相机预览图像。
	6. 相机有回调`Camera.PreviewCallback`, 可以获取图像数据帧(byte[] data),
		将相机预览数据传递给解码Handler处理。
	
-------------------------------------------------

2018.05.07

1. 导入zxing android项目做库工程(application module作 library module)</br>
	1. import module 导入com.goole.zxing.client.android项目为zxing
	2. 修改zxing module的build.gradle:
		1. 修改 `apply plugin` 为 `com.android.library`
		2. 去掉`applicationId "com.google.zxing.client.android`, 否则出现错误:
		>	 Error:Library projects cannot set applicationId. applicationId is set to 'com.google.zxing.client.android' in default config.
		
		3. build zxing module, 期间会显示错误:
		> case expressions must be constant expressions 
		原因是作为Lib工程的zxing不能在代码里面使用switch R.id.*, 由于R资源不是常量。
		解决方法是将`switch`换成`if-else`, 直接把鼠标移动到swit处会有提示，可以自动转换。
		
		4. 修改app module gradle, 添加zxing library, 有两种方法:
			1. 在AS中, 左侧导航栏(Android) app -(右键)-> Open Module Setting -> Dependencies -> + Module dependency -> 选择library module.
			2. 在app module gradle中, 添加语句:
				```gradle
				dependencies{
					...
					implementation project(':zxing')
				}
				```
	3. 后面查看了zxing android项目后, 发现项目过于臃肿,不适合我的项目,故自己写了一个扫描二维码的module.
	
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

3. `java.util.concurrent.CountDownLatch` 同步工具类
	countDownLatch.await(); // 阻塞调用线程
	countDownLatch.countDown();// 当前线程工作完成，减少线程数目
	
4. 新颖控件总结, 使用的依赖是:
	```gradle
	implementation 'com.android.support:design:27.1.1'
	```
	
	1. `android.support.design.widget.TextInputLayout` 包裹的EditText会比较漂亮。
	
	2. `android.support.v4.widget.DrawerLayout` 可以轻松实现抽屉效果，
		默认嵌套的第一个布局是主界面布局，第2，3个布局**必须**指定`layout_gravity`来区分是左抽屉还是右。
		
	3. `android.support.design.widget.NavigationView` 可以结合`DrawerLayout`快速实现侧面的导航栏。
		1. `app:headerLayout` 属性可以指定头部的布局， `app`是xml命名空间: ``xmlns:app="http://schemas.android.com/apk/res-auto"``。
		2. `app:menu="@menu/menu_header"`属性可以用于生成菜单项，通过设置`setNavigationItemSelectedListener`监听器监听事件。
		3. ``mNavigationView.setItemIconTintList(null);`` 让图片以原来的颜色显示, 默认显示相同颜色。
	
	4. `android.support.design.widget.AppBarLayout` 包裹 `Toolbar`可以实现滑动隐藏。
	
5. `ButterKnife` 注解框架的使用
	1. app module 添加依赖: 
		```java
		implementation 'com.jakewharton:butterknife:8.8.1'
		annotationProcessor 'com.jakewharton:butterknife-compiler:8.8.1'
		```
	2. 注解UI控件方法: 
		```java
		@BindView(R.id.toolBar_login) protected Toolbar toolbar;
		```
		经过测试， `SurfaceView`控件不能使用ButterKnife被注解。
	3. 注解点击事件:
		```java
		@OnClick({R.id.button_client_main_scanecode})
		protected void scanQrCodeAction(){
			// TODO 扫描二维码
		}
		```
	ButterKnife 还有其它许多的用法。
	**注意**, 库工程不能使用ButterKnife注解框架，否则出现R资源非常量错误。库工程还是老实使用`findViewById`...

6. `GreenDAO` 数据库框架的使用
	1. 添加依赖:
		```java
		compile 'net.zetetic:android-database-sqlcipher:3.5.9@aar'
		implementation 'org.greenrobot:greendao:3.2.2'
		```
		添加 net.zetetic的sqlcipher是由于greendao用到了。
	2. 在app gradle中编写GreenDAO的配置:
		```gradle
		greendao {
			schemaVersion 1 // 指定数据库schema版本号，迁移等操作会用到
			daoPackage 'com.gthncz.mymarketclient.greendao' // 通过gradle插件生成的数据库相关文件的包名，默认为你的entity所在的包名
			targetGenDir 'src/main/java' // 自定义生成数据库文件的目录，可以将生成的文件放到我们的java目录中，而不是build中，这样就不用额外的设置资源目录了
		}
		```
		`schemaVersion` 指定数据库schema版本号，迁移等操作会用到
		`daoPackage` 通过gradle插件生成的数据库相关文件的包名，默认为你的entity所在的包名
		`targetGenDir` 自定义生成数据库文件的目录，可以将生成的文件放到我们的java目录中，而不是build中，这样就不用额外的设置资源目录了
	3. 在daoPackage指定的包下，编写数据表实体类:
		eg: 用户登记表
		```java
		package com.gthncz.mymarketclient.greendao;

		import org.greenrobot.greendao.annotation.Entity;
		import org.greenrobot.greendao.annotation.Generated;
		import org.greenrobot.greendao.annotation.Id;
		import org.greenrobot.greendao.annotation.NotNull;
		import org.greenrobot.greendao.annotation.Property;
		import org.greenrobot.greendao.annotation.Unique;
		
		@Entity
		public class UserLevel {
			@Property(nameInDb = "id")
			@Id(autoincrement = true)
			@Unique
			private long _id;
			@NotNull
			private String name;
			@NotNull
			private int count;
			@NotNull
			private int status;

			@Generated(hash = 826131989)
			public UserLevel() {
			}

			@Generated(hash = 1995930281)
			public UserLevel(long _id, @NotNull String name, int count, int status) {
				this._id = _id;
				this.name = name;
				this.count = count;
				this.status = status;
			}
		}
		```
		注：其中的hash值是greenDAO自动生成的，一开始编写时不用写。
	4. 运行`gradle task`就可以得到 `DaoMaster`, `DaoSession` 和 `UserLevelDao`等数据库类。
		**注：**一定要有实体类在`daoPackage`中才能生成。
		DaoMaster 是管理应用中数据库的总管理类，DaoSession 则是管理与数据库的连接，(Entity)Dao 则包含各自实体表的属性.
	5. 获取`DaoMaster` 和 `DaoSession`的实例:
		```java
		/*获取DaoMaster实例*/
		DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(weakContext.get(), Params.DB_NAME, null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
		```
		```java 
		/*获取DaoSession实例*/
		DaoSession daoSession = daoMaster.newSession();
		```
	6. 插入数据
		eg: 插入用户等级:
		```java
		UserLevel userLevel11 = new UserLevel(1, "Level 1", 1, 1);
		getDaoSession().getUserLevelDao().insert(userLevel11); // getDaoSession()是自定义函数
		```
		也还有其它几种插入函数.
	7. 查询数据:
		eg: 查询用户
		```java
		List<User> users = getDaoSession()
								.getUserDao()
								.queryBuilder()
								.where(UserDao.Properties.Id.eq(user_id))
								.limit(1)
								.list();
		```
		查询有许多高级查询, 具体查看文档.
	8. 更新数据, 具体查看文档.
	9. 删除数据, 具体查看文档.

7. `volley` 异步网络框架的使用.
	1. 添加依赖:
		```gradle
		compile 'com.android.volley:volley:1.1.0'
		```
	2. 使用:
		1. 先定义一个 请求队列(`RequestQueue`):
			```java
			RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
			```
		2. 定义一个 请求(`Request`), 可以是 `StringRequest`, `JsonObjectRequest`等.
			```java
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Params.URL_USER_LOGIN, params, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {// UI 线程中执行
					// TODO 处理请求成功逻辑
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO 处理请求失败逻辑
				}
			});
			mQueue.add(jsonObjectRequest); // !important
			```
		3. 添加Request 到 RequestQueue.
		4. 启动RequestQueue轮询Request.
			```java
			mQueue.start();
			```
	使用Volley进行网络数据请求还是很方便的说.
	