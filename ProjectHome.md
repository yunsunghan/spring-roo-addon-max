# Quick Start #
먼저 max osgi를 설치하고, max osgi install로 관련 번들을 설치한다.
```

roo>osgi start --url http://spring-roo-addon-max.googlecode.com/svn/repo/com.ks.spring.roo.addon.maxosgi-1.0.0.M2.jar 

roo>max osgi install --version 1.0.0.M2

```
특정 roo 프로젝트 폴더를 만들고, 아래처럼 max show를 실행한다.

```
>mkdir test2
>roo
roo>max show start --topLevelPackage net.max --projectName test2
>q
```
생성된 코드를 실행한다.
```
>mvn jetty:run
```
max 관련 addon을 제거한다. 이때, max osgi 는 수동으로 제거해야 한다.
```

>roo
roo>max osgi uninstall 
roo>osgi uninstall --bundelSymbolicName com.ks.spring.roo.addon.maxosgi

```

http://cfile22.uf.tistory.com/image/191D1F374E1651AB2E2104
