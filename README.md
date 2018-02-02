# apkUrlAnalysis
1、根据apk的下载路径来解析apk的包名、版本号，暂时不支持应用名和图标等信息；

2、本项目基于zip4j项目源码进行改造，根据apk的下载路径，不用下载apk，即可解析出apk的一些manifest中的信息；

3、600+m的apk解析差不多需要20秒左右，如果是https的路径的话可能会相对慢点；

