package ru.ifmo.collectionmanagingservice.dto;

import jakarta.xml.bind.annotation.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@XmlRootElement(name = "filters")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilterMap {

    @XmlElement(name = "entry")
    private List<FilterEntry> entries = new ArrayList<>();

    public FilterMap(Map<String, String> map) {
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                entries.add(new FilterEntry(entry.getKey(), entry.getValue()));
            }
        }
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        if (entries != null) {
            for (FilterEntry entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlRootElement(name = "entry")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class FilterEntry {
        @XmlElement(required = true)
        private String key;

        @XmlElement
        private String value;
    }
}