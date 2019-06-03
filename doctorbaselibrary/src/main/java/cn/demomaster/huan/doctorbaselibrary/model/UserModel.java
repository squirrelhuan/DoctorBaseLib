package cn.demomaster.huan.doctorbaselibrary.model;

public class UserModel {


    /**
     * data : {"sessionId":"1251","token":"IdRj6cdaHNZiRZGCW+8bALe0PasmotCy45Sw+/s6QFMEDtlEpr1kjq3+0QFZPd2XqraPm0TA1DPLuNfcgc0qZ4obBirq0lOv356ufcaSMGIiJcWaO94Rm51LYxgnlONCZTpES5aCoKec0dk8W5fOiqtC/W7RVEhas8Sns2J2rr4S7MU9NXW2Jvx4mtJ+cA4ASICxjLHQ/Gaup4CkzsrRiw==","uuid":"SXngi5SkEFoC2UzUbbaz7Tg0We1xehsxRYDJ+MIyb6wuND3qQdZuDyCD87JLupTgyHHc7ItFZZPQZ6J4y6cJLwv1GzVkl0w0NIxwwflAb4dhLBr6wRvoU8lo9WW2mLNnf8Pjm1bnTFK9XGkQFadyimtl7BXc1GvgE5wPrO+RMKQ=","userName":"郑小白"}
     * message : 验证成功
     * retCode : 0
     */

    private String data;
    private String message;
    private int retCode;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }
}
