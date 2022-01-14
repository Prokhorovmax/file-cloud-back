package edu.prokhorovmax.bigdata.rest;

import edu.prokhorovmax.bigdata.model.File;
import edu.prokhorovmax.bigdata.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@ConditionalOnExpression("${cloud.endpoint.enabled:true}")
@Api(description = "API работы с приложением в облаке")
@RequestMapping("/cloud-endpoint")
@RestController
public class CloudEndpoint {

    private final FileService fileService;

    public CloudEndpoint(@Lazy FileService fileService) {
        this.fileService = fileService;
    }

    @ApiOperation(value = "Загрузить файл")
    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean upload(
            @ApiParam(value = "Файл", required = true)
            @RequestParam("file") MultipartFile file) {
        fileService.uploadFile(file);
        return true;
    }

    @ApiOperation(value = "Загрузить файлы")
    @PostMapping(value = "/upload-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean uploadFile(
            @ApiParam(value = "Файлы", required = true)
            @RequestParam("files") List<MultipartFile> files) {
        fileService.uploadFiles(files);
        return true;
    }

    @ApiOperation(value = "Скачать файл")
    @GetMapping("/download-file/{fileId:.+}")
    public ResponseEntity<Resource> downloadFile(
            @ApiParam(value = "Идентификатор файла", required = true)
            @PathVariable long fileId) throws IOException {
        Resource resource = fileService.downloadFile(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileService.getFileName(fileId) + "\"")
                .body(resource);
    }

    @ApiOperation(value = "Удалить файл")
    @GetMapping("/delete-file/{fileId:.+}")
    public boolean deleteFile(
            @ApiParam(value = "Идентификатор файла", required = true)
            @PathVariable long fileId) throws IOException {
        fileService.deleteFile(fileId);
        return true;
    }

    @ApiOperation(value = "Получить список файлов")
    @GetMapping("/file-list")
    public List<File> getFileList() {
        return fileService.getFileList();
    }

}

