package edu.prokhorovmax.bigdata.service;

import edu.prokhorovmax.bigdata.model.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class TestServiceImpl implements TestService {

    private static final Logger logger = LoggerFactory.getLogger(TestServiceImpl.class.getName());

    @Value("${test.start.segment.size:4}")
    private int segmentSize;

    @Value("${test.segment.size.multiplier:4}")
    private int segmentSizeMultiplier;

    @Value("${test.storage.file.size:512}")
    private int maxStorageFileSize;

    @Value("${test.storage.file.size.multiplier:2}")
    private int storageFileSizeMultiplier;

    @Value("${test.base.download.path}")
    private String baseDownloadPath;

    @Value("${test.base.files.path}")
    private String baseFilesPath;

    private final FileService fileService;
    private final FileHashService fileHashService;
    private final HashService hashService;

    public TestServiceImpl(@Lazy FileService fileService,
                           @Lazy FileHashService fileHashService,
                           @Lazy HashService hashService) {
        this.fileService = fileService;
        this.fileHashService = fileHashService;
        this.hashService = hashService;
    }

    @Override
    public TestResult runTest(int segmentSize, int maxStorageFileSize, String storagePath, String baseCopyFileName) {
        long startTime = System.currentTimeMillis();
        logger.info(String.format("Prototype test started with segment size = %d and max storage file size = %d bytes",
                segmentSize, maxStorageFileSize));

        // upload
        for (int i = 0; i < 10; i++) {
            String fileName = i + ".txt";
            fileService.uploadFile(baseFilesPath + fileName, segmentSize, maxStorageFileSize, storagePath);
        }
        long uploadEndTime = System.currentTimeMillis();
        long uploadTime = uploadEndTime - startTime;
        logger.info(String.format("Upload ended, elapsed time: %f sec", (double) uploadTime / 1000));

        // download
        Map<String, String> files = new HashMap<>();
        for (int i = 0; i < 10; i += 3) {
            String fileName = i + ".txt";
            String copyFileName = baseDownloadPath + baseCopyFileName + "_" + i + ".txt";
            fileService.downloadFile(fileName, copyFileName, segmentSize);
            files.put(fileName, copyFileName);
        }
        long downloadTime = System.currentTimeMillis() - uploadEndTime;
        logger.info(String.format("Download ended, elapsed time: %f sec", (double) downloadTime / 1000));
        long fullTime = System.currentTimeMillis() - startTime;

        // mistakes
        double mistakes = 0;
        double totalSymbols = 0;
        for (Map.Entry<String, String> entry : files.entrySet()) {
            try {
                byte[] original = Files.readAllBytes(Paths.get(baseFilesPath + entry.getKey()));
                byte[] copy = Files.readAllBytes(Paths.get(entry.getValue()));
                int originalLength = original.length;
                int copyLength = copy.length;
                totalSymbols += originalLength;
                if (Arrays.equals(original, copy)) {
                    continue;
                }
                int difference = originalLength - copyLength;
                int numberOfSymbols = originalLength;
                if (difference != 0) {
                    mistakes += Math.abs(difference);
                    numberOfSymbols = (difference < 0) ? originalLength : copyLength;
                }
                for (int i = 0; i < numberOfSymbols; i++) {
                    if (original[i] != copy[i]) {
                        mistakes++;
                    }
                }
            } catch (IOException ex) {
                logger.error("Error in file compare process", ex);
            }
        }
        double mistakePercent = mistakes / totalSymbols;
        logger.info(String.format("Total time elapsed time: %f sec", (double) fullTime / 1000));
        TestResult res = new TestResult(segmentSize,
                maxStorageFileSize,
                fullTime,
                uploadTime,
                downloadTime,
                mistakePercent);

        System.out.println();
        System.out.println("Segment size = " + segmentSize + "; max storage file size = " + maxStorageFileSize);
        System.out.println("Upload: " + res.getUploadTime() + "; download: " + res.getDownloadTime() +
                "; total: " + res.getFullTime() + "; mistake percent: " + res.getMistakePercent());
        System.out.println();

        return res;
    }

    @Override
    public List<TestResult> runMultipleTests() {
        List<TestResult> results = new ArrayList<>();
        int currentMaxStorageFileSize = maxStorageFileSize;
        for (int j = 0; j < 2; j++) {
            int currentSegmentSize = segmentSize;
            for (int i = 0; i < 14; i++) {
                String storagePath = (j == 0) ?
                        ("C:\\Users\\Max\\IdeaProjects\\BigData2\\BigData\\test\\storage_" + i + "\\") :
                        ("C:\\Users\\Max\\IdeaProjects\\BigData2\\BigData\\test\\storage_" + i + "_1\\");
                String copyFileName = "test_download_" + i + '_' + j;

                TestResult res = runTest(currentSegmentSize, currentMaxStorageFileSize, storagePath, copyFileName);
                results.add(res);

                System.out.println();
                System.out.println("Test " + i + '.' + j + ':');
                System.out.println("Segment size = " + currentSegmentSize + "; max storage file size = " + currentMaxStorageFileSize);
                System.out.println("Upload: " + res.getUploadTime() + "; download: " + res.getDownloadTime() +
                        "; total: " + res.getFullTime() + "; mistake percent: " + res.getMistakePercent());
                System.out.println();

                // clear db
                fileHashService.clear();
                fileService.clear();
                hashService.clear();

                fileService.testReset();
                currentSegmentSize *= segmentSizeMultiplier;
            }
            currentMaxStorageFileSize *= storageFileSizeMultiplier;
        }
        ExcelOutput.outputTestResults(results);
        return results;
    }
}
