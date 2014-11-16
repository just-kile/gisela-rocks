package gisela.rocks

import com.google.api.services.calendar.Calendar
import org.joda.time.DateTime
import org.joda.time.Interval

class MainController {

    def googleCalendarService

    def client
    def location
    def until

    def index() {
        client = googleCalendarService.retrieveCalendar()
        retrieveCurrentTravel()

        List previousTravels = retrievePreviousTravels(client)
        List upcomingTravels = retrieveUpcomingTraves(client)

        [
                current: [
                        isTraveling: location != null,
                        location   : location,
                        until      : until
                ],

                previous: previousTravels,
                upcoming: upcomingTravels

        ]
    }

    private List retrievePreviousTravels(Calendar client) {
        def previousTravels = []
        def items = client.events().list(GoogleCalendarService.CALENDAR_ID).execute().items
        items.each {
            def start = new DateTime(it.start.date.getValue())
            def end = new DateTime(it.end.date.getValue())
            if (end.beforeNow) {
                def event = retrieveEventName(it.id)
                def location = event.getLocation()
                previousTravels.add([start: start, end: end, location: location])
            }
        }
        previousTravels = previousTravels.sort { it.start }.reverse()
        previousTravels
    }

    private List retrieveUpcomingTraves(Calendar client) {
        def upcomingTravels = []
        def items = client.events().list(GoogleCalendarService.CALENDAR_ID).execute().items
        items.each {
            def start = new DateTime(it.start.date.getValue())
            def end = new DateTime(it.end.date.getValue())
            if (start.afterNow) {
                def event = retrieveEventName(it.id)
                def location = event.getLocation()
                upcomingTravels.add([start: start, end: end, location: location])
            }
        }
        upcomingTravels = upcomingTravels.sort { it.start }
        upcomingTravels
    }

    private void retrieveCurrentTravel() {
        def items = client.events().list(GoogleCalendarService.CALENDAR_ID).execute().items
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
        return client.events().get(GoogleCalendarService.CALENDAR_ID, eventId).execute()
    }

}
