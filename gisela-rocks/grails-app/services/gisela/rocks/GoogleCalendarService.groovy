package gisela.rocks

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.CalendarScopes
import grails.transaction.Transactional
import org.joda.time.DateTime

import javax.annotation.PostConstruct

@Transactional
class GoogleCalendarService {

    def static CALENDAR_ID = "5hpviugc2v1c9vkoudqj1po9jo@group.calendar.google.com"

    def grailsApplication

    def private static APPLICATION_NAME = "gisela.rocks"

    def private EMAIL_ADDRESS = "54598536839-4nioujstbka1rkmp4tjr94m9r7jnlqr9@developer.gserviceaccount.com"
    def private PATH_TO_P12_FILE

    def private HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    def private JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    @PostConstruct
    def init() {
        PATH_TO_P12_FILE = grailsApplication.config.gisela.googleP12Certificate
    }

    def com.google.api.services.calendar.Calendar retrieveCalendar() {
        def credential = createCredentials()
        return new com.google.api.services.calendar.Calendar.Builder(
                httpTransport, jsonFactory, credential).setApplicationName(APPLICATION_NAME).build()
    }

    def String retrieveLocation(String eventId, DateTime lastUpdate) {
        def event = CalendarEvent.findByEventId(eventId)
        if (!event || event.lastUpdate != lastUpdate.getMillis()) {
            def rawEvent = retrieveCalendar().events().get(GoogleCalendarService.CALENDAR_ID, eventId).execute()
            event = new CalendarEvent(
                    eventId: eventId,
                    location: rawEvent.location,
                    lastUpdate: lastUpdate.getMillis()
            )
            event.save()
        }
        return event.location
    }

    def private Credential createCredentials() throws Exception {
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(EMAIL_ADDRESS)
                .setServiceAccountPrivateKeyFromP12File(new File(PATH_TO_P12_FILE))
                .setServiceAccountScopes(Collections.singleton(CalendarScopes.CALENDAR))
                .build();
        credential.refreshToken()
        return credential
    }
}
