package time;


import com.alibaba.dubbo.common.utils.Assert;

public class TimeUtils {

    private static Time time;

    public TimeUtils(Time time) {
        this.time = time;
    }

    public static long getMisecond() {
        return time.getMisecond();
    }
}