package util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class MLBUtil {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    public static final DecimalFormat DOLLAR_FORMAT = new DecimalFormat("$#,##0.00");
    public static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat(".000");
    public static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("#,###");
}
