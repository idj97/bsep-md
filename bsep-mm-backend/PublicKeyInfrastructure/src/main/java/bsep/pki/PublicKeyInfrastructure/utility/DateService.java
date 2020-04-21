package bsep.pki.PublicKeyInfrastructure.utility;

import bsep.pki.PublicKeyInfrastructure.exception.ApiBadRequestException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class DateService {

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public Date getDate(String dateStr) {
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new ApiBadRequestException("Exception catched while parsing date in DateService.");
        }
    }

    public Date addMonths(Date date, int months) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime.plusMonths((long)months);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
