package edu.prokhorovmax.bigdata.service;

import edu.prokhorovmax.bigdata.model.TestResult;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public interface TestService {

    TestResult runTest(int segmentSize, int maxStorageFileSize, String storagePath, String baseCopyFileName);

    List<TestResult> runMultipleTests();

}
