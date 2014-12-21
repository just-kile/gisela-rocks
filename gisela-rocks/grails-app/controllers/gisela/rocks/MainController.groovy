package gisela.rocks

import com.google.api.services.calendar.Calendar
import org.joda.time.DateTime
import org.joda.time.Interval

class MainController {

    def grailsApplication
    def googleCalendarService
    def locationService

    def index() {
        def client = googleCalendarService.retrieveCalendar()

        def currentTravel = retrieveCurrentTravel(client)
        List previousTravels = retrievePreviousTravels(client)
        List upcomingTravels = retrieveUpcomingTravels(client)

        int totalDistance = calcTotalTravelDistance(previousTravels)

        [
                current   : currentTravel,

                previous  : previousTravels,
                upcoming  : upcomingTravels,

                statistics: [
                        totalDistance: totalDistance
                ]

        ]
    }

    private int calcTotalTravelDistance(List previousTravels) {
        def totalDistance = 0
        def homeLat = grailsApplication.config.gisela.home.lat.toDouble()
        def homeLng = grailsApplication.config.gisela.home.lng.toDouble()

        previousTravels.each { travel ->
            // trip distance: return trip!
            if (travel.coordinates)
                totalDistance += 2 * calcDistance(travel.coordinates.latitude, travel.coordinates.longitude, homeLat, homeLng)
        }
        totalDistance
    }

    private List retrievePreviousTravels(Calendar client) {
        def travels = retrieveTravels(client) { start, end -> end.beforeNow }
        travels = travels.sort { it.start }.reverse()
        return travels
    }

    private List retrieveUpcomingTravels(Calendar client) {
        def travels = retrieveTravels(client) { start, end -> start.afterNow }
        travels = travels.sort { it.start }
        return travels
    }

    private List retrieveTravels(Calendar client, Closure condition) {
        def travels = []
        def items = client.events().list(GoogleCalendarService.CALENDAR_ID).execute().items
        items.each { calendarEntry ->
            processCalendarEntry(travels, calendarEntry, condition)
        }
        return travels
    }

    private void processCalendarEntry(def travels, def calendarEntry, Closure condition) {
        def updated = new DateTime(calendarEntry.updated.getValue())
        def start = new DateTime(calendarEntry.start.date.getValue())
        def end = new DateTime(calendarEntry.end.date.getValue()).minusDays(1)
        if (condition(start, end)) {
            def location = googleCalendarService.retrieveLocation(calendarEntry.id, updated)
            def coordinates = locationService.retrieveLocation(location)
            def travel = [
                    start      : start,
                    end        : end,
                    location   : location,
                    coordinates: coordinates
            ]
            travels.add(travel)
        }
    }

    private def retrieveCurrentTravel(Calendar client) {
        def travels = retrieveTravels(client) { start, end -> new Interval(start, end).containsNow() }
        if (travels) {
            return  travels.first()
        } else {
            return null
        }
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
