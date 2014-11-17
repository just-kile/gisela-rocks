package gisela.rocks

import org.joda.time.DateTime

/**
 * The CalendarEvent is used as cache to minimize calls on the google calendar api.
 */
class CalendarEvent {

    static constraints = {
    }

    String eventId
    String location
    long lastUpdate

}
