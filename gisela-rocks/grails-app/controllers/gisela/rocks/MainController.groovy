package gisela.rocks

import org.joda.time.DateTime
import org.joda.time.Interval

import static gisela.rocks.GoogleCalendarService.getCALENDAR_ID

class MainController {

    def googleCalendarService

    def client
    def location
    def until

    def index() {
        client = googleCalendarService.retrieveCalendar()
        retrieveCurrentTravel()

        [
                isTraveling    : location == null,
                currentLocation: location,
                until          : until
        ]
    }

    private void retrieveCurrentTravel() {
        def items = client.events().list(CALENDAR_ID).execute().items
        items.each {
            def start = new DateTime(it.start.date.getValue())
            def end = new DateTime(it.end.date.getValue())
            def interval = new Interval(start, end)
            if (interval.containsNow()) {
                def event = retrieveEventName(it.id)
                location = event.getLocation()
                until = new DateTime(event.end.date.getValue())
            }
        }
    }

    def private retrieveEventName(String eventId) {
        return client.events().get(CALENDAR_ID, eventId).execute()
    }

}
