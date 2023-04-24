機器學習讀書筆記
=================

前言
-----------
簡單的機器學習讀書筆記，主要範例出自以下書藉與教學影片：

1. [機器學習駭客秘笈 Machine Learning for Hackers][01]
2. [Data Analytics and Machine Learning Fundamentals LiveLessons Video Training][02]

由於這兩個教學資源裡，使用的均是我不熟悉的程式語言環境，例如 R 或 Matlab 來進行實作。

因此對於我自己而言，學習最快的方式，就是看是否能以我自己熟悉的 Scala 生態環境，實作出與書中或影片中範例的相同效果的程式，而這個 REPO 就是相關的程式碼的集結，並且在程式碼中加入說明範例。

也因為是自己的讀書筆記，所以範例不一定與書中是一對一的對應的。例如在「機器學習駭客秘笈」第二張有一些簡易的統計觀念，例如平均數、中位數、變異數、標準差與常見的分佈……等等基本的統計學概念的介紹與作圖，但因為是真的很基本的統計學概念，程式碼也相對簡單，所以這個 REPO 裡就沒有相對應的範例。

另外，上述第二個教學影片，雖然講的很淺顯易懂，但不幸的是並沒有提供範例測試資料。因此我只能夠將其中講到的概念，套用在我能找到的合適的資料上。舉例而言，第二個影片的線性迴歸，是以行車時間與剎車速度來做範例，但這個 REPO 裡，會以一萬人的男女分組的身高體重做為範例。

目錄
-----

以下為目前的進度：

1. [01-DataCleanning 資料清理](01-DataCleanning/README.md)
2. [02-DataObservation 資料觀察](02-DataObservation/README.md)
3. [03-LinearRegression 線性迴歸預測](03-LinearRegression/README.md)

程式碼編譯方式
---------------

需求：Java 11 SDK

1. 下載 SBT (Simple Build Tool) 並安裝
2. 進到相對應的目錄，並執行 `sbt run` 指令

```console
  $ cd 01-DataCleanning
  $ sbt run
```

[01]: https://learning.oreilly.com/library/view/machine-learning-for/9781449330514/
[02]: https://learning.oreilly.com/videos/data-analytics-and/9780135557358/
