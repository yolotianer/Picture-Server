package dao;

import common.JavaImageServerException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yolo
 * @date 2020/2/26-21:37
 */
public class ImageDao {
    /**
     *把Image对象插入数据库中
     * @param image
     */
    public  void insert(Image image)  {
        // 1. 获取数据库连接
        Connection connection = DBUtil.getConnection();
        // 2. 创建并拼装 SQL 语句
        String sql = "insert into image_table values(null, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, image.getImageName());
            statement.setInt(2, image.getSize());
            statement.setString(3, image.getUploadTime());
            statement.setString(4, image.getContentType());
            statement.setString(5, image.getPath());
            statement.setString(6, image.getMd5());

            // 3. 执行 SQL 语句
            int ret = statement.executeUpdate();//影响行数
            if (ret != 1) {
                // 程序出现问题, 抛出一个异常
                throw new JavaImageServerException("数据库插入出错");
            }
        } catch (SQLException | JavaImageServerException e) {
            e.printStackTrace();
        } finally {
            // 4. 关闭连接和statement对象
            DBUtil.close(connection, statement, null);
        }
    }

    /**
     * 查找出所有Image数据(分页)
     * @return
     */
    public List<Image> selectAll(){
        List<Image>images=new ArrayList<>();
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        PreparedStatement statement=null;
        ResultSet res=null;
        //2.拼装SQL语句
        String sql="select * from image_table";
        //3..获取SQL执行对象
        try {
             statement = connection.prepareStatement(sql);
            //4.执行SQL语句
             res = statement.executeQuery();
            //5.处理结果对象
            while (res.next()){
                Image image=new Image();
                image.setImageId(res.getInt("imageId"));
                image.setImageName(res.getString("imageName"));
                image.setSize(res.getInt("size"));
                image.setUploadTime(res.getString("uploadTime"));
                image.setContentType(res.getString("contentType"));
                image.setPath(res.getString("path"));
                image.setMd5(res.getString("md5"));
                images.add(image);
            }
            return images;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection, statement,res);
        }
        //6.关闭相关对象
        return null;
    }

    /**
     * 查找指定图片信息
     * @param imageId：图片Id
     * @return
     */
    public Image selectOne(int imageId){
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2.品璋SQL语句
        String sql="select * from image_table where imageId=?";
        PreparedStatement statement=null;
        ResultSet res=null;
        try {
            //3.获取SQL执行对象
            statement = connection.prepareStatement(sql);
            statement.setInt(1,imageId);
            //4.执行SQL对象
             res = statement.executeQuery();
            if(res.next()){
                Image image = new Image();
                image.setImageId(res.getInt("imageId"));
                image.setImageName(res.getString("imageName"));
                image.setSize(res.getInt("size"));
                image.setUploadTime(res.getString("uploadTime"));
                image.setContentType(res.getString("contentType"));
                image.setPath(res.getString("path"));
                image.setMd5(res.getString("md5"));
                return image;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,res);
        }
        return null;
    }

    /**
     * 删除指定图片
     * @param imageId：图片Id
     */
    public void delete (int imageId){
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        String sql="delete from image_table where imageId=?";
        PreparedStatement statement=null;
        try {
             statement = connection.prepareStatement(sql);
             statement.setInt(1,imageId);
            int ret = statement.executeUpdate();
            if(ret!=1){
                throw new JavaImageServerException("删除图片出错");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JavaImageServerException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,null);
        }
    }

    public Image selectByMd5(String md5) {
        // 1. 获取数据库连接
        Connection connection = DBUtil.getConnection();
        // 2. 构造 SQL 语句
        String sql = "select * from image_table where md5 = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 3. 执行 SQL 语句
            statement = connection.prepareStatement(sql);
            statement.setString(1, md5);
            resultSet = statement.executeQuery();
            // 4. 处理结果集
            if (resultSet.next()) {
                Image image = new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                return image;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 5. 关闭链接
            DBUtil.close(connection, statement, resultSet);
        }
        return null;
    }
}
