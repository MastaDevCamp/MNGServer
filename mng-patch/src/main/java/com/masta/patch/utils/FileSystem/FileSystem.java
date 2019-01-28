package com.masta.patch.utils.FileSystem;

import com.masta.patch.utils.FileSystem.model.FileEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.List;

@Slf4j
@Service
public class FileSystem {

    private List<FileEntry> fileList;
    int listIndex;
    int fileIndex;



//    private ObjectMapper mapper;

    public FileSystem(final List<FileEntry> fileList) {
        this.fileList = fileList;
    }

    public void listFilesForFolder(final File folder) {

        for (final File fileEntry : folder.listFiles()) {
            fileList.add(getFileEntry(fileEntry));
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            }
        }
    }


    /**
     * @param file
     * @return FileEntrey Filled fields.
     */
//    @JsonView(Views.Patch.class)
    public FileEntry getFileEntry(File file) {
        // set file type
        char fileType = file.isDirectory() ? 'D' : (file.getTotalSpace() != 0 ? 'F' : 'G');

        FileEntry fileEntry = FileEntry.builder()
                .listIndex(listIndex++)
                .type(fileType)
                .path(file.getPath())
                .fileIndex(fileType != 'D' ? fileIndex++ : 0)
                .compress("gzip")
                .originalSize((int) file.length())
                .compressSize((int) file.length())
                .originalHash("gieaorngoiarengionraeoigneariognoierango")
                .compressHash("gig34ng90w43ng09qnero903oigrs9g0540p9roe")
                .diffType('x') //patch
                .nowVersion("0.1.0") //full
                .fromVersion("0.1.0") //patch
                .toVersion("0.1.2") //patch
                .build();

        return fileEntry;
    }

    public String getMD5Hash(File file) throws Exception {
        String md5 = "";

        byte[] fileByte = new byte[20];

        if (file.length() > 20) {
            byte[] last = new byte[10];
            byte[] first = new byte[10];

            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.read(first, 0, 10);
            raf.seek(file.length() - 10);
            raf.read(last, 0, 10);

            // combine byte[]
            System.arraycopy(first, 0, fileByte, 0, first.length);
            System.arraycopy(last, 0, fileByte, first.length, last.length);
        } else {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.read(fileByte, 0, (int) file.length());
        }

        byte[] res = MessageDigest.getInstance("MD5").digest(fileByte);
        md5 = DatatypeConverter.printHexBinary(res);

        return md5;
    }


    public List<FileEntry> getFileTreeList(String path) {
        fileList.clear();
        this.listIndex = 1;
        this.fileIndex = 1;
        listFilesForFolder(new File(path));
        return this.fileList;
//        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_JSON_FILE, this.fileList);
    }
}
