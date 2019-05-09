package com.oliver;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.oliver.Document.Sizes;

/**
 * Author: Oliver
 * <p>
 * formatters for setting values
 */
@Slf4j
@NoArgsConstructor
class DocumentFormatter {
    // constants
    private static final String DEFAULT_YYYYMMDD_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_ZoneId_TORONTO = "America/Toronto";
    // timeZoneSettings etc.
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_YYYYMMDD_PATTERN, Locale.CANADA);
    private ZoneId zoneId = ZoneId.of(DEFAULT_ZoneId_TORONTO);
    //    private ZoneOffset zoneOffsetToronto = ZoneOffset.of(DEFAULT_ZoneOffset_TORONTO);
    private static Map<String, Long> map4ParsingSize = new HashMap<>();

    static {
        map4ParsingSize.put(Sizes.bytes.toString(), 1L);
        map4ParsingSize.put(Sizes.k.toString(), 1024L);
        map4ParsingSize.put(Sizes.mb.toString(), 1048576L);
        map4ParsingSize.put(Sizes.gb.toString(), 1073741824L);
        map4ParsingSize.put(Sizes.tb.toString(), 1099511627776L);
        map4ParsingSize.put(Sizes.pb.toString(), 1125899906842624L);
//        map4ParsingSize.put(Sizes.eb.toString(), 1152921504606846976L);
//        map4ParsingSize.put(Sizes.zb.toString(), 1180591620717411303424L);
//        map4ParsingSize.put(Sizes.yb.toString(), 1208925819614629174706176L);
    }

    public DocumentFormatter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public DocumentFormatter(final DateTimeFormatter dateTimeFormatter, final ZoneId zoneId) {
        this.dateTimeFormatter = dateTimeFormatter;
        this.zoneId = zoneId;
    }

    private Long parseTime(final String dateTimeString, final DateTimeFormatter formatter, final ZoneOffset zoneOffset) {
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

    Long parseTime(final String dateTimeString) {
        // Validate preconditions
        if (StringUtils.isBlank(dateTimeString)) {
            return null;
        }

        try {
            LocalDate date = LocalDate.parse(dateTimeString);
            LocalDateTime localDateTime = LocalDateTime.of(date, LocalTime.MIN);
            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
            return zonedDateTime.toInstant().toEpochMilli();
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
    static Long parseSize(final String size) {
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

    // above are formatters for setting values
    // below are formatters for printing values
    // constants
    static final String truncatedIndication = "...";
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

    static String formatDescription(final String description) {
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

        return Instant.ofEpochMilli(timeToFormat).atZone(zoneId).format(dateTimeFormatter);
    }

    static String formatSize(final Long sizeInBytes) {
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
}
