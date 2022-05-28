package com.reggie.controller;

import cn.hutool.core.lang.UUID;
import com.reggie.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author XuLongjie
 * @create 2022-05-14-14:41
 */

@Api(tags = "文件上传和下载")
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //随机生成UUID作为文件名，避免文件名重复被覆盖  + .jpg
        String filename = UUID.randomUUID().toString(true) + suffix;

        //创建文件目录
        File dir = new File(basePath);
        //判断文件目录是否存在
        if (!dir.exists()) {
            //不存在则创建
            dir.mkdirs();
        }

        try {
            //将文件转存到指定位置
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return R.success(filename);
    }


    @ApiOperation("文件下载")
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        try {
            //通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //通过输出流以流的形式将文件回显到浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");

            //读取内容
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
