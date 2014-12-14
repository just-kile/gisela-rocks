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
        retrieveCurrentTravel(client)

        List previousTravels = retrievePreviousTravels(client)
        List upcomingTravels = retrieveUpcomingTravels(client)

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
        retrieveTravels(client) {start, end -> end.beforeNow}
    }

    private List retrieveUpcomingTravels(Calendar client) {
        retrieveTravels(client) {start, end -> end.afterNow}
    }

    private List retrieveTravels(Calendar client, Closure condition) {
        def travels = []
        def items = client.events().list(GoogleCalendarService.CALENDAR_ID).execute().items
        items.each {
            def updated = new DateTime(it.updated.getValue())
            def start = new DateTime(it.start.date.getValue())
            def end = new DateTime(it.end.date.getValue()).minusDays(1)
            if (condition(start, end)) {
                def location = googleCalendarService.retrieveLocation(it.id, updated)
                travels.add([start: start, end: end, location: location])
            }
        }
        travels = travels.sort { it.start }.reverse()
        travels
    }

    private void retrieveCurrentTravel(Calendar client) {
        def travels = retrieveTravels(client) {start, end ->new Interval(start, end).containsNow()}
        def travel = travels.first()
        location = googleCalendarService.retrieveLocation(travel.id, updated)
        until = travel.end
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
