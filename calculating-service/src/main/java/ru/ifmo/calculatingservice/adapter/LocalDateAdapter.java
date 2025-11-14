package ru.ifmo.calculatingservice.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal(String v) {
        if (v == null) {
            return null;
        }
        String t = v.trim();
        if (t.isEmpty()) {
            return null;
        }
        return LocalDate.parse(t);
    }

    @Override
    public String marshal(LocalDate v) {
        return v != null ? v.toString() : null;
    }
}
