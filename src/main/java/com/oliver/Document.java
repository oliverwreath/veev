package com.oliver;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


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

    static final String ZoneOffset_TORONTO = "-05:00";
    static final String DEFAULT_YYYYMMDD_PATTERN = "yyyy-MM-dd";
    static final String truncatedIndication = "...";

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
    public Document(final String createdBy, final String name, final String description, final Long sizeInBytes, final Long createdTime, final Long modifiedTime) {
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.sizeInBytes = sizeInBytes;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

    public Document(final DocumentFormatter documentFormatter, final String createdBy, final String name, final String description, final String sizeString, final String createdTime, final String modifiedTime) {
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.sizeInBytes = documentFormatter.parseSizeString2Long(sizeString);
        this.createdTime = documentFormatter.parseDateTimeString2Long(createdTime);
        this.modifiedTime = documentFormatter.parseDateTimeString2Long(modifiedTime);
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

    String toStringBeautify() {
        return "Document{" +
                "'" + name + '\'' +
                ",'" + formatDescription(description) + '\'' +
                "," + formatSize(sizeInBytes) +
                "," + formatTime(createdTime) +
                "," + formatTime(modifiedTime) +
                '}';
    }

    String formatDescription(final String description) {
        // Validate preconditions
        if (StringUtils.isBlank(description)) {
            return "";
        }

        // DON't truncate
        if (description.length() <= 25) {
            return description;
        }
        // DO truncate
        boolean isFirstWordTooLong = true;
        for (int i = 0; i < 25; i++) {
            if (' ' == description.charAt(i)) {
                isFirstWordTooLong = false;
                break;
            }
        }

        if (isFirstWordTooLong) {
            // chop the word if necessary
            return description.substring(0, 25) + truncatedIndication;
        } else {
            // MUST truncate BUT don't chop last word.
            int endIndex = 25;
            while (endIndex < description.length()) {
                if (' ' == description.charAt(endIndex)) {
                    break;
                }
                endIndex++;
            }

            String truncated = description.substring(0, endIndex);
            return truncated + truncatedIndication;
        }
    }

    String formatTime(final Long timeToFormat, final String dateTimePattern) {
        // Validate preconditions
        Validate.notBlank(dateTimePattern);
        if (timeToFormat == null) {
            return "";
        }

        SimpleDateFormat df2 = new SimpleDateFormat(dateTimePattern);
        String dateText = df2.format(new Date(timeToFormat));
        log.debug("formatTime: {} to {}", timeToFormat, dateText);
        return dateText;
    }

    String formatTime(final Long timeToFormat) {
        // Validate preconditions
        if (timeToFormat == null) {
            return "";
        }

        SimpleDateFormat df2 = new SimpleDateFormat(DEFAULT_YYYYMMDD_PATTERN);
        String dateText = df2.format(new Date(timeToFormat));
        log.debug("formatTime: {} to {}", timeToFormat, dateText);
        return dateText;
    }

    private static final List<Pair<Long, String>> sizeList;

    static {
        sizeList = new LinkedList<>();
//        sizeList.add(Pair.of(1208925819614629174706176L, Sizes.yb.toString()));
//        sizeList.add(Pair.of(1180591620717411303424L, Sizes.zb.toString()));
//        sizeList.add(Pair.of(1152921504606846976L, Sizes.eb.toString()));
        sizeList.add(Pair.of(1125899906842624L, Sizes.pb.toString()));
        sizeList.add(Pair.of(1099511627776L, Sizes.tb.toString()));
        sizeList.add(Pair.of(1073741824L, Sizes.gb.toString()));
        sizeList.add(Pair.of(1048576L, Sizes.mb.toString()));
        sizeList.add(Pair.of(1024L, Sizes.k.toString()));
        sizeList.add(Pair.of(1L, Sizes.bytes.toString()));
    }

    String formatSize(final Long sizeInBytes) {
        // Validate preconditions
        if (sizeInBytes == null) {
            return "";
        }

        for (Pair<Long, String> longStringPair : sizeList) {
            if (sizeInBytes > longStringPair.getLeft()) {
                return sizeInBytes / longStringPair.getLeft() + " " + longStringPair.getRight();
            }
        }

        return "0 " + Sizes.bytes;
    }

    @NoArgsConstructor
    protected static class DocumentFormatter {
        private DateTimeFormatter formatterYYYYMMdd = DateTimeFormatter.ofPattern(DEFAULT_YYYYMMDD_PATTERN, Locale.CANADA);
        private ZoneOffset zoneOffsetToronto = ZoneOffset.of(ZoneOffset_TORONTO);
        private static Map<String, Long> map4ParsingSize = new HashMap<>();

        static {
            map4ParsingSize.put(Sizes.bytes.toString(), 1L);
            map4ParsingSize.put(Sizes.k.toString(), 1024L);
            map4ParsingSize.put(Sizes.mb.toString(), 1048576L);
            map4ParsingSize.put(Sizes.gb.toString(), 1073741824L);
            map4ParsingSize.put(Sizes.tb.toString(), 1099511627776L);
            map4ParsingSize.put(Sizes.pb.toString(), 1125899906842624L);
//            map4ParsingSize.put(Sizes.eb.toString(), 1152921504606846976L);
//        map4ParsingSize.put(Sizes.zb.toString(), 1180591620717411303424L);
//        map4ParsingSize.put(Sizes.yb.toString(), 1208925819614629174706176L);
        }

        public DocumentFormatter(final DateTimeFormatter formatterYYYYMMdd, final ZoneOffset zoneOffsetToronto) {
            this.formatterYYYYMMdd = formatterYYYYMMdd;
            this.zoneOffsetToronto = zoneOffsetToronto;
        }

        public DocumentFormatter(final DateTimeFormatter formatterYYYYMMdd, final ZoneOffset zoneOffsetToronto, final Map<String, Long> map4ParsingSize) {
            this.formatterYYYYMMdd = formatterYYYYMMdd;
            this.zoneOffsetToronto = zoneOffsetToronto;
            this.map4ParsingSize = map4ParsingSize;
        }

        private Long parseDateTimeString2Long(final String dateTimeString, final DateTimeFormatter formatter, final ZoneOffset zoneOffset) {
            // Validate preconditions
            if (StringUtils.isBlank(dateTimeString)) {
                return null;
            }

            try {
                return LocalDateTime.of(LocalDate.parse(dateTimeString, formatter), LocalTime.of(0, 0)).toInstant(zoneOffset).toEpochMilli();
            } catch (Exception e) {
                return null;
            }
        }

        Long parseDateTimeString2Long(final String dateTimeString) {
            // Validate preconditions
            if (StringUtils.isBlank(dateTimeString)) {
                return null;
            }

            try {
                return LocalDateTime.of(LocalDate.parse(dateTimeString, formatterYYYYMMdd), LocalTime.of(0, 0)).toInstant(zoneOffsetToronto).toEpochMilli();
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * parse String represented size 2 long represented in the unit of Byte.
         *
         * @param size
         * @return
         */
        Long parseSizeString2Long(final String size) {
            // Validate preconditions
            if (StringUtils.isBlank(size)) {
                return null;
            }

            String[] s = size.trim().split(" ");
            Validate.isTrue(s.length == 2);
            Long resultLong = Long.valueOf(s[0]) * map4ParsingSize.get(s[1]);
            Validate.notNull(resultLong);
            return resultLong.longValue();
        }
    }

    enum Sizes {
        bytes, k, mb, gb, tb, pb
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
