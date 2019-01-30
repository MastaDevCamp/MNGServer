package com.masta.patch.utils.FileSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masta.patch.utils.FileSystem.model.DirEntry;
import com.masta.patch.utils.FileSystem.model.FileEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DiffSystem {

    private DirEntry resultDir = new DirEntry();
    private boolean isRoot = true;

    public DirEntry makePatchJson(String prevJsonPath, String nextJsonPath) {
        //DirEntry resultDir = new DirEntry();
        DirEntry prevRoot = readVersionJson(prevJsonPath);
        DirEntry nextRoot = readVersionJson(nextJsonPath);

        diff(prevRoot, nextRoot);

        return resultDir;
    }

    public void diff(DirEntry prevDir, DirEntry nextDir) {
        try {
            DirEntry bPrevDir = prevDir;
            DirEntry bNextDir = nextDir;

            DirEntry patchDir = new DirEntry();
            DirEntry notPatchDir = new DirEntry();

            diffDir(prevDir, nextDir, patchDir, notPatchDir);
            for (final DirEntry dirEntry : notPatchDir.dirEntryList) {
                prevDir = bPrevDir;
                nextDir = bNextDir;
                prevDir = prevDir.dirEntryList.stream().filter(d -> d.getPath().equals(dirEntry.getPath())).findAny().orElse(null);
                nextDir = nextDir.dirEntryList.stream().filter(d -> d.getPath().equals(dirEntry.getPath())).findAny().orElse(null);
                diff(prevDir, nextDir);
            }
//            patchDir.clearList();
//            notPatchDir.clearList();
        } catch (Exception e) {
            return;
        }

    }

    public void newDir(DirEntry newDir, DirEntry patchDir) {
        for (final FileEntry fileEntry : newDir.fileEntryList) {
            fileEntry.setDiffType('C');
            patchDir.fileEntryList.add(fileEntry);
        }

        for (final DirEntry dirEntry : newDir.dirEntryList) {
            dirEntry.setDiffType('C');
            patchDir.dirEntryList.add(dirEntry);
            newDir(dirEntry, patchDir);
        }
    }

    public void diffDir(DirEntry prevDir, DirEntry nextDir, DirEntry patchDir, DirEntry notPatchDir) {
        Map<String, Character> commonDirs = getDirRetainAll(prevDir, nextDir);
        Map<FileEntry, Character> commonFiles = getFileRetainAll(prevDir, nextDir);

        for (String path : commonDirs.keySet()) {
            DirEntry tempDir = prevDir.dirEntryList.stream().filter(d -> d.getPath().equals(path)).findAny().orElse(null);
            if (tempDir == null)
                tempDir = nextDir.dirEntryList.stream().filter(d -> d.getPath().equals(path)).findAny().orElse(null);

            if (commonDirs.get(path) != 'S') {
                tempDir.setAllDiffType(commonDirs.get(path));
                resultDir.dirEntryList.add(tempDir);
                patchDir.dirEntryList.add(tempDir);
            } else {
                tempDir.setDiffType(commonDirs.get(path));
                notPatchDir.dirEntryList.add(tempDir);
            }
        }

        if (isRoot) {
            for (FileEntry file : commonFiles.keySet()) {
                if (commonFiles.get(file) != 'S') {
                    file.setDiffType(commonFiles.get(file));
                    resultDir.fileEntryList.add(file);
                    //patchDir.fileEntryList.add(file);
                }
            }
            isRoot = false;
        }

    }


    /**
     * get diff log in prevDir and nextDir
     */
    public Map<String, Character> getDirRetainAll(DirEntry prevDir, DirEntry nextDir) {

        // tracking directories changes
        Map<String, Character> retainDirAll = new HashMap<>();
        for (final DirEntry dir : prevDir.dirEntryList) {
            retainDirAll.put(dir.getPath(), 'D');
        }
        for (final DirEntry dir : nextDir.dirEntryList) {
            if (retainDirAll.get(dir.getPath()) == null) { // dir is not in commonDir
                retainDirAll.put(dir.getPath(), 'C');
            } else { // dir is in commonDir
                retainDirAll.put(dir.getPath(), 'S');  // 'S' not changed
            }
        }
        return retainDirAll;
    }

    public Map<FileEntry, Character> getFileRetainAll(DirEntry prevDir, DirEntry nextDir) {

        // tracking files changes
        Map<FileEntry, Character> retainFileAll = new HashMap<>();
        for (final FileEntry file : prevDir.fileEntryList) {
            retainFileAll.put(file, 'D');
        }
        log.info(retainFileAll.toString());
        for (final FileEntry file : nextDir.fileEntryList) {
            FileEntry commonFile = findFile(retainFileAll, file);
            if (commonFile == null) {
                FileEntry fileInPrev = prevDir.findFileEntry(file.getPath());
                if (fileInPrev == null) {   // file is not in prev but in next
                    retainFileAll.put(file, 'C');
                } else { // file is in prev and next but is not same
                    retainFileAll.put(file, 'U');
                }
            } else {
                retainFileAll.remove(commonFile);
                retainFileAll.put(file, 'S');
            }
        }

        return retainFileAll;
    }

    public FileEntry findFile(Map<FileEntry, Character> map, FileEntry file) {
        for (FileEntry fileEntry : map.keySet()) {
            if (checkSameFile(fileEntry, file) != 'x') {
                return fileEntry;
            }
        }
        return null;
    }

    public FileEntry findFile(Map<FileEntry, Character> map, String path) {
        for (FileEntry fileEntry : map.keySet()) {
            if (fileEntry.getPath().equals(path)) {
                return fileEntry;
            }
        }
        return null;
    }

    public char checkSameFile(FileEntry file1, FileEntry file2) {
        if (file1.getPath().equals(file2.getPath())) {
            if (file1.getOriginalHash().equals(file2.getPath())) {
                return 'S';
            } else {
                return 'U';
            }
        } else {
            return 'x';
        }
    }


    public DirEntry readVersionJson(String jsonPath) {
        DirEntry dirEntry = new DirEntry();

        ObjectMapper mapper = new ObjectMapper();
        try {
            dirEntry = mapper.readValue(new File(jsonPath), DirEntry.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return dirEntry;
    }

}
