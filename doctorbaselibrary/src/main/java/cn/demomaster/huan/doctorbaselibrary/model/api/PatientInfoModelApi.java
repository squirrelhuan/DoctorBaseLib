package cn.demomaster.huan.doctorbaselibrary.model.api;

/**
 * @author squirrel桓
 * @date 2018/12/24.
 * description：
 */
public class PatientInfoModelApi {


    /**
     * name : 褚国庆
     * birth : 1992-05-01
     * defaultAddress : {"id":7,"province":"上海市","city":"上海市","region":"黄浦区","detailAddress":"佛珠","isDefault":"1"}
     * isIdentityAuth : Y
     */

    private String name;
    private String birth;
    private String gender;
    private DefaultAddressBean defaultAddress;
    private String isIdentityAuth;
    private String personalImageUrl;
    private String reactDrugs;
    private String diseaseHistory;
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPersonalImageUrl() {
        return personalImageUrl;
    }

    public void setPersonalImageUrl(String personalImageUrl) {
        this.personalImageUrl = personalImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public DefaultAddressBean getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(DefaultAddressBean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public String getIsIdentityAuth() {
        return isIdentityAuth;
    }

    public void setIsIdentityAuth(String isIdentityAuth) {
        this.isIdentityAuth = isIdentityAuth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getReactDrugs() {
        return reactDrugs;
    }

    public void setReactDrugs(String reactDrugs) {
        this.reactDrugs = reactDrugs;
    }

    public String getDiseaseHistory() {
        return diseaseHistory;
    }

    public void setDiseaseHistory(String diseaseHistory) {
        this.diseaseHistory = diseaseHistory;
    }

    public static class DefaultAddressBean {
        /**
         * id : 7
         * province : 上海市
         * city : 上海市
         * region : 黄浦区
         * detailAddress : 佛珠
         * isDefault : 1
         */

        private int id;
        private String province;
        private String city;
        private String region;
        private String detailAddress;
        private String isDefault;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getDetailAddress() {
            return detailAddress;
        }

        public void setDetailAddress(String detailAddress) {
            this.detailAddress = detailAddress;
        }

        public String getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(String isDefault) {
            this.isDefault = isDefault;
        }
    }
}
