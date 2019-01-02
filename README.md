# bsdiff

android 下编译的 bsdiff_4.3

bsdiff_4.3 引用了 bzip2_1.0.6

## 功能
用于flutter的 增量更新

android 增量更新知己完善

## 为什么要做下面的 工程引入
    因为工程结构被我改了，

    原来是：
    android
    ios
    lib
    pubspec.yaml

    被我改为了
    android
        src
            main
            flutter
                pubspec.yaml

    所以要做一些改动


## 工程引入
- pub 加入
    ```pub
    dev_dependencies:
      tzj_bsdiff:
        git:
          url: git://github.com/tzjandroid/tzj_bsdiff.git
          path: tzj_bsdiff/src/flutter
    ```
- android 工程下的 settings.gradle 中改为如下
    ```Gradle
    def flutterProjectRoot = rootProject.projectDir.parentFile.toPath()

    def plugins = new Properties()
    def pluginsFile = new File(flutterProjectRoot.toFile(), '.flutter-plugins')
    if (pluginsFile.exists()) {
        pluginsFile.withReader('UTF-8') { reader -> plugins.load(reader) }
    }

    plugins.each { name, path ->
        def pluginDirectory = flutterProjectRoot.resolve(path).resolve('android').toFile()
        if(!pluginDirectory.exists()){
            pluginDirectory = flutterProjectRoot.resolve(path).getParent().getParent().toFile()
        }
        if(pluginDirectory.exists()){
            include ":$name"
            project(":$name").projectDir = pluginDirectory
        }
    }
    ```
- android 工程下的 build.gradle  加入
    ```Gradle
    rootProject.extensions.add("tzj_bsdiff",Type.isFlutterPlugin.name())
    enum Type{
        isAPP,
        isModule,
        isFlutterPlugin;
    }
    project.ext {
        ext._compileSdkVersion = 27
        ext._buildToolsVersion = '27.0.3'
        ext._minSdkVersion = 16
        ext._targetSdkVersion = 27
        ext._supportVersion = "27.1.1"
        ext.javaVersion = JavaVersion.VERSION_1_8
    }
    ```

## android 代码需要做什么？
可以在Application中调用如下
```java
    @Override
    public void onCreate() {
        super.onCreate();
        AsyncTask update = UtilFlutterUpdate.update(this, new UtilFlutterUpdate.Callback() {
            @Override
            public void download(Context ctx, FlutterUpdate update) {
                //TODO 下载文件
                String newFile = "这是新下的文件";
                update.patchAndReplace(ctx, newFile);
            }
        });
    }
```
