package pub.developers.forum.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WeekDayEn {
    MONDAY(1, "星期一", "Monday"),
    TUESDAY(2, "星期二", "Tuesday"),
    WEDNESDAY(3, "星期三", "Wednesday"),
    THURSDAY(4, "星期四", "Thursday"),
    FRIDAY(5, "星期五", "Friday"),
    SATURDAY(6, "星期六", "Saturday"),
    SUNDAY(7, "星期日", "Sunday"),
    ;

    private Integer code;
    private String desc;
    private String englishName;

    public static WeekDayEn getByCode(Integer code) {
        for (WeekDayEn day : values()) {
            if (day.getCode().equals(code)) {
                return day;
            }
        }
        return null;
    }

    public static WeekDayEn getByDesc(String desc) {
        for (WeekDayEn day : values()) {
            if (day.getDesc().equals(desc)) {
                return day;
            }
        }
        return null;
    }

    public boolean isWeekend() {
        return this == SATURDAY || this == SUNDAY;
    }

    public boolean isWeekday() {
        return !isWeekend();
    }
}
