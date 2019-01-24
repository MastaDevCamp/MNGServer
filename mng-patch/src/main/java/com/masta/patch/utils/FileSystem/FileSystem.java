package com.masta.patch.utils.FileSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.masta.patch.utils.FileSystem.model.Version;
import com.masta.patch.utils.FileSystem.model.FileEntry;
import com.masta.patch.utils.FileSystem.model.Views;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@Service
public class FileSystem {

//    private List<FileEntry> fileList;
    private List<ObjectWriter> fileList;
    int listIndex;
    int fileIndex;

    private ObjectMapper mapper;

    public FileSystem(final List<FileEntry> fileList, final ObjectMapper mapper) {
        this.mapper = mapper;
//        this.fileList = fileList;
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

        boolean forPatch = false;

        ObjectWriter viewWriter;
        if (forPatch) {
            viewWriter = mapper.writerWithView(Views.Patch.class);
        } else {
            viewWriter = mapper.writerWithView(Views.Full.class);
        }


        // set file type
        char fileType = file.isDirectory() ? 'D' : (file.getTotalSpace() != 0 ? 'F' : 'G');

        FileEntry fileEntry = FileEntry.builder()
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
                .diffType('x') //현재는 full file
                .build();


        viewWriter.writeValue(viewWriter);
        return fileEntry;
    }

    public List<FileEntry> getFileTreeList(String path) {
        listFilesForFolder(new File(path));


        return this.fileList;
    }
}
