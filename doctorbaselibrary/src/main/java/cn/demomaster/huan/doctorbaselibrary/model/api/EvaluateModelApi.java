package cn.demomaster.huan.doctorbaselibrary.model.api;

import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/28.
 * description：
 */
public class EvaluateModelApi {


    /**
     * starRanks : {"overallRank":"3","professionRank5Star":"0","serviceRankBelow3Star":"0","serviceRank4Star":"0","professionRankBelow3Star":"0","serviceRank5Star":"0","professionRank4Star":"0"}
     * detailedEvaluations : []
     */

    private StarRanksBean starRanks;
    private List<Evaluation> detailedEvaluations;

    public StarRanksBean getStarRanks() {
        return starRanks;
    }

    public void setStarRanks(StarRanksBean starRanks) {
        this.starRanks = starRanks;
    }

    public List<Evaluation> getDetailedEvaluations() {
        return detailedEvaluations;
    }

    public void setDetailedEvaluations(List<Evaluation> detailedEvaluations) {
        this.detailedEvaluations = detailedEvaluations;
    }

    public static class StarRanksBean {
        /**
         * overallRank : 3
         * professionRank5Star : 0
         * serviceRankBelow3Star : 0
         * serviceRank4Star : 0
         * professionRankBelow3Star : 0
         * serviceRank5Star : 0
         * professionRank4Star : 0
         */

        private String overallRank;
        private String professionRank5Star;
        private String serviceRankBelow3Star;
        private String serviceRank4Star;
        private String professionRankBelow3Star;
        private String serviceRank5Star;
        private String professionRank4Star;

        public String getOverallRank() {
            return overallRank;
        }

        public void setOverallRank(String overallRank) {
            this.overallRank = overallRank;
        }

        public String getProfessionRank5Star() {
            return professionRank5Star;
        }

        public void setProfessionRank5Star(String professionRank5Star) {
            this.professionRank5Star = professionRank5Star;
        }

        public String getServiceRankBelow3Star() {
            return serviceRankBelow3Star;
        }

        public void setServiceRankBelow3Star(String serviceRankBelow3Star) {
            this.serviceRankBelow3Star = serviceRankBelow3Star;
        }

        public String getServiceRank4Star() {
            return serviceRank4Star;
        }

        public void setServiceRank4Star(String serviceRank4Star) {
            this.serviceRank4Star = serviceRank4Star;
        }

        public String getProfessionRankBelow3Star() {
            return professionRankBelow3Star;
        }

        public void setProfessionRankBelow3Star(String professionRankBelow3Star) {
            this.professionRankBelow3Star = professionRankBelow3Star;
        }

        public String getServiceRank5Star() {
            return serviceRank5Star;
        }

        public void setServiceRank5Star(String serviceRank5Star) {
            this.serviceRank5Star = serviceRank5Star;
        }

        public String getProfessionRank4Star() {
            return professionRank4Star;
        }

        public void setProfessionRank4Star(String professionRank4Star) {
            this.professionRank4Star = professionRank4Star;
        }
    }


    public static class Evaluation{

        /**
         * date : 2019-05-06
         * serviceMerit : 4
         * patientMobileNumber : 166****5136
         * professionMerit : 3
         * moreDesc : 34
         */

        private String date;
        private String serviceMerit;
        private String patientMobileNumber;
        private String professionMerit;
        private String moreDesc;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getServiceMerit() {
            return serviceMerit;
        }

        public void setServiceMerit(String serviceMerit) {
            this.serviceMerit = serviceMerit;
        }

        public String getPatientMobileNumber() {
            return patientMobileNumber;
        }

        public void setPatientMobileNumber(String patientMobileNumber) {
            this.patientMobileNumber = patientMobileNumber;
        }

        public String getProfessionMerit() {
            return professionMerit;
        }

        public void setProfessionMerit(String professionMerit) {
            this.professionMerit = professionMerit;
        }

        public String getMoreDesc() {
            return moreDesc;
        }

        public void setMoreDesc(String moreDesc) {
            this.moreDesc = moreDesc;
        }
    }

}
