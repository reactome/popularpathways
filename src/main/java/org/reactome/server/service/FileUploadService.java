package org.reactome.server.service;

import org.apache.commons.codec.digest.DigestUtils;
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
        String UPLOADED_LOG_FOLDER = popularPathwaysService.getPopularPathwayFolder() + "/" + "log";

        if (!file.isEmpty()) {
            String logFilePath = UPLOADED_LOG_FOLDER + "/" + year;
            File dir = new File(logFilePath);
            if (!dir.exists())
                dir.mkdirs();

            byte[] bytes = file.getBytes();
            serverFile = new File(dir + "/" + "HSA-hits-" + year + ".csv");
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.flush();
            stream.close();
        }
        return serverFile;
    }

    /**
     * The multipart file is the one that the user submit and we get the checksum ....
     *
     * @param multipartFile the file from the Form sent by the user
     * @return
     * @throws IOException
     */
    public String getUploadFileMd5Code(MultipartFile multipartFile) throws IOException {
        String md5Ret = null;
        if (multipartFile != null && !multipartFile.isEmpty()) {
            md5Ret = DigestUtils.md5Hex(multipartFile.getInputStream());
        }
        return md5Ret;
    }
}
