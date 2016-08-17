package gisela.rocks

/**
 * Used as cache to minimize requests to google maps.
 */
class Location {

    static constraints = {
    }

    String location
    double longitude
    double latitude

}
