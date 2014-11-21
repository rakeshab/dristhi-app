package org.ei.drishti.view.contract;


import org.joda.time.LocalDate;

import java.util.Comparator;
import java.util.List;

public interface PNCSmartRegisterClient extends BaseFPSmartRegisterClient {

    Comparator<SmartRegisterClient> DATE_OF_DELIVERY_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient lhs, SmartRegisterClient rhs) {
            return ((PNCSmartRegisterClient) lhs).deliveryDate().compareTo(((PNCSmartRegisterClient) rhs).deliveryDate());
        }
    };
    public String thayiNumber();
    public String deliveryDateForDisplay();
    public String deliveryShortDate();
    public LocalDate deliveryDate();
    public String deliveryPlace();
    public String deliveryType();
    public String deliveryComplications();
    public String womanDOB();
    public List<ChildClient> children();
}

