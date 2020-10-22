package com.tristana.sandroid.model.trafficManager;

import java.util.List;

public class LightStatusRespModel {
    /**
     * code : 0
     * status : [{"roadId":"1","redLightDuration":"258","yellowLightDuration":"6","greenLightDuration":"416"},{"roadId":"2","redLightDuration":"554","yellowLightDuration":"651","greenLightDuration":"240"},{"roadId":"3","redLightDuration":"631","yellowLightDuration":"892","greenLightDuration":"792"},{"roadId":"4","redLightDuration":"729","yellowLightDuration":"450","greenLightDuration":"10"},{"roadId":"5","redLightDuration":"346","yellowLightDuration":"744","greenLightDuration":"147"},{"roadId":"6","redLightDuration":"104","yellowLightDuration":"528","greenLightDuration":"565"},{"roadId":"7","redLightDuration":"585","yellowLightDuration":"803","greenLightDuration":"865"},{"roadId":"8","redLightDuration":"757","yellowLightDuration":"531","greenLightDuration":"94"},{"roadId":"9","redLightDuration":"493","yellowLightDuration":"309","greenLightDuration":"513"},{"roadId":"10","redLightDuration":"173","yellowLightDuration":"812","greenLightDuration":"194"}]
     */

    private String code;
    private List<StatusBean> status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<StatusBean> getStatus() {
        return status;
    }

    public void setStatus(List<StatusBean> status) {
        this.status = status;
    }

    public static class StatusBean {
        /**
         * roadId : 1
         * redLightDuration : 258
         * yellowLightDuration : 6
         * greenLightDuration : 416
         */

        private String roadId;
        private String redLightDuration;
        private String yellowLightDuration;
        private String greenLightDuration;

        public String getRoadId() {
            return roadId;
        }

        public void setRoadId(String roadId) {
            this.roadId = roadId;
        }

        public String getRedLightDuration() {
            return redLightDuration;
        }

        public void setRedLightDuration(String redLightDuration) {
            this.redLightDuration = redLightDuration;
        }

        public String getYellowLightDuration() {
            return yellowLightDuration;
        }

        public void setYellowLightDuration(String yellowLightDuration) {
            this.yellowLightDuration = yellowLightDuration;
        }

        public String getGreenLightDuration() {
            return greenLightDuration;
        }

        public void setGreenLightDuration(String greenLightDuration) {
            this.greenLightDuration = greenLightDuration;
        }
    }
}
