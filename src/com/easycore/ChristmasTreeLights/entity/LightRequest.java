package com.easycore.ChristmasTreeLights.entity;


import com.easycore.ChristmasTreeLights.helper.Utils;

import java.awt.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LightRequest implements Serializable {

    private Color color;

    private String colorType;

    private OffsetDateTime created;

    private OffsetDateTime displayed;

    public void setColor(String color) {
        try {
            this.color = Color.decode(color);
        } catch (NumberFormatException ignored) {
        }

    }

    public void setCreated(String created) {
        try {
            this.created = OffsetDateTime.parse(created, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException ignored) {
        }

    }

    public void setDisplayed(String displayed) {
        try {
            this.displayed = OffsetDateTime.parse(displayed, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException ignored) {
        }
    }

    public void setColorType(String colorType) {
        this.colorType = colorType;
    }

    public String getColor() {
        if (color == null) {
            return null;
        } else {
            return Utils.colorToHex(color);
        }
    }

    public String getCreated() {
        if (created == null) {
            return null;
        } else {
            return created.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
    }

    public String getDisplayed() {
        if (displayed == null) {
            return null;
        } else {
            return displayed.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
    }

    public String getColorType() {
        return colorType;
    }

    public Color color() {
        return color;
    }

    public OffsetDateTime created() {
        return created;
    }

    public OffsetDateTime displayed() {
        return displayed;
    }

    public boolean defined() {
        return "defined".equals(colorType);
    }

    public boolean shouldBeDisplayed() {
        return color != null && created != null && displayed == null;
    }

    public void setDisplayedNow() {
        this.displayed = OffsetDateTime.now();
    }


}
