package bsep.sc.SiemCenter.service;

import bsep.sc.SiemCenter.exception.ApiBadRequestException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class DateService {

    public Date getDate(String dateStr, String timezone) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new ApiBadRequestException("Invalid date format/timezone.");
        }
    }

    public String getString(Date date, String timezone) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        return sdf.format(date);
    }

    public Date getMinDate() {
        return new Date(0);
    }

    public Date getMaxDate() {
        return new Date(Long.MAX_VALUE);
    }

}
