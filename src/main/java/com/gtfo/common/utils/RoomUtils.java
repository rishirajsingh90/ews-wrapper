package com.gtfo.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

public final class RoomUtils {

    public static Optional<String> getMeetingLocation(String location, List<String> allRooms) {
        return allRooms.stream().filter(room -> StringUtils.containsIgnoreCase(location, room)).findFirst();
    }
}
