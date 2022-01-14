package edu.prokhorovmax.bigdata.model;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "hash")
public class Hash {

    @Id
    @Column(name = "hash")
    private byte[] hash;

    @Column(name = "file_name")
    private String fileName;


    @Column(name = "line_number")
    private int lineNumber;

    @Column(name = "count")
    private int count;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Hash)) {
            return false;
        }
        Hash anotherInstance = (Hash) obj;
        return Arrays.equals(this.hash, anotherInstance.hash) &&
                Objects.equals(this.fileName, anotherInstance.fileName) &&
                Objects.equals(this.lineNumber, anotherInstance.lineNumber) &&
                this.count == anotherInstance.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }

    @Override
    public String toString() {
        return "Hash{" +
                "hash=" + '{' + Arrays.toString(hash) + '}' +
                ", fileName=" + '\'' + fileName + '\'' +
                ", lineNumber=" + lineNumber +
                ", count=" + count +
                "}";

    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void increaseCount() {
        this.count++;
    }

    public void decreaseCount() {
        this.count--;
    }
}
