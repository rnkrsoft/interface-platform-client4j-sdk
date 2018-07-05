package com.rnkrsoft.platform.client;

/**
 * Created by rnkrsoft.com on 2018/7/5.
 * 位置提供者
 */
public interface LocationProvider {
    class Location{
        /**
         * 经度
         */
        double lng = 0;
        /**
         * 纬度
         */
        double lat = 0;


        public Location(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
        }

        public Location() {
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    /**
     * 定位位置
     * @param locationStore 位置存储对象
     */
    void locate(LocationStore locationStore);
}
