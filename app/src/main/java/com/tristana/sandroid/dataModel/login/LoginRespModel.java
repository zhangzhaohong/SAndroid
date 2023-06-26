package com.tristana.sandroid.dataModel.login;

public class LoginRespModel {
    /**
     * code : 0
     * msg : 登录成功
     * userInfo : {"userId":"10000088","headUrl":"https://storage.tracup.com/o_1ekl8jvoc1cua189s11kl1inbpnca.png","userPrivateName":"帅总","userPhoneNumber":"11011111111","userKey":"07d3fb7d3ce87af1ef2f48c1c3bb0cd2b2480d57db7c9ff82d3a260f2fc86ebb"}
     */

    private String code;
    private String msg;
    private UserInfoBean userInfo;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public static class UserInfoBean {
        /**
         * userId : 10000088
         * headUrl : https://storage.tracup.com/o_1ekl8jvoc1cua189s11kl1inbpnca.png
         * userPrivateName : 帅总
         * userPhoneNumber : 11011111111
         * userKey : 07d3fb7d3ce87af1ef2f48c1c3bb0cd2b2480d57db7c9ff82d3a260f2fc86ebb
         */

        private String userId;
        private String headUrl;
        private String userPrivateName;
        private String userPhoneNumber;
        private String userKey;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public String getUserPrivateName() {
            return userPrivateName;
        }

        public void setUserPrivateName(String userPrivateName) {
            this.userPrivateName = userPrivateName;
        }

        public String getUserPhoneNumber() {
            return userPhoneNumber;
        }

        public void setUserPhoneNumber(String userPhoneNumber) {
            this.userPhoneNumber = userPhoneNumber;
        }

        public String getUserKey() {
            return userKey;
        }

        public void setUserKey(String userKey) {
            this.userKey = userKey;
        }
    }
}
