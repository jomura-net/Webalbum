package thumb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * �w�肳�ꂽ�摜�̏k���Łi�T���l�C���摜�j�𐶐����A�ԋp����T�[�u���b�g.
 * @author Jomora ( kazuhiko@jomura.net http://jomura.net/ )
 */
public class ThumbServlet2 extends ThumbServlet {

    /** ����ID. */
    private static final long serialVersionUID = 1L;

    /**
     * URL�N�G���upath�v��URL�w�肳�ꂽ�摜�t�@�C���̃T���l�C�����쐬����.
     * @param request HTTP���N�G�X�g
     * @param response HTTP���X�|���X
     * @throws ServletException �T�[�u���b�g��O
     * @throws IOException ���o�͗�O
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
                // �T���l�C���̃T�C�Y���w�肳��Ȃ������ꍇ
                imageUrl = pathInfoList.get(0);
                break;
            case 2:
                // �T���l�C���̕����w�肳�ꂽ�ꍇ
                thumbWidth = getThumbnailWidth(pathInfoList.get(0), 65535);
                thumbHeight = 65535;
                imageUrl = pathInfoList.get(1);
                break;
            default:
                // �T���l�C���̕��E�������w�肳�ꂽ�ꍇ
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
     * PathInfo����͂��āA�摜URL��w��T�C�Y�������o��.
     * @param request HTTP�v��
     * @return PathInfo�v�f���X�g
     */
    private List<String> getPathInfoList(HttpServletRequest request) {
        String pathInfo = request.getRequestURI().substring(request.getContextPath().length());
//        log("pathInfo : " + pathInfo);

        // �摜URL�̒��o
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

        // ���̑����̒��o
        String[] exPathInfo = pathInfo.substring(1, imageUrlStartIndex).split("/");

        List<String> pathInfoList = new ArrayList<String>(3);
        pathInfoList.addAll(Arrays.asList(exPathInfo));
        pathInfoList.add(imageUrl);
        return pathInfoList;
    }

}
