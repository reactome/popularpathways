package org.reactome.server.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@SuppressWarnings("WeakerAccess")
@Service
public class FileUploadService {

    private PopularPathwaysService popularPathwaysService;

    @Autowired
    public void setPopularPathwaysService(PopularPathwaysService popularPathwaysService) {
        this.popularPathwaysService = popularPathwaysService;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
     * get md5 code of upload file
     *
     * @param multipartFile the file from the Form sent by the user
     * @return md5 code String
     */
    public String getUploadFileMd5Code(MultipartFile multipartFile) throws IOException {
        String md5Ret = null;
        if (multipartFile != null && !multipartFile.isEmpty()) {
            md5Ret = DigestUtils.md5Hex(multipartFile.getInputStream());
        }
        return md5Ret;
    }
}
