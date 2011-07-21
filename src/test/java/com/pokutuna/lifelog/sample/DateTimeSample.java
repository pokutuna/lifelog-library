package com.pokutuna.lifelog.sample;

import com.pokutuna.lifelog.util.DateTime;
import java.util.Date;

//DateTimeクラス利用サンプル
public class DateTimeSample {

  public static void main(String[] args) {
    //文字列からDateTimeインスタンスを作るにはDateTime.formatを使う
    DateTime dt = DateTime.format("2011-07-18 21:28:00");
    System.out.println(dt); // => DateTime(2011-07-18 21:28:00)

    //文字列表現を得るにはDateTime#asStringを使う
    System.out.println(dt.asString()); // => 2011-07-18 21:28:00

    //桁を0で埋めていなかったりする怪しげな文字列表現の補正を行うこともできる
    DateTime dtFromDirtyFormat = DateTime.format("2011-7-1 1:3:5");
    System.out.println(dtFromDirtyFormat.asString()); // => 2011-07-01 01:03:05

    //このクラスを経由することで文字列を綺麗にするのが主な使い方になるかもしれない

    //年月日等それぞれの値へのアクセサ
    System.out.println(dt.year()); // => 2011
    System.out.println(dt.month()); // => 7
    System.out.println(dt.day()); // => 18
    System.out.println(dt.hour());  // => 21
    System.out.println(dt.minute());  // => 28
    System.out.println(dt.second()); // => 0

    //日付まで、時刻のみのアクセサ
    System.out.println(dt.date()); // => 2011-07-18
    System.out.println(dt.time()); // => 21:28:00

    //DateTimeインスタンスを基準に前後の時間を保持したDateTimeインスタンスを取得する
    //y,m,d,h,min,sのフィールドに差分を入れる、scalaからならdt.fromNow(minute = 3)とかNamedParameterで書けるようにしてある
    //未来の時刻を取得するにはDateTime#fromNowを使う、次は3分後の時刻を得る例
    System.out.println(dt.fromNow(0, 0, 0, 0, 3, 0)); // => DateTime(2011-07-18 21:31:00)
    //過去の時刻を取得するにはDateTime#agoを使う、次は1時間半前の時刻を得る例
    System.out.println(dt.ago(0,0,0,1,30,0)); // => DateTime(2011-07-18 19:58:00)
    //もちろんメソッドチェーンもきく
    System.out.println(dt.ago(1, 0, 0, 0, 0, 0).asString()); // => 2010-07-18 21:28:00

    //java.util.Date型への変換はDateTime#toDateを使う
    System.out.println(dt.toDate()); // => Mon Jul 18 21:28:00 JST 2011

    //java.util.Date型からDateTime型への変換はDateTime.applyを使う
    System.out.println(DateTime.apply(new Date()).asString()); // => 2011-07-18 21:48:19

    //year, month, day, hour, minute, secondの値を各個に入力するにもDateTime.applyを使う
    System.out.println(DateTime.apply(2011,4,7,1,2,3).asString()); // => 2011-04-07 01:02:03

  }
}
