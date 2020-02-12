package org.reactome.server.service;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.util.Calendar.YEAR;

@Component
public class FileUploadService {

    @Autowired
    PopularPathwaysService popularPathwaysService;

    public void saveToServer(MultipartFile file) throws IOException {
        // get the directory to store file ...../input
        String UPLOADED_FOLDER = popularPathwaysService.getPopularPathwayLogFolder();
        if (!file.isEmpty()) {
            File dir = new File(UPLOADED_FOLDER + "/" + YEAR);
            if (!dir.exists())
                dir.mkdirs();

            byte[] bytes = file.getBytes();
            File serverFile = new File(dir + "/" + file.getOriginalFilename());
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.flush();
            stream.close();
            // return serverFile;
        }
    }

    //convert MuitipartFile to file: no need
    private File convertFile(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        convertFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convertFile);
        fos.write(file.getBytes());
        fos.close();
        return convertFile;
    }
}
