package bsep.sc.SiemCenter.service;

import bsep.sc.SiemCenter.exception.ApiBadRequestException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DateService {

    public Date getDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new ApiBadRequestException("Invalid date format.");
        }
    }

    public Date getMinDate() {
        return new Date(0);
    }

    public Date getMaxDate() {
        return new Date(Long.MAX_VALUE);
    }

    public String toMongoFormat(Date date) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        return sdfDate.format(date) + "T" + sdfTime.format(date);
    }

}
