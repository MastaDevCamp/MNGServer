package com.masta.patch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.model.JsonType;
import com.masta.patch.utils.FileSystem.TypeConverter;
import com.masta.patch.utils.FileSystem.model.DirEntry;
import com.masta.patch.utils.HttpConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AdminClientService {

    private TypeConverter typeConverter;
    private HttpConnection httpConnection;
    private VersionMapper versionMapper;

    public AdminClientService(final TypeConverter typeConverter, final HttpConnection httpConnection,
                              final VersionMapper versionMapper) {
        this.typeConverter = typeConverter;
        this.httpConnection = httpConnection;
        this.versionMapper = versionMapper;
    }

    public DefaultRes getAllVersionList() {
        List<VersionLog> versionLogs = versionMapper.getAllVersionList();
        if (versionLogs != null)
            return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_VERSION, versionLogs);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.EMPTY_VERSION);
    }

    public DefaultRes getFullJsonContent(final String inputUrl, final JsonType type) throws Exception {
        String urlpath = "http://aws.nage.wo.tc:8021/" + inputUrl; //properties 속성으로 바꾸기

        String jsonContent = httpConnection.readResponse(urlpath);

        List<String> stringList = null;

        ObjectMapper objectMapper = new ObjectMapper();
        if (type == JsonType.FULL) {
            DirEntry dirEntry = objectMapper.readValue(jsonContent, DirEntry.class);
            stringList = typeConverter.makeFileList(dirEntry);
        } else if (type == JsonType.PATCH) {
            if (jsonContent.equals("null")) {
                return DefaultRes.res(StatusCode.CONFLICT, ResponseMessage.VERSION_ERROR);
            }
            stringList = objectMapper.readValue(jsonContent, List.class);
        }
        Collections.sort(stringList);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_VERSION, stringList);
    }

}
