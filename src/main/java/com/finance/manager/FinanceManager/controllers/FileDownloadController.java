package com.finance.manager.FinanceManager.controllers;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finance.manager.FinanceManager.ReadWriteExelData.ExelData;
import com.finance.manager.FinanceManager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileDownloadController {

    @Autowired
    TransactionRepository transactionRepository;

    @RequestMapping("/admin/allTransaction/exelDownload")
    public void downloadExelResource(HttpServletRequest request, HttpServletResponse response
                                   ) throws IOException {

        File file = ExelData.importDataToExelFile(transactionRepository.findAll());

        if (file.exists()) {

            //get the mimetype
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            System.out.println(mimeType);
            if (mimeType == null) {
                //unknown mimetype so set the mimetype to application/octet-stream
                mimeType = "application/octet-stream";
                System.out.println(mimeType);

            }

            response.setContentType(mimeType);

            /**
             * In a regular HTTP response, the Content-Disposition response header is a
             * header indicating if the content is expected to be displayed inline in the
             * browser, that is, as a Web page or as part of a Web page, or as an
             * attachment, that is downloaded and saved locally.
             *
             */

            /**
             * Here we have mentioned it to show inline
             */
            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

            //Here we have mentioned it to show as attachment
            // response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

            response.setContentLength((int) file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            FileCopyUtils.copy(inputStream, response.getOutputStream());
            file.delete();
        }else {
            System.out.println(file.getPath());
        }
    }
}

