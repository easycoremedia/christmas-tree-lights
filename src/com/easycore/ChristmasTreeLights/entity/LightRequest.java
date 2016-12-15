package com.easycore.ChristmasTreeLights.entity;


import com.easycore.ChristmasTreeLights.helper.Utils;
import com.easycore.ChristmasTreeLights.led.LedController;

import java.awt.*;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

public class LightRequest implements Serializable {

    private Color color;

    private String colorType;

    private Date created;

    private Date displayed;

    private static final Logger logger = Logger.getLogger(LedController.class.getName());

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());

    public void setColor(String color) {
        try {
            this.color = Color.decode(color);
        } catch (NumberFormatException ignored) {
            logger.warning(String.format("Cannot decode color '%s'.", color));
        }

    }

    public void setCreated(String created) {
        try {
            this.created = dateFormatter.parse(created);
        } catch (ParseException ignored) {
            logger.warning(String.format("Cannot parse timestamp '%s'.", created));
        }

    }

    public void setDisplayed(String displayed) {
        try {
            this.displayed = dateFormatter.parse(displayed);
        } catch (ParseException ignored) {
            logger.warning(String.format("Cannot parse timestamp '%s'.", displayed));
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
            return dateFormatter.format(created);
        }
    }

    public String getDisplayed() {
        if (displayed == null) {
            return null;
        } else {
            return dateFormatter.format(displayed);
        }
    }

    public String getColorType() {
        return colorType;
    }

    public Color color() {
        return color;
    }

    public Date created() {
        return created;
    }

    public Date displayed() {
        return displayed;
    }

    public boolean defined() {
        return "defined".equals(colorType);
    }

    public boolean shouldBeDisplayed() {
        return (color != null || !defined()) && created != null && displayed == null;
    }

    public void setDisplayedNow() {
        this.displayed = new Date();
    }


}
