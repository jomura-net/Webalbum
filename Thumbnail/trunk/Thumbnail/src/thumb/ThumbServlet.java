package thumb;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jomora.io.File;
import jomora.io.IOUtils;
import jomora.io.image.ThumbnailFactory;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;

/**
 * 指定された画像の縮小版（サムネイル画像）を生成し、返却するサーブレット.
 * 
 * @author Jomora ( kazuhiko@jomura.net http://jomura.net/ )
 * @version 2006/05/06 コメント追加(CheckStyle,FindBugs対応)
 * @version 2006/11/27 指定された画像が見つからない場合、HTTP:404を返すだけに変更
 */
public class ThumbServlet extends HttpServlet {

    /**
     * 直列化ID.
     */
    private static final long serialVersionUID = -496429029995188148L;

    /**
     * サムネイル画像のデフォルト幅(pixel).
     */
    private static final int MAX_WIDTH = 120;
    /**
     * サムネイル画像のデフォルト高さ(pixel).
     */
    private static final int MAX_HEIGHT = 90;

    /**
     * ファイルアップロード用のユーティリティクラス.
     */
    private transient DiskFileUpload dfu = new DiskFileUpload();
    /**
     * アップロードファイルの最大サイズ.
     */
    private static final int SIZE_MAX = 30000000;
    /**
     * アップロードファイルをオンメモリで処理するか、
     * ディスクに書き込むかの境界サイズ.
     */
    private static final int SIZE_THRESHOLD = 5000000;
    /**
     * アップロードファイルの一時保管ディレクトリ.
     */
    private static final String REPOSITORY_PATH =
        System.getProperty("java.io.tmpdir");
    /**
     * アップロードファイルのヘッダ文字コード.
     */
    private static final String HEADER_ENCODING = "Windows-31J";

    /**
     * 初期処理.
     *  ファイルアップロード用のユーティリティクラスに対して、
     *  最大ファイルサイズ、一時保管ディレクトリ、ヘッダ文字コード等を設定する。
     */
    @Override
    public final void init() {
        dfu.setSizeMax(SIZE_MAX);
        dfu.setSizeThreshold(SIZE_THRESHOLD);
        dfu.setRepositoryPath(REPOSITORY_PATH);
        dfu.setHeaderEncoding(HEADER_ENCODING);
    }

    /**
     * URLクエリ「path」でURL指定された画像ファイルのサムネイルを作成する.
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected final void doGet(
    		final HttpServletRequest request,
    		final HttpServletResponse response)
    	throws ServletException, IOException {

		request.getSession(false);

        // デフォルトのサムネイル画像サイズ
        int maxThumbWidth = MAX_WIDTH;
        int maxThumbHeight = MAX_HEIGHT;

        // サムネイル画像の最大幅を決定する。
        if (request.getParameter("mtw") != null) {
            try {
                maxThumbWidth = Integer.parseInt(request.getParameter("mtw"));
            } catch (NumberFormatException nfe) {
                String message = "param:mtw needs number format.";
                log(message);
                throw new ServletException(message, nfe);
            }
        }
        // サムネイル画像の最大高さを決定する。
        if (request.getParameter("mth") != null) {
            try {
                maxThumbHeight = Integer.parseInt(request.getParameter("mth"));
            } catch (NumberFormatException nfe) {
                String message = "param:mth needs number format.";
                log(message);
                throw new ServletException(message, nfe);
            }
        }

        // 画像ファイルのURL文字列
        String pathStr = request.getParameter("path");
        String extension = File.getExtension(pathStr);

        InputStream is = null;
        ByteArrayOutputStream baos = null;
        byte[] thumbnailBytes;
        try {
            URL path = new URL(pathStr);
            is = path.openStream();
            baos = new ByteArrayOutputStream();

            ThumbnailFactory.createThumbnail(is, baos,
                    maxThumbWidth, maxThumbHeight, extension);
            thumbnailBytes = baos.toByteArray();
        /*
        } catch (MalformedURLException murle) {
            String message = "param:path needs a correct URL.";
            log(message);
            throw new ServletException(message, murle);
        } catch (FileNotFoundException fnfe) {
            String message = "param:path needs a correct URL.";
            log(message);
            throw new ServletException(message, fnfe);
        */
        } catch (IllegalArgumentException iae) {
        	response.sendError(
        			HttpServletResponse.SC_BAD_REQUEST, iae.getMessage());
        	return;
        } catch (IOException ioe) {
        	response.sendError(
        			HttpServletResponse.SC_NOT_FOUND, ioe.getMessage());
        	return;
        } finally {
        	IOUtils.closeQuietly(is);
        	IOUtils.closeQuietly(baos);
        }

        response.getOutputStream().write(thumbnailBytes);
    }

    /**
     * upload.jspからポストされた画像ファイルのサムネイルを作成する.
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
	@Override
    public final void doPost(
    		final HttpServletRequest request,
    		final HttpServletResponse response)
        throws ServletException, IOException {
		
		request.getSession(false);

        // POSTが enctype="multipart/form-data" かどうかチェックする。
        if (!FileUploadBase.isMultipartContent(request)) {
            String message = "request does not consist of mutipartContent.";
            log(message);
            throw new ServletException(message);
        }

        // デフォルトのサムネイル画像サイズ
        int maxThumbWidth = MAX_WIDTH;
        int maxThumbHeight = MAX_HEIGHT;

        // パラメータ取り出し処理
        FileItem uploadFileItem = null;
        Map<String, String> paramMap = new HashMap<String, String>();
        try {
            @SuppressWarnings("unchecked")
            List<FileItem> itemList = 
            	(List<FileItem>) dfu.parseRequest(request);
            Iterator<FileItem> itemIter = itemList.iterator();
            while (itemIter.hasNext()) {
                FileItem item = itemIter.next();
                if (!item.isFormField()) {
                    String name = item.getName();
                    if (name != null && !name.equals("")) {
                        uploadFileItem = item;
                    }
                } else {
                    paramMap.put(item.getFieldName(), item.getString());
                }
            }
        } catch (FileUploadException fue) {
            throw new ServletException(fue);
        }

        // アップロードされたファイルがない場合は例外とする。
        if (uploadFileItem == null) {
            String message = "upload file is not specified.";
            log(message);
            throw new ServletException(message);
        }

        // サムネイル画像の最大幅を決定する。
        String mtwParam = paramMap.get("mtw");
        if (mtwParam != null) {
            try {
                maxThumbWidth = Integer.parseInt(mtwParam);
            } catch (NumberFormatException nfe) {
                String message = "param:mtw needs number format.";
                log(message);
                throw new ServletException(message);
            }
        }
        // サムネイル画像の最大高さを決定する。
        String mthParam = paramMap.get("mth");
        if (mthParam != null) {
            try {
                maxThumbHeight = Integer.parseInt(mthParam);
            } catch (NumberFormatException nfe) {
                String message = "param:mth needs number format.";
                log(message);
                throw new ServletException(message);
            }
        }

        // アップロードされた画像ファイルをサーバに保管する。
        File restoreFile = new File(System.getProperty("java.io.tmpdir")
                + java.io.File.separator
                + System.currentTimeMillis()
                + "_" + new File(uploadFileItem.getName()).getName());
        FileOutputStream fos = new FileOutputStream(restoreFile);
        fos.write(uploadFileItem.get());
        fos.close();

        // プラットフォームに画像読み込みサポートがあるかどうかチェック。
        String extension = File.getExtension(uploadFileItem.getName());
        if (!ThumbnailFactory.isSupportedReaderFormat(extension)) {
            String message = "this image format is not supported.";
            throw new ServletException(message);
        }

        InputStream iStrm = null;
        ByteArrayOutputStream baos = null;
        byte[] thumbnailBytes = null;
        try {
            iStrm = uploadFileItem.getInputStream();
            baos = new ByteArrayOutputStream();

            ThumbnailFactory.createThumbnail(iStrm, baos,
                maxThumbWidth, maxThumbHeight, extension);
            thumbnailBytes = baos.toByteArray();
        } catch (IOException ioe) {
        	response.sendError(
        			HttpServletResponse.SC_NOT_FOUND, ioe.getMessage());
        	return;
        } finally {
        	IOUtils.closeQuietly(iStrm);
        	IOUtils.closeQuietly(baos);
            uploadFileItem.delete();
        }

        // サムネイルをクライアントへ返す方式
        String outputType = "attachment";
        if (paramMap.get("type").equals("inline")) {
            outputType = "inline";
        }
        response.addHeader("Content-Disposition", outputType
                + "; filename=\"tn_"
                + File.getName(uploadFileItem.getName()) + "\"");

        response.getOutputStream().write(thumbnailBytes);
    }

}
