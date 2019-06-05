package com.gilecode.xmx.smx.impl;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PortRangesParser {

    /**
     * Parses and validates the input string with the comma-separated ports and port ranges
     * @param str the input ranges, e.g. "1,3-5,10"
     * @return the port ranges as pairs of integers
     *
     * @throws IllegalArgumentException if the inpit string has invalid format
     */
    public List<Pair<Integer, Integer>> parsePortRanges(String str) throws IllegalArgumentException {
        List<Pair<Integer, Integer>> pairs = new ArrayList<>();
        Pattern patternRange = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");
        for (String rangeStr : str.split(",")) {
            rangeStr = rangeStr.trim();
            if (rangeStr.contains("-")) {
                Matcher m = patternRange.matcher(rangeStr);
                if (!m.matches()) {
                    throw new IllegalArgumentException("Invalid format of a ports range: " + rangeStr);
                }
                int lo = parsePortNum(m.group(1));
                int hi = parsePortNum(m.group(2));
                if (lo <= hi) {
                    pairs.add(Pair.of(lo, hi));
                } else {
                    throw new IllegalArgumentException("Invalid format of a ports range (expected lower boundary first): " + rangeStr);
                }
            } else {
                int num = parsePortNum(rangeStr);
                pairs.add(Pair.of(num, num));
            }
        }
        return pairs;
    }

    private int parsePortNum(String str) throws IllegalArgumentException {
        try {
            int num = Integer.parseInt(str);
            if (num < 0 || num > 65535) {
                throw new IllegalArgumentException("Valid ports range is 0-65535, but got " + str);
            }
            return num;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Expected an integer number as a port, but got " + str);
        }
    }
}
