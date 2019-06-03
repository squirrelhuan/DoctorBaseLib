package cn.demomaster.huan.doctorbaselibrary.model.api;

import java.io.Serializable;

/**
 * @author squirrel桓
 * @date 2018/11/22.
 * description：
 */
public class AddRessModel implements Serializable {


    /**
     * id : 1
     * province : 上海
     * city : 上海
     * region : 黄埔区
     * detailAddress : 三门路600号
     * isDefault : 1
     */

    private String id;
    private String province;
    private String city;
    private String region;
    private String detailAddress;
    private String isDefault;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getAddressName() {
        return province+" "+region+" "+detailAddress;
    }
}
