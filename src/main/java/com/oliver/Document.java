package com.oliver;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Author: Oliver
 */
@Slf4j
@Data
@NoArgsConstructor
public class Document {
    String name;
    String description;// (#2.3 format: max length 25, don't truncate any words unless the first word > 25, display "..." if truncated(these 3 do not count as part of 25))
    String createdBy;// #1.1. Group by document.createdBy, sort ascending.
    String lastModifiedBy;
    Long sizeInBytes;// format to 50 mb, 900 k, 342 bytes
    Long createdTime;// #1.2. sort ascending; (#2.2 format: yyyy-MM-dd)
    Long modifiedTime;// (#2.2 format: yyyy-MM-dd)

    /**
     * constructor
     *
     * @param name
     * @param description
     * @param createdBy
     * @param sizeInBytes
     * @param createdTime
     * @param modifiedTime
     */
    public Document(String createdBy, String name, String description, Long sizeInBytes, Long createdTime, Long modifiedTime) {
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.sizeInBytes = sizeInBytes;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

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

    public static void main(String[] args) {
        Document document = new Document();
        document.test();
    }

    private void test() {
        System.out.println("Begin ---------------------- !");
        List<Document> lst = new LinkedList<>();
        DateTimeFormatter formatterYYYYMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CANADA);
        ZoneOffset zoneOffsetToronto = ZoneOffset.of("-05:00");
        log.debug("new document = {}", new Document("Andy Andrews", "Bobby Timmons Biography", "An exhaustive look at the ...", parseSizeString2Long("233 mb"), parseDateTimeString2Long("2013-05-09", formatterYYYYMMdd, zoneOffsetToronto), parseDateTimeString2Long("2013-05-14", formatterYYYYMMdd, zoneOffsetToronto)));
        lst.add(new Document("Andy Andrews", "Bobby Timmons Biography", "An exhaustive look at the ...", parseSizeString2Long("233 mb"), parseDateTimeString2Long("2013-05-09", formatterYYYYMMdd, zoneOffsetToronto), parseDateTimeString2Long("2013-05-14", formatterYYYYMMdd, zoneOffsetToronto)));
        log.debug("lst = {}", lst);
        printDocumentsReport(lst);
        System.out.println("End ------------------------ !");
    }

    private static long parseDateTimeString2Long(String dateTimeString, DateTimeFormatter formatter, ZoneOffset zoneOffset) {
        return LocalDateTime.of(LocalDate.parse("2013-05-09", formatter), LocalTime.of(0, 0)).toInstant(zoneOffset).toEpochMilli();
    }

    private static final Map<String, Long> map4ParsingSize;

    static {
        map4ParsingSize = new HashMap<>();
        map4ParsingSize.put("bytes", 1L);
        map4ParsingSize.put("k", 1024L);
        map4ParsingSize.put("mb", 1048576L);
        map4ParsingSize.put("gb", 1073741824L);
        map4ParsingSize.put("tb", 1099511627776L);
        map4ParsingSize.put("pb", 1125899906842624L);
        map4ParsingSize.put("eb", 1152921504606846976L);
//        map4ParsingSize.put("Zettabyte", 1180591620717411303424L);
//        map4ParsingSize.put("Yottabyte", 1208925819614629174706176L);
    }

    public enum Sizes {
        bytes, k, mb, gb, tb, pb, eb
    }

    /**
     * parse String represented size 2 long represented in the unit of Byte.
     *
     * @param size
     * @return
     */
    private static long parseSizeString2Long(String size) {
        // Validate preconditions
        Validate.notBlank(size);
        String[] s = size.trim().split(" ");
        Validate.isTrue(s.length == 2);
        Long resultLong = Long.valueOf(s[0]) * map4ParsingSize.get(s[1]);
        Validate.notNull(resultLong);
        return resultLong.longValue();
    }

    /**
     * Prints a report of the list of documents in the following format:
     * <p>
     * #1
     * Group by document.createdBy
     * Sort the groups using document.createdBy ascending, case insensitive
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
    public void printDocumentsReport(List<Document> documents) {
        // Validate preconditions
        Validate.notEmpty(documents);

        log.debug("documents = {}", documents);
        Map<String, List<Document>> mapString2Documents = new HashMap<>();
        for (Document document : documents) {
            String key = document.getCreatedBy();
            if (!mapString2Documents.containsKey(key)) {
                mapString2Documents.put(key, new LinkedList<>());
            }
        }

        log.debug("mapString2Documents = {}", mapString2Documents);
    }
}
