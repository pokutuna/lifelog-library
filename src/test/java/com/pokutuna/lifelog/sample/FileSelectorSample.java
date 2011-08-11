package com.pokutuna.lifelog.sample;

import com.pokutuna.lifelog.util.FileSelectorForJava;
import java.util.List;
import java.io.File;

//FileSelectorサンプル
public class FileSelectorSample {

  //指定したディレクトリ以下のFileをListで返すユーティリティ

  public static void main(String[] args) {
    //パスを指定したFileをFileSelectorForJava.selectに渡す
    List<File> files = FileSelectorForJava.select(new File("src/test/resources"));
    for(File f : files) {
      System.out.println(f.getPath());
    }
    //指定したディレクトリ以下のファイルが入ったリストが返る
    /*
src/test/resources/icon.jpg
src/test/resources/instagram.jpg
src/test/resources/tanu.jpg
src/test/resources/test_btlogdata.tsv
src/test/resources/test_lifelog.db
src/test/resources/test_sensing.db
src/test/resources/test_wifilogdata.tsv
    */

    /*
      正規表現でフィルタ
      正規表現にマッチしたファイル名を持つファイルのみを取り出すことができる。
      ここでは.jpgを取り出している。
     */
    List<File> filteredFiles = FileSelectorForJava.select(new File("src/test/resources"), ".*\\.jpg");
    for(File f : filteredFiles) {
      System.out.println(f.getPath());
    }
    /*
src/test/resources/icon.jpg
src/test/resources/instagram.jpg
src/test/resources/tanu.jpg
    */

    /*
      もうちょっと複雑な例: test_から始まり.dbで終わるファイル
     */
    List<File> filteredFiles2 = FileSelectorForJava.select(new File("src/test/resources"), "test_.*\\.db");
    for(File f : filteredFiles2) {
      System.out.println(f.getPath());
    }
    /*
src/test/resources/test_lifelog.db
src/test/resources/test_sensing.db
    */

    /*
      パス以外の物を渡すとIllegalArgumentExceptionが返る
     */
  }
}
