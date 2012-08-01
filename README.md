# lifelog-library

## Environment

sbt 0.11.x

scala 2.9.2


## Packages

* com.pokutuna.lifelog.db - DBを操作しちゃう系
    * model - DBのカラム等に対応するモデル
        * LifelogModel - lifelog.db内の各写真データカラム対応するモデル(PhotoRecord)
        * SensingModel - sensing.db内のデバイスとデバイス検出に対応するモデル
    * table - ORMapperが利用するテーブル
        * 略
    * dao - DatabaseAccessObject[(sample)](https://github.com/pokutuna/lifelog-library/blob/master/src/test/java/com/pokutuna/lifelog/sample/DAOSample.java)
        * LifelogDAO - lifelog.dbに簡単にアクセス
        * LifelogDAOForJava - java向け、返り値をSeq[T] -> java.util.List<T> にした
        * SensingDAO - sensing.dbに簡単にアクセス
        * SensingDAOForJava - LifelogDAOForJavaと同様
    * util - DBに関するユーティリティ
        * PhotoRecordFactory - java.io.FileからPhotoRecordを生成する
* com.pokutuna.lifelog.util - ユーティリティ
    * DateTime - SQLiteの日時の文字列表現をいじくる[(sample)](https://github.com/pokutuna/lifelog-library/blob/master/src/test/java/com/pokutuna/lifelog/sample/DateTimeSample.java)
    * Exif - 写真から抽出されるExifデータ、現在のところ撮影日時、緯度、経度のみ対応
    * ExifExtractor - java.io.FileからExifを抽出する[(sample)](https://github.com/pokutuna/lifelog-library/blob/master/src/test/java/com/pokutuna/lifelog/sample/ExifSample.java)
    * TimeUtil - 時刻を楽に扱う、DateTimeクラスが主に呼び出す
    * FileSelector - あるディレクトリ以下のファイルを正規表現でフィルタして取り出す[(sample)](https://github.com/pokutuna/lifelog-library/blob/master/src/test/java/com/pokutuna/lifelog/sample/FileSelectorSample.java)
* com.pokutuna.lifelog.parsing - ログパーサ
    * LogParser - ログデータをパースする[(sample)](https://github.com/pokutuna/lifelog-library/blob/master/src/test/java/com/pokutuna/lifelog/sample/LogParserSample.java)
    * LogToken - ログの各行、各行中のデータに対応するクラス集
    * Transformer - LogToken以下のクラスをcom.pokutuna.lifelog.db.model以下のクラスに変換
* com.pokutuna.lifelog.sample - サンプルコード
    * DAOSample
    * DateTimeSample
    * ExifSample
    * LogParserSample


## Javaのひとむけ

以下のjarをEclipseのビルドパスに追加して動かしてください

[lifelog-library](https://github.com/pokutuna/lifelog-library/downloads/) から最新版のjar

[scala-library.jar](http://repo.typesafe.com/typesafe/releases/org/scala-lang/scala-library/2.9.2/scala-library-2.9.2.jar) 2.9.2


* Javaから利用するサンプル - https://github.com/pokutuna/lifelog-library/tree/master/src/test/java/com/pokutuna/lifelog/sample


# Using Libraries

* metadata-extractor 
    * http://drewnoakes.com/code/exif/

* anorm
    * https://github.com/playframework/Play20

* sqlite-jdbc
    * http://www.xerial.org/trac/Xerial/wiki/SQLiteJDBC

These are provided under [Apache version 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).


# License

This library follows the Apache License version 2.0 (http://www.apache.org/licenses/)
