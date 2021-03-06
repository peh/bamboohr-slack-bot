package uberall.services

import uberall.data.UserDatabase
import uberall.model.Employee
import uberall.model.EmployeeTimeOffInfo
import uberall.model.User
import uberall.model.slack.LoginBotCommand
import uberall.model.slack.LogoutBotCommand
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
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

    /**
     * Uses the BambooHR API to get a detailed list of employees and there TimeOff information.
     * This is done in two steps by first getting the /whos_out endpoint to grab a list
     * afterwards it uses the time_off API to grab a detailed list to also gather the "type" info.
     * As calling the details API 20 times might take to long this is done in parallel
     * @param apiKey
     * @return
     */
    List<EmployeeTimeOffInfo> getEmployeesWhoAreOutToday(String apiKey) {
        String url = "$basePath/time_off/whos_out?start=${LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)}&end=${LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)}"
        def list
        try {
            list = httpClient.retrieve(getRequest(url, apiKey), List).firstElement().blockingGet().collect { EmployeeTimeOffInfo.parse(it as Map) }.unique { it.employeeId }

            Flowable.fromIterable(list).flatMapMaybe({ basicInfo ->
                getTimeOffDetails(apiKey, basicInfo.id)?.doOnSuccess({ List l ->
                    if (l.size()) {
                        def detailedInfo = l.first()
                        basicInfo.type = EmployeeTimeOffInfo.Type.fromBambooType(detailedInfo.type.name)
                        basicInfo.typeString = detailedInfo.type.name
                    }
                })
            }, false, 10).toList().blockingGet()

        } catch (HttpClientResponseException e) {
            e.response.body()
            true
        }
        list
    }

    /**
     * Uses the time_off API to return details about the given TimeOffRequest
     * @param apiKey
     * @param id
     * @return
     */
    Maybe<List> getTimeOffDetails(String apiKey, long id) {
        try {
            return httpClient.retrieve(getRequest("$basePath/time_off/requests?start=${LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ISO_DATE)}&end=${LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ISO_DATE)}&id=$id", apiKey), List).firstElement()
        } catch (HttpClientResponseException e) {
            if(e.response.status == HttpStatus.UNAUTHORIZED) {
                // nothing
            } else {
                log.error("could not get TimeOff request for $id cause: $e.response.status")
            }
        }
        return null
    }

    /**
     * Validates the apiKey of the the given LoginBotCommand by simply sending a request to the BambooHR API and checking for exceptions
     * @param cmd
     * @return
     */
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

    /**
     * Builds a request with proper headers set for the given apikey
     * @param url
     * @param apiKey
     * @return
     */
    private static HttpRequest getRequest(String url, String apiKey) {
        HttpRequest.GET(url).headers(["accept": "application/json", "Authorization": "Basic ${"$apiKey:x".bytes.encodeBase64().toString()}"])
    }

    /**
     * removes the user assigned to the given LogoutBotCommand from the userDatabase
     * @param cmd
     * @return
     */
    boolean logout(LogoutBotCommand cmd) {
        userDatabase.remove(cmd.user)
        true
    }

    private getBasePath() {
        "/api/gateway.php/$company/v1"
    }
}
