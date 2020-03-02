package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Image;
import dao.ImageDao;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author yolo
 * @date 2020/2/28-12:56
 */
@WebServlet("/image")
public class ImageServlet extends HttpServlet {
    /**
     * 查看图片信息：查看所有，查看指定
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 考虑到查看所有图片属性和查看指定图片属性
        // 通过是否 URL 中带有 imageId 参数来进行区分.
        // 存在 imageId 查看指定图片属性, 否则就查看所有图片属性
        // 例如: URL /image?imageId=100
        // imageId 的值就是 "100"
        // 如果 URL 中不存在 imageId 那么返回 null
        String imageId=req.getParameter("imageId");
        if (imageId == null || imageId.equals("")) {
            // 查看所有图片属性
            selectAll(req, resp);
        } else {
            // 查看指定图片
            selectOne(imageId, resp);
        }
    }



    private void selectAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=utf-8");
        //1.创建一个ImageDao对象，并查找数据库
        ImageDao imageDao=new ImageDao();
        List<Image> images = imageDao.selectAll();
        //2.把查找到的信息转换成JSON格式字符串，写回resp对象
        Gson gson=new GsonBuilder().create();
        String toJson = gson.toJson(images);

        resp.getWriter().write(toJson);
    }

    private void selectOne(String imageId, HttpServletResponse resp) throws IOException {
        //1.设置类型ContentType
        resp.setContentType("application/json; charset=utf-8");
        //2.创建ImageDao对象，对数据库进行查找
        ImageDao imageDao=new ImageDao();
        Image image = imageDao.selectOne(Integer.parseInt(imageId));
        //3.创建Gson对象,把查到的数据转换成json格式，宁写回响应
        Gson gson=new GsonBuilder().create();
        String toJson = gson.toJson(image);
        resp.getWriter().write(toJson);

    }


    /**
     * 上传图片
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
        获取图片信息，并切存入数据库
        1.需要创建一个factory对象和upload对象，这是为了获得到图片属性
          固定逻辑
         */
        FileItemFactory factory = new DiskFileItemFactory();//1.设置环境:创建一个DiskFileItemFactory工厂
        ServletFileUpload upload = new ServletFileUpload(factory);//2.核心操作类:创建一个文件上传解析器。
        /*
         使用ServletFileUpload解析器解析上传数据
            解析结果返回的是一个List<FileItem>集合每一个FileItem对应一个Form表单的输入项，也就是我们上传的一个文件
            List<FileItem> items =upload.parseRequest(req);

        public List parseRequest(javax.servlet.http.HttpServletRequest req)
        parseRequest 方法是ServletFileUpload类的重要方法，它是对HTTP请求消息体内容进行解析的入口方法。
        它解析出FORM表单中的每个字段的数据，并将它们分别包装成独立的FileItem对象
        然后将这些FileItem对象加入进一个List类型的集合对象中返回。

         */

        List<FileItem>items=null;
        try {
            items = upload.parseRequest(req);
        } catch (FileUploadException e) {
            //打印异常栈
            e.printStackTrace();

            // 告诉客户端出现的具体的错误是啥
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write("{ \"ok\": false, \"reason\": \"请求解析失败\" }");
            return;
        }

        //考虑每次只上传一张图片
        FileItem fileItem=items.get(0);
        Image image=new Image();
        //获取图片信息
        image.setImageName(fileItem.getName());
        image.setSize((int)fileItem.getSize());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// 手动获取一下当前日期, 并转成格式化日期, yyMMdd => 20200218
        image.setUploadTime(simpleDateFormat.format(new Date()));

        image.setContentType(fileItem.getContentType());
        //文件在服务器上存储的路径
        image.setPath("./image/" + System.currentTimeMillis()+"_"+image.getImageName());// 自己构造一个路径来保存, 引入时间戳是为了让文件路径能够唯一
        image.setMd5(fileItem.get().toString());// MD5 暂时先不去计算

        //存入数据库
        ImageDao imageDao=new ImageDao();
        imageDao.insert(image);

        // 2. 获取图片的内容信息, 若磁盘或中还没有相同图片，则写入磁盘文件，否则不写入
        // 看看数据库中是否存在相同的 MD5 值的图片, 不存在, 返回 null
//        Image existImage = imageDao.selectByMd5(image.getMd5());
//        if (existImage == null) {
//            File file = new File(image.getPath());
//            try {
//                fileItem.write(file);
//            } catch (Exception e) {
//                e.printStackTrace();
//
//                resp.setContentType("application/json; charset=utf-8");
//                resp.getWriter().write("{ \"ok\": false, \"reason\": \"写磁盘失败\" }");
//                return;
//            }
//        }
        //进行重定向，让客户直接看到结果-302重定向
        //resp.sendRedirect("index.html");
    }

    /**
     * 删除指定图片
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        // 1. 先获取到请求中的 imageId
        String imageId = req.getParameter("imageId");
        if (imageId == null || imageId.equals("")) {
            resp.setStatus(200);
            resp.getWriter().write("{ \"ok\": false, \"reason\": \"解析请求失败\" }");
            return;
        }
        // 2. 创建 ImageDao 对象, 查看到该图片对象对应的相关属性
        // 这是为了知道这个图片对应的文件路径，便于随磁盘进行删除文件操作
        ImageDao imageDao = new ImageDao();
        Image image = imageDao.selectOne(Integer.parseInt(imageId));
        if (image == null) {
            // 此时请求中传入的 id 在数据库中不存在.
            resp.setStatus(200);
            resp.getWriter().write("{ \"ok\": false, \"reason\": \"imageId 在数据库中不存在\" }");
            return;
        }

        // 3. 删除数据库中的记录
        imageDao.delete(Integer.parseInt(imageId));

        // 4. 删除本地磁盘文件

        // 看看数据库中是否存在相同的 MD5 值的图片, 不存在, 返回 null
        Image existImage = imageDao.selectByMd5(image.getMd5());
        if (existImage == null) {
            File file = new File(image.getPath());
            file.delete();
        }
        resp.setStatus(200);
        resp.getWriter().write("{ \"ok\": true }");
    }
}
