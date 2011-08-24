package thumb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 指定された画像の縮小版（サムネイル画像）を生成し、返却するサーブレット.
 * @author Jomora ( kazuhiko@jomura.net http://jomura.net/ )
 */
public class ThumbServlet2 extends ThumbServlet {

    /** 直列化ID. */
    private static final long serialVersionUID = 1L;

    /**
     * URLクエリ「path」でURL指定された画像ファイルのサムネイルを作成する.
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        request.getSession(false);

        List<String> pathInfoList = getPathInfoList(request);

        int thumbWidth = MAX_WIDTH;
        int thumbHeight = MAX_HEIGHT;
        String imageUrl = null;
        switch (pathInfoList.size()) {
            case 1:
                // サムネイルのサイズが指定されなかった場合
                imageUrl = pathInfoList.get(0);
                break;
            case 2:
                // サムネイルの幅が指定された場合
                thumbWidth = getThumbnailWidth(pathInfoList.get(0), 65535);
                thumbHeight = 65535;
                imageUrl = pathInfoList.get(1);
                break;
            default:
                // サムネイルの幅・高さが指定された場合
                thumbWidth = getThumbnailWidth(pathInfoList.get(pathInfoList.size() - 3), 65535);
                thumbHeight = getThumbnailHeight(pathInfoList.get(pathInfoList.size() - 2), 65535);
                imageUrl = pathInfoList.get(pathInfoList.size() - 1);
                break;
        }
        if (65535 == thumbWidth && 65535 == thumbHeight) {
            thumbWidth = MAX_WIDTH;
            thumbHeight = MAX_HEIGHT;
        }
//        log("width:" + thumbWidth + " height:" + thumbHeight + " url:" + imageUrl);
        
        outputThumbnail(response, thumbWidth, thumbHeight, imageUrl);
    }

    /**
     * PathInfoを解析して、画像URLや指定サイズ情報を取り出す.
     * @param request HTTP要求
     * @return PathInfo要素リスト
     */
    private List<String> getPathInfoList(HttpServletRequest request) {
        String pathInfo = request.getRequestURI().substring(request.getContextPath().length());
//        log("pathInfo : " + pathInfo);

        // 画像URLの抽出
        int imageUrlStartIndex = pathInfo.indexOf("http://");
        if (-1 == imageUrlStartIndex) {
            imageUrlStartIndex = pathInfo.indexOf("https://");
        }
        if (-1 == imageUrlStartIndex) {
            throw new IllegalArgumentException("not specified image url.");
        }

        String imageUrl = pathInfo.substring(imageUrlStartIndex);
        String queryString = request.getQueryString();
        if (null != queryString && queryString.length() != 0) {
            imageUrl += "&" + queryString;
        }

        // その他情報の抽出
        String[] exPathInfo = pathInfo.substring(1, imageUrlStartIndex).split("/");

        List<String> pathInfoList = new ArrayList<String>(3);
        pathInfoList.addAll(Arrays.asList(exPathInfo));
        pathInfoList.add(imageUrl);
        return pathInfoList;
    }

}
