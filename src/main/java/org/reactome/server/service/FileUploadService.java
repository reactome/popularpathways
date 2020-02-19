package org.reactome.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


@Component
public class FileUploadService {

    private PopularPathwaysService popularPathwaysService;

    @Autowired
    public void setPopularPathwaysService(PopularPathwaysService popularPathwaysService) {
        this.popularPathwaysService = popularPathwaysService;
    }

    public File saveLogFileToServer(MultipartFile file, int year) throws IOException {

        File serverFile = null;
        String UPLOADED_FOLDER = popularPathwaysService.getPopularPathwayFolder() + "/" + "log";

        if (!file.isEmpty()) {
            String csvFilePath = UPLOADED_FOLDER + "/" + year;
            File dir = new File(csvFilePath);
            if (!dir.exists())
                dir.mkdirs();

            byte[] bytes = file.getBytes();
            serverFile = new File(dir + "/" + file.getOriginalFilename());
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.flush();
            stream.close();
        }
        return serverFile;
    }

    //convert MuitipartFile to file: no need
    public File convertFile(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        convertFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convertFile);
        fos.write(file.getBytes());
        fos.close();
        return convertFile;
    }

}
