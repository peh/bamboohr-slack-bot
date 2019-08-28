package uberall.model

import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

class EmployeeTimeOffInfo {

    long id
    long employeeId
    String name
    LocalDate start
    LocalDate end
    Type type
    String typeString

    static EmployeeTimeOffInfo parse(Map json) {
        EmployeeTimeOffInfo info = new EmployeeTimeOffInfo(
                id: json.id,
                employeeId: json.employeeId,
                typeString: json.type,
                type: Type.fromBambooType(json.type),
                name: json.name,
                start: LocalDate.parse(json.start),
                end: LocalDate.parse(json.end),
        )
        info
    }

    Map getSlackAttachment() {
        StringBuilder sb = new StringBuilder(startString)
        if (endString != startString) {
            sb.append(" - $endString")
        }
        int diff = Days.daysBetween(start, end).days + 1
        String diffString = diff > 1 ? "$diff days" : "$diff day"

        sb.append(" ($diffString - $typeString)")
        [
                title: "$name".toString(),
                value: sb.toString(),
                short: false
        ]
    }

    String getStartString() {
        start.toString(DateTimeFormat.mediumDate())
    }

    String getEndString() {
        end.toString(DateTimeFormat.mediumDate())
    }

    static enum Type {
        TIME_OFF,
        HOMEOFFICE,
        SICK

        static Type fromBambooType(String type) {
            switch (type) {
                case "Sick Leave": return SICK
                case "Homeoffice": return HOMEOFFICE
                case "timeOff":
                default: return TIME_OFF
            }
        }
    }
}
