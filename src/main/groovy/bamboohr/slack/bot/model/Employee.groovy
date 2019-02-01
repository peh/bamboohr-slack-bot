package bamboohr.slack.bot.model

class Employee {
    long id
    String type
    int empoyeeId
    String name
    String fullName1
    String start
    String end

    String getName() {
        name ?: fullName1
    }
}
