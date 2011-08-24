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
 * �w�肳�ꂽ�摜�̏k���Łi�T���l�C���摜�j�𐶐����A�ԋp����T�[�u���b�g.
 * 
 * @author Jomora ( kazuhiko@jomura.net http://jomura.net/ )
 * @version 2006/05/06 �R�����g�ǉ�(CheckStyle,FindBugs�Ή�)
 * @version 2006/11/27 �w�肳�ꂽ�摜��������Ȃ��ꍇ�AHTTP:404��Ԃ������ɕύX
 */
public class ThumbServlet extends HttpServlet {

    /**
     * ����ID.
     */
    private static final long serialVersionUID = -496429029995188148L;

    /**
     * �T���l�C���摜�̃f�t�H���g��(pixel).
     */
    private static final int MAX_WIDTH = 120;
    /**
     * �T���l�C���摜�̃f�t�H���g����(pixel).
     */
    private static final int MAX_HEIGHT = 90;

    /**
     * �t�@�C���A�b�v���[�h�p�̃��[�e�B���e�B�N���X.
     */
    private transient DiskFileUpload dfu = new DiskFileUpload();
    /**
     * �A�b�v���[�h�t�@�C���̍ő�T�C�Y.
     */
    private static final int SIZE_MAX = 30000000;
    /**
     * �A�b�v���[�h�t�@�C�����I���������ŏ������邩�A
     * �f�B�X�N�ɏ������ނ��̋��E�T�C�Y.
     */
    private static final int SIZE_THRESHOLD = 5000000;
    /**
     * �A�b�v���[�h�t�@�C���̈ꎞ�ۊǃf�B���N�g��.
     */
    private static final String REPOSITORY_PATH =
        System.getProperty("java.io.tmpdir");
    /**
     * �A�b�v���[�h�t�@�C���̃w�b�_�����R�[�h.
     */
    private static final String HEADER_ENCODING = "Windows-31J";

    /**
     * ��������.
     *  �t�@�C���A�b�v���[�h�p�̃��[�e�B���e�B�N���X�ɑ΂��āA
     *  �ő�t�@�C���T�C�Y�A�ꎞ�ۊǃf�B���N�g���A�w�b�_�����R�[�h����ݒ肷��B
     */
    @Override
    public final void init() {
        dfu.setSizeMax(SIZE_MAX);
        dfu.setSizeThreshold(SIZE_THRESHOLD);
        dfu.setRepositoryPath(REPOSITORY_PATH);
        dfu.setHeaderEncoding(HEADER_ENCODING);
    }

    /**
     * URL�N�G���upath�v��URL�w�肳�ꂽ�摜�t�@�C���̃T���l�C�����쐬����.
     * @param request HTTP���N�G�X�g
     * @param response HTTP���X�|���X
     * @throws ServletException �T�[�u���b�g��O
     * @throws IOException ���o�͗�O
     */
    @Override
    protected final void doGet(
    		final HttpServletRequest request,
    		final HttpServletResponse response)
    	throws ServletException, IOException {

		request.getSession(false);

        // �f�t�H���g�̃T���l�C���摜�T�C�Y
        int maxThumbWidth = MAX_WIDTH;
        int maxThumbHeight = MAX_HEIGHT;

        // �T���l�C���摜�̍ő啝�����肷��B
        if (request.getParameter("mtw") != null) {
            try {
                maxThumbWidth = Integer.parseInt(request.getParameter("mtw"));
            } catch (NumberFormatException nfe) {
                String message = "param:mtw needs number format.";
                log(message);
                throw new ServletException(message, nfe);
            }
        }
        // �T���l�C���摜�̍ő卂�������肷��B
        if (request.getParameter("mth") != null) {
            try {
                maxThumbHeight = Integer.parseInt(request.getParameter("mth"));
            } catch (NumberFormatException nfe) {
                String message = "param:mth needs number format.";
                log(message);
                throw new ServletException(message, nfe);
            }
        }

        // �摜�t�@�C����URL������
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
     * upload.jsp����|�X�g���ꂽ�摜�t�@�C���̃T���l�C�����쐬����.
     * @param request HTTP���N�G�X�g
     * @param response HTTP���X�|���X
     * @throws ServletException �T�[�u���b�g��O
     * @throws IOException ���o�͗�O
     */
	@Override
    public final void doPost(
    		final HttpServletRequest request,
    		final HttpServletResponse response)
        throws ServletException, IOException {
		
		request.getSession(false);

        // POST�� enctype="multipart/form-data" ���ǂ����`�F�b�N����B
        if (!FileUploadBase.isMultipartContent(request)) {
            String message = "request does not consist of mutipartContent.";
            log(message);
            throw new ServletException(message);
        }

        // �f�t�H���g�̃T���l�C���摜�T�C�Y
        int maxThumbWidth = MAX_WIDTH;
        int maxThumbHeight = MAX_HEIGHT;

        // �p�����[�^���o������
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

        // �A�b�v���[�h���ꂽ�t�@�C�����Ȃ��ꍇ�͗�O�Ƃ���B
        if (uploadFileItem == null) {
            String message = "upload file is not specified.";
            log(message);
            throw new ServletException(message);
        }

        // �T���l�C���摜�̍ő啝�����肷��B
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
        // �T���l�C���摜�̍ő卂�������肷��B
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

        // �A�b�v���[�h���ꂽ�摜�t�@�C�����T�[�o�ɕۊǂ���B
        File restoreFile = new File(System.getProperty("java.io.tmpdir")
                + java.io.File.separator
                + System.currentTimeMillis()
                + "_" + new File(uploadFileItem.getName()).getName());
        FileOutputStream fos = new FileOutputStream(restoreFile);
        fos.write(uploadFileItem.get());
        fos.close();

        // �v���b�g�t�H�[���ɉ摜�ǂݍ��݃T�|�[�g�����邩�ǂ����`�F�b�N�B
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

        // �T���l�C�����N���C�A���g�֕Ԃ�����
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
