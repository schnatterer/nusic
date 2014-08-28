package org.mc2;

import java.util.Calendar;
import java.util.TimeZone;

/**
 *
 * @author marco
 */
public class CalendarUtils {


    public static String calcDurationString(Long durms){
        
        if (durms ==null) return "";

        Calendar cal = Calendar.getInstance();
        TimeZone tx = TimeZone.getDefault();
        int offset = tx.getOffset(durms);
        cal.setTimeInMillis(durms-offset);

        String dur;
       
        if (durms>3600000) //1 h.
        {
             dur = String.format("%1$tH:%1$tM:%1$tS", cal);
        }
        else
        {
            dur = String.format("%1$tM:%1$tS", cal);
        }

        return dur;
    }
    public static String calcDurationString(int sectors){
        
        return calcDurationString(calcDurationInMillis(sectors));
    }
    
    public static long calcDurationInMillis(int sectors) {
        return new Long(sectors)*1000/75;
    }
    public static long calcDurationInMillis(String durStr) throws MC2Exception {
        
        if (durStr == null) return 0;
        try {
                Calendar cal = Calendar.getInstance();
                TimeZone tx = TimeZone.getDefault();
                int offset = tx.getOffset(cal.getTimeInMillis());
                cal.setTimeInMillis(0);
                
                int sec=0;
                int min=0;
                int hour=0;
                
                long time;
                
 
                String[] split = durStr.split(":");
                if (split.length == 1){
                      sec = Integer.parseInt(split[0]);
                      cal.set(Calendar.SECOND,sec );
                }
                else if (split.length == 2) {
                      min = Integer.parseInt(split[0]);
                      sec = Integer.parseInt(split[1]);
                      cal.set(Calendar.MINUTE,min);
                      cal.set(Calendar.SECOND, sec);
                } else if (split.length == 3) {
                      hour = Integer.parseInt(split[0]);
                      min = Integer.parseInt(split[1]);
                      sec = Integer.parseInt(split[2]);
                      cal.set(Calendar.HOUR, hour);
                      cal.set(Calendar.MINUTE, min);
                      cal.set(Calendar.SECOND, sec);
                }
                else{
                    throw new MC2Exception("invalid time format, should be HH:MM:SS");
                }
                /*
             * Used calendar just as a entry validation. Strange stuffs with 
             * dayligth saving offset...
             */
                long timecal = cal.getTimeInMillis();
                long timeOffset = timecal+offset;
                
                time = (sec +min*60+hour*60*60)*1000;
                
                return time;
                
        } catch (NumberFormatException ex){
            throw new MC2Exception(ex);
        }
    }
    public static int calcDurationInSector(long durms){
         return ((int)durms*75/1000);
    }
    public static int calcDurationInSector(String durSt) throws MC2Exception{
         return calcDurationInSector(calcDurationInMillis(durSt));
    }
}
