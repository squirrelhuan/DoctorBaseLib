package cn.demomaster.huan.doctorbaselibrary.model.api;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author squirrel桓
 * @date 2019/4/9.
 * description：
 */
public class ClosedOrderModelApi {

    /**
     * totalNum : 1
     * currentMonNum : 1
     * requestInfoMonthMap : {"201904":[{"trxId":69,"requestName":"全科_20190409120355","startAt":"2019-04-09 12:04:59","status":"WAITING_PAY_DOCTOR"}]}
     */

    private int totalNum;
    private int currentMonNum;
    private LinkedHashMap<String,List<OrderInfo> > requestInfoMonthMap;
    private List<OrderInfo>  requestBriefInfoDtos;


    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getCurrentMonNum() {
        return currentMonNum;
    }

    public void setCurrentMonNum(int currentMonNum) {
        this.currentMonNum = currentMonNum;
    }

    public LinkedHashMap<String, List<OrderInfo>> getRequestInfoMonthMap() {
        return requestInfoMonthMap;
    }

    public void setRequestInfoMonthMap(LinkedHashMap<String, List<OrderInfo>> requestInfoMonthMap) {
        this.requestInfoMonthMap = requestInfoMonthMap;
    }

    public List<OrderInfo> getRequestBriefInfoDtos() {
        return requestBriefInfoDtos;
    }

    public void setRequestBriefInfoDtos(List<OrderInfo> requestBriefInfoDtos) {
        this.requestBriefInfoDtos = requestBriefInfoDtos;
    }

    public static class OrderInfo {

        /**
         * trxId : 69
         * requestName : 全科_20190409120355
         * startAt : 2019-04-09 12:04:59
         * status : WAITING_PAY_DOCTOR
         */

        private int trxId;
        private String requestName;
        private String startAt;
        private String status;

        public int getTrxId() {
            return trxId;
        }

        public void setTrxId(int trxId) {
            this.trxId = trxId;
        }

        public String getRequestName() {
            return requestName;
        }

        public void setRequestName(String requestName) {
            this.requestName = requestName;
        }

        public String getStartAt() {
            return startAt;
        }

        public void setStartAt(String startAt) {
            this.startAt = startAt;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
