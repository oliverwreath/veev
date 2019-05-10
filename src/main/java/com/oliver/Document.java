package com.oliver;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Author: Oliver
 */
@Slf4j
@Data
@NoArgsConstructor
public class Document {
    // fields
    String name;
    String description;// (#2.3 format: max length 25, don't truncate any words unless the first word > 25, display "..." if truncated(these 3 do not count as part of 25))
    String createdBy;// #1.1. Group by document.createdBy, sort ascending.
    String lastModifiedBy;
    Long sizeInBytes;// format to 50 mb, 900 k, 342 bytes
    Long createdTime;// #1.2. sort ascending; (#2.2 format: yyyy-MM-dd)
    Long modifiedTime;// (#2.2 format: yyyy-MM-dd)
    // Utility
    DocumentFormatter documentFormatter;

    /**
     * constructors
     *
     * @param name
     * @param description
     * @param createdBy
     * @param sizeInBytes
     * @param createdTime
     * @param modifiedTime
     */
    public Document(final String createdBy, final String name, final String description, final Long sizeInBytes, final Long createdTime, final Long modifiedTime) {
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.sizeInBytes = sizeInBytes;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

    public Document(final DocumentFormatter documentFormatter, final String createdBy, final String name, final String description, final String sizeString, final String createdTime, final String modifiedTime) {
        this.documentFormatter = documentFormatter;
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.sizeInBytes = DocumentFormatter.parseSize(sizeString);
        this.createdTime = documentFormatter.parseTime(createdTime);
        this.modifiedTime = documentFormatter.parseTime(modifiedTime);
    }

    /**
     * toStrings
     *
     * @return
     */
    @Override
    public String toString() {
        return "Document{" +
                "'" + createdBy + '\'' +
                ",'" + name + '\'' +
                ",'" + description + '\'' +
                "," + sizeInBytes +
                "," + createdTime +
                "," + modifiedTime +
                '}';
    }

    String toStringBeautify() {
        return "Document{" +
                "'" + name + '\'' +
                ",'" + DocumentFormatter.formatDescription(description) + '\'' +
                "," + DocumentFormatter.formatSize(sizeInBytes) +
                "," + documentFormatter.formatTime(createdTime) +
                "," + documentFormatter.formatTime(modifiedTime) +
                '}';
    }

    /**
     * Prints a report of the list of documents in the following format:
     * <p>
     * #1
     * Group by document.createdBy
     * #1.2.
     * Sort the groups using document.createdBy ascending, case insensitive
     * #1.3.
     * Sort each sub list of documents by document.createdTime ascending
     * <p>
     * #2
     * Format the output of document.size to be a more friendly format. Ex.  50 mb, 900 k, 342 bytes, etc...
     * Format the dates using the format: yyyy-MM-dd
     * #2.3
     * Format the output of document.description such that
     * - no more than the first 25 characters of the description are displayed
     * - don't truncate any words unless the first word is longer than 25 characters
     * - display "..." at the end of the description to indicate that it has been truncated
     * (these three characters do not count as part of the 25 character limit)
     * <p>
     * Example:
     * Andy Andrews
     * "Bobby Timmons Biography","An exhaustive look at the ...",233 mb,2013-05-09,2013-05-14
     * "Apple Sauce","Study of apple sauces.”,87 gb,2013-05-10,2013-05-10
     * "Zed","All matters, A to Zed”,924 k,2013-05-12,2013-05-12
     * Janet Smith
     * "Xray","How the Xray shows your ...",48 mb,2010-10-22,2010-12-02
     * "Computers","Inventory list of ...",423 bytes,2013-03-01,2013-02-17
     *
     * @param documents not null
     */
    public void printDocumentsReport(final List<Document> documents) {
        log.info("Begin printDocumentsReport() -------------- ");
        System.out.println(printDocumentsReportHelper(documents).toString());
        log.info("End   printDocumentsReport() -------------- ");
    }

    /**
     * that original function returns void which is NOT test friendly so here's a helper for quality assurance
     *
     * @param documents
     */
    public StringBuilder printDocumentsReportHelper(final List<Document> documents) {
        // Validate preconditions
        if (CollectionUtils.isEmpty(documents)) {
            return new StringBuilder();
        }

        // prepare the map
        log.debug("\n\nprintDocumentsReport(): documents = {}\n", documents);
        Map<String, PriorityQueue<Document>> mapString2Documents = new HashMap<>();
        for (Document document : documents) {
            String key = document.getCreatedBy();
            if (!mapString2Documents.containsKey(key)) {
                // #1.3. Sort each sub list of documents by document.createdTime ascending
                mapString2Documents.put(key, new PriorityQueue<>(Comparator.comparingLong(o -> o.createdTime)));
            }
            mapString2Documents.get(key).add(document);
        }

        // populate the stringBuilder
        StringBuilder stringBuilder = new StringBuilder();
        log.debug("mapString2Documents.keySet().size() = {}", mapString2Documents.keySet().size());
        // #1.2. Sort the groups using document.createdBy ascending, case insensitive
        List<String> keys = mapString2Documents.keySet().stream().sorted(Comparator.comparing(String::toLowerCase)).collect(Collectors.toList());
        for (String key : keys) {
            log.debug("key = {}; \nmapString2Documents.get(key).size() = {}", key, mapString2Documents.get(key).size());
            stringBuilder.append(key).append('\n');
            for (Document document : mapString2Documents.get(key)) {
                log.debug("document = {}", document);
                stringBuilder.append(document.toStringBeautify()).append('\n');
            }
        }
        return stringBuilder;
    }
}
