3種類のアプリが混在しています。

# WebAlbum

* サーバーファイルシステム上の画像格納フォルダ以下にある画像ファイルを再帰的に検索し、作成したサムネイルをメモリ上にストックして、一覧ページを表示します。
* subfolder毎に、アコーディオン？で表示します。  
* ファイルシステム上で画像の削除が検出された場合は、次回参照時に登録が解除され、表示されなくなります。
* ファイルシステム上で新規画像の追加があった場合、/picture/list?check=1 にアクセスすると、差分の更新を行います。また、一定時間毎に自動更新しています。
* "EXIF情報の取得":http://www.drewnoakes.com/code/exif/ もやってま～す。
* JavaEE。

## DEMO

https://jomura.net/picture/

![scrsht](https://user-images.githubusercontent.com/42256916/113679500-ca99ce80-96fa-11eb-8c67-73f5babd90dc.jpg)

## Requirement

* Java EE APサーバ
* JDK 8 以上

* eclipse  
eclipse project なので。  

## Install

1. web.xmlで、"PictureDir"を指定してください。

## Changelog

### version:WebAlbum-1.0 (r20)

* 初版作成。

[Features]
* 特になし

[Fixed Bugs]
* Timerのcancel()実装 (#135)

<br /><br /><br />



# WallpaperSetter

指定したURLの画像を、Windows desktopの背景に設定するWindows Application。  
上述の"WebAlbum"にある「ランダムに選択した画像を返すServlet」のURLを指定すると、実行都度、画像がランダムに切り替わる。  

## Requirement

* .NET framework

## Install

1. *.configファイルで、"ImageURL"と"FitScreen"を設定する。
　FitScreen : 画像サイズをdesktopのサイズに合わせて伸張するか？ true/false

<br /><br /><br />


# Thumbnail

* 指定した画像のサムネイル画像を返します。
* aspect比を変えずに、指定したwidth,heightの小さい方に合わせて縮小します。
* width,heightの指定は省略化。また、大きな数を指定しても、画像等倍より拡大はしません。
* [GET] だと、URLで指定された画像のサムネイルを作って返します。
** URLパターン1 http://jomura.net/thumbnail/240/180/http://jomura.net/archive/2004/V6010005.jpg
** URLパターン2 http://jomura.net/thumbnail/maker.jpg?mtw=240&mth=180&path=http://jomura.net/archive/2004/V6010005.jpg
*** path:画像のURL、mtw:最大幅(default:120[px])、mth:最大高(default:90[px])
* [POST] だと、ブラウザからアップロードされたファイルのサムネイルを作って返します。
** 公開URL : http://jomura.net/thumbnail/
** Apacheの commons-fileupload 1.0 を使ってます。日本語ファイル名も問題なく使えるようになったのですね…
*** 参考サイト http://www.ki.rim.or.jp/~kuro/Java/Commons/01-FileUpload.html
* JavaEE。

## Requirement

* Java EE APサーバ
* JDK 8 以上

### for development
* eclipse  
eclipse project なので。  

## Changelog

### version:Thumbnail-1.0 (r24)

* 初版作成。

[Features]
* "Sencha io":http://www.sencha.com/products/io/ のようなURL指定を可能とした。(#134)

[Fixed Bugs]
* 特になし
