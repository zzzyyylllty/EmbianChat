# EmbianChat

### 项目使用 TabooLib Start Jar 创建!
### 爱来自枫溪

## 构建发行版本

发行版本用于正常使用, 不含 TabooLib 本体。

```
./gradlew clean build
```

## 构建开发版本

开发版本包含 TabooLib 本体, 用于开发者使用, 但不可运行。

```
./gradlew clean taboolibBuildApi -PDeleteCode
```

> 参数 -PDeleteCode 表示移除所有逻辑代码以减少体积。#   E m b i a n C h a t  
 