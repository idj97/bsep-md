package bsep.pki.PublicKeyInfrastructure.utility;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateService {

    public Date addMonths(Date date, int months) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime.plusMonths((long)months);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
