package com.ict300.P04.Utilitaires;

public class GeoUtils {

    // Rayon moyen de la terre en kilomètres
    private static final int EARTH_RADIUS_KM = 6371;

    /**
     * Calcule la distance géographique entre deux points GPS (Formule de Haversine).
     * Si l'un des points est nul, retourne Double.MAX_VALUE pour rejeter l'élément en fin de tri.
     */
    public static double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return Double.MAX_VALUE;
        }

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c; // Distance en Kilomètres
    }

    /**
     * Convertit une chaîne textuelle de scope (ex: "500m", "5km") en valeur numérique numérique (Kilomètres).
     */
    public static double convertScopeToKilometers(String scope) {
        if (scope == null) return 1.0;

        switch (scope.toLowerCase().trim()) {
            case "100m": return 0.1;
            case "500m": return 0.5;
            case "1km": return 1.0;
            case "5km": return 5.0;
            case "10km": return 10.0;
            case "ville": return -1.0;
            case "region": return -1.0;
            default: return 1.0;
        }
    }
}
