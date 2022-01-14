package edu.prokhorovmax.bigdata.rest;

import edu.prokhorovmax.bigdata.model.File;
import edu.prokhorovmax.bigdata.model.TestResult;
import edu.prokhorovmax.bigdata.service.FileService;
import edu.prokhorovmax.bigdata.service.TestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ConditionalOnExpression("${endpoint.enabled:true}")
@Api(description = "API работы с приложением")
@RequestMapping("/endpoint")
@RestController
public class Endpoint {

    private final FileService fileService;

    @Value("${base.download.path}")
    private String baseDownloadPath;

    @Value("${base.files.path}")
    private String baseFilesPath;

    private final TestService testService;

    public Endpoint(@Lazy FileService fileService,
                    @Lazy TestService testService) {
        this.fileService = fileService;
        this.testService = testService;
    }

    @ApiOperation(value = "Загрузить файл по его имени")
    @PostMapping("/upload-file-by-name")
    public boolean create(
            @ApiParam(value = "Имя файла", required = true)
            @RequestParam String fileName) {
        fileService.uploadFile(baseFilesPath + fileName);
        return true;
    }

    @ApiOperation(value = "Скачать файл из хранилища")
    @GetMapping("/download-file-by-name")
    public boolean create(
            @ApiParam(value = "Имя файла", required = true)
            @RequestParam String fileName,
            @ApiParam(value = "Имя нового файла", required = true)
            @RequestParam String destination) {
        fileService.downloadFile(fileName, baseDownloadPath + destination);
        return true;
    }

    @ApiOperation(value = "Получить список файлов")
    @GetMapping("/file-list")
    public List<File> getFileList() {
        return fileService.getFileList();
    }

    @ApiOperation(value = "Запустить короткое тестирование прототипа системы хранения")
    @GetMapping("/run-test")
    public TestResult runTest(
            @ApiParam(value = "Размер сегмента", required = true)
            @RequestParam int segmentSize,
            @ApiParam(value = "Ограничение файла в хранилище", required = true)
            @RequestParam int maxFileSize,
            @ApiParam(value = "Базовое имя файлов", required = true)
            @RequestParam String baseFileName) {
        return testService.runTest(segmentSize, maxFileSize, "C:\\Users\\Max\\IdeaProjects\\BigData2\\BigData\\test\\storage_0\\", baseFileName);
    }

    @ApiOperation(value = "Запустить полное тестирование прототипа системы хранения")
    @GetMapping("/run-tests")
    public List<TestResult> runMultipleTests() {
        return testService.runMultipleTests();
    }
}
