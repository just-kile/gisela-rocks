package gisela.rocks

import com.google.api.services.calendar.Calendar
import org.joda.time.DateTime
import org.joda.time.Interval

class MainController {

    def grailsApplication
    def googleCalendarService
    def locationService

    def client
    def location
    def until

    def index() {
        client = googleCalendarService.retrieveCalendar()
        retrieveCurrentTravel()

        List previousTravels = retrievePreviousTravels(client)
        List upcomingTravels = retrieveUpcomingTraves(client)

        def gpsCoordinates = []
        previousTravels.each { travel ->
            def location = locationService.retrieveLocation(travel.location)
            if (location){
                gpsCoordinates.add([lat:location.latitude, lng:location.longitude])
            }
        }

        def totalDistance = 0
        def homeLat = grailsApplication.config.gisela.home.lat.toDouble()
        def homeLng = grailsApplication.config.gisela.home.lng.toDouble()

        gpsCoordinates.each {
            // trip distance: return trip!
            totalDistance += 2 * calcDistance(it.lat, it.lng, homeLat, homeLng)
        }

        [
                current       : [
                        isTraveling: location != null,
                        location   : location,
                        until      : until
                ],

                previous      : previousTravels,
                upcoming      : upcomingTravels,

                gpsCoordinates: gpsCoordinates,

                statistics    : [
                        totalDistance: totalDistance
                ]

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


    private double calcDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


}
