package bamboohr.slack.bot.services

import bamboohr.slack.bot.data.UserDatabase
import bamboohr.slack.bot.model.Employee
import bamboohr.slack.bot.model.EmployeeTimeOffInfo
import bamboohr.slack.bot.model.User
import bamboohr.slack.bot.model.slack.LoginBotCommand
import bamboohr.slack.bot.model.slack.LogoutBotCommand
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.reactivex.Flowable
import io.reactivex.Maybe

import javax.inject.Inject
import javax.inject.Singleton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Singleton
@Slf4j
class BambooService {

    @Client('https://api.bamboohr.com')
    @Inject
    RxHttpClient httpClient

    @Value('${bamboohr.company}')
    String company

    @Inject
    UserDatabase userDatabase

    List<EmployeeTimeOffInfo> getEmployeesWhoAreOutToday(String apiKey) {
        String url = "$basePath/time_off/whos_out?end=${LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)}"
        def list = httpClient.retrieve(getRequest(url, apiKey), List).firstElement().blockingGet().collect { EmployeeTimeOffInfo.parse(it as Map) }.unique { it.employeeId }

        Flowable.fromIterable(list).flatMapMaybe({ basicInfo ->
            getTimeOffDetails(apiKey, basicInfo.id).doOnSuccess({ List l ->
                if (l.size()) {
                    def detailedInfo = l.first()
                    basicInfo.type = EmployeeTimeOffInfo.Type.fromBambooType(detailedInfo.type.name)
                    basicInfo.typeString = detailedInfo.type.name
                }
            })
        }, false, 10).toList().blockingGet()
        list
    }

    Maybe<List> getTimeOffDetails(String apiKey, long id) {
        httpClient.retrieve(getRequest("$basePath/time_off/requests?id=$id", apiKey), List).firstElement()
    }

    boolean login(LoginBotCommand cmd) {
        boolean valid = false
        try {
            Employee employee = httpClient.toBlocking().retrieve(getRequest("$basePath/employees/0?fields=fullName1", cmd.apiKey), Employee)

            if (employee) {
                valid = true
                User known = userDatabase.get(cmd.user)
                if (!known) {
                    known = new User(id: cmd.user, name: employee.name)
                }
                known.apiKey = cmd.apiKey
                userDatabase.save(known)
            }
        } catch (e) {
            log.error "failed to login using the given apiKey", e
            cmd.replyMessage = "failed to login using the given apiKey"
        }
        cmd.replyMessage = "Successfully Logged you in. You can now use `/bamboo preview` to check out how todays message would have looked"
        valid
    }

    private static HttpRequest getRequest(String url, String apiKey) {
        HttpRequest.GET(url).header("accept", "application/json").header("Authorization", "Basic ${"$apiKey:x".bytes.encodeBase64().toString()}")
    }

    boolean logout(LogoutBotCommand cmd) {
        userDatabase.remove(cmd.user)
        true
    }

    private getBasePath() {
        "/api/gateway.php/$company/v1"
    }
}
