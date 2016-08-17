package gisela.rocks

import grails.transaction.Transactional

@Transactional
class LocationService {

    /**
     * Returns the {@link Location} for a string represented location
     *
     * @param location the location
     * @return the {@link Location} or <code>null</code> if no location could be found.
     */
    Location retrieveLocation(String location) {
        def foundLocation = Location.findByLocation(location)
        if (!foundLocation) {
            foundLocation = createLocation(location)
        }
        return foundLocation
    }

    private Location createLocation(String location) {
        withHttp(uri: "https://maps.googleapis.com") {
            def html = get(path: '/maps/api/geocode/json', query: [address: location])
            try {
                def gpsCoordinates = html.results[0].geometry.location
                def dbLocation = new Location(
                        location: location,
                        latitude: gpsCoordinates.lat,
                        longitude: gpsCoordinates.lng
                )
                dbLocation.save()
                return dbLocation;
            } catch (Throwable th) {
                th.printStackTrace()
            }
        }
        return null
    }
}
