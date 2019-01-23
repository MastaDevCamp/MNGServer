package com.masta.patch.utils.FileSystem;

import com.masta.patch.utils.FileSystem.model.DiffFileEntry;
import com.masta.patch.utils.FileSystem.model.Version;
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

    public FileSystem(final List<FileEntry> fileList) {

        this.fileList = fileList;
        this.listIndex = 1;
        this.fileIndex = 1;
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
    public FileEntry getFileEntry(File file) {

        // set file type
        char fileType = file.isDirectory() ? 'D' : (file.getTotalSpace() != 0 ? 'F' : 'G');


        FileEntry fileEntry = DiffFileEntry.builder()
                .listIndex(listIndex++)
                .type(fileType)
                .path(file.getPath())
                .fileIndex(fileType != 'D' ? fileIndex++ : 0)
                .compress("gzip")
                .version(Version.builder().from("0.1.0").to("0.1.0").build())
                .originalSize((int) file.length())
                .compressSize((int) file.length())
                .originalHash("gieaorngoiarengionraeoigneariognoierango")
                .compressHash("gig34ng90w43ng09qnero903oigrs9g0540p9roe")
                .build();

        return fileEntry;
    }

    public List<FileEntry> getFileTreeList(String path) {
        listFilesForFolder(new File(path));
        return this.fileList;
    }
}
