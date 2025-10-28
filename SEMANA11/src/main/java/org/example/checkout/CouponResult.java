package org.example.checkout;

public final class CouponResult {
    public final double discountPercent;
    public final boolean freeShipping;
    public double percent;

    CouponResult(double discountPercent, boolean freeShipping) {
        this.discountPercent = discountPercent;
        this.freeShipping = freeShipping;
    }
}
