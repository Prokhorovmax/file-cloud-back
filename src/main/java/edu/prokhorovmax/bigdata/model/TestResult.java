package edu.prokhorovmax.bigdata.model;


public class TestResult {

    private int segmentSize;
    private int maxStorageFileSize;

    private long fullTime;
    private long uploadTime;
    private long downloadTime;
    private double mistakePercent;

    public TestResult(int segmentSize,
                      int maxStorageFileSize,
                      long fullTime,
                      long uploadTime,
                      long downloadTime,
                      double mistakePercent) {
        this.segmentSize = segmentSize;
        this.maxStorageFileSize = maxStorageFileSize;
        this.fullTime = fullTime;
        this.uploadTime = uploadTime;
        this.downloadTime = downloadTime;
        this.mistakePercent = mistakePercent;
    }

    public TestResult() {

    };

    public int getSegmentSize() {
        return segmentSize;
    }

    public void setSegmentSize(int segmentSize) {
        this.segmentSize = segmentSize;
    }

    public int getMaxStorageFileSize() {
        return maxStorageFileSize;
    }

    public void setMaxStorageFileSize(int maxStorageFileSize) {
        this.maxStorageFileSize = maxStorageFileSize;
    }

    public long getFullTime() {
        return fullTime;
    }

    public void setFullTime(long fullTime) {
        this.fullTime = fullTime;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public long getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(long downloadTime) {
        this.downloadTime = downloadTime;
    }

    public double getMistakePercent() {
        return mistakePercent;
    }

    public void setMistakePercent(double mistakePercent) {
        this.mistakePercent = mistakePercent;
    }
}
