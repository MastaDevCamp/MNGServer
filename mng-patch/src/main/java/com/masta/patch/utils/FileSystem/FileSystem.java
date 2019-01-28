package com.masta.patch.utils.FileSystem;

import com.masta.patch.utils.FileSystem.model.FileEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
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



    public List<FileEntry> getFileTreeList(String path) {
        fileList.clear();
        this.listIndex = 1;
        this.fileIndex = 1;
        listFilesForFolder(new File(path));
        return this.fileList;
//        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_JSON_FILE, this.fileList);
    }
}
