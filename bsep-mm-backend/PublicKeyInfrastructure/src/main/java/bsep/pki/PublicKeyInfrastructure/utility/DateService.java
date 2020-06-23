package bsep.pki.PublicKeyInfrastructure.utility;

import bsep.pki.PublicKeyInfrastructure.exception.ApiBadRequestException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class DateService {

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public Date getDate(String dateStr) {
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new ApiBadRequestException("Invalid date format.");
        }
    }

    public Date addMonths(Date date, int months) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, months);
        return c.getTime();
    }
}
