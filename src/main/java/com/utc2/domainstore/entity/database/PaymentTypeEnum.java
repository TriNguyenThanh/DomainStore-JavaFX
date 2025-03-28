package com.utc2.domainstore.entity.database;

public enum PaymentTypeEnum {
    VNPAY(1),
    MOMO(2),
    CREDITCARD(3),
    ZALOPAY(4);
    
    private int code;
    PaymentTypeEnum(int code){
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }

    public static PaymentTypeEnum getPaymentMethod(int code) {
        for (PaymentTypeEnum type : PaymentTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
