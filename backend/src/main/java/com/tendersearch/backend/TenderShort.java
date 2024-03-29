package com.tendersearch.backend;

import com.tendersearch.backend.models.Tender;

public class TenderShort {
    public Long id;
    public String name;
    public String vKey;
    // Search
    public String customer;
    public String price;
    public String currency;

    public static TenderShort fromTenderDB(Tender tender) {
        TenderShort tenderShort = new TenderShort();
        tenderShort.id = tender.id;
        tenderShort.name = tender.name;
        tenderShort.vKey = tender.vKey;
        tenderShort.customer = tender.customer;
        tenderShort.price = tender.price;
        tenderShort.currency = tender.currency;
        return tenderShort;
    }
}
