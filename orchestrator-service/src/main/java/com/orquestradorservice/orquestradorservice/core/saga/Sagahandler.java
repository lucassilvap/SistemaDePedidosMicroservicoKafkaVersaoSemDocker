package com.orquestradorservice.orquestradorservice.core.saga;


import com.orquestradorservice.orquestradorservice.core.enums.ESagaStatus;
import com.orquestradorservice.orquestradorservice.core.enums.Etopics;

import static com.orquestradorservice.orquestradorservice.core.enums.EEventSource.*;
import static com.orquestradorservice.orquestradorservice.core.enums.ESagaStatus.*;
import static com.orquestradorservice.orquestradorservice.core.enums.Etopics.*;

public final class Sagahandler {

    private Sagahandler() {
    }
    public static final Object [][] SAGA_HANDLER = {
            {ORCHESTRATOR, ESagaStatus.SUCCESS,PRODUCT_VALIDATION_SUCCESS},
            {ORCHESTRATOR,FAIL,FINISH_FAIL},

            {PRODUCT_VALIDATION_SERVICE,ROLLBACK_PENDING,PRODUCT_VALIDATION_FAIL    },
            {PRODUCT_VALIDATION_SERVICE,FAIL,FINISH_FAIL},
            {PRODUCT_VALIDATION_SERVICE,  ESagaStatus.SUCCESS, PAYMENT_SUCCESS},

            {PAYMENT_SERVICE,ROLLBACK_PENDING,PRODUCT_VALIDATION_FAIL    },
            {PAYMENT_SERVICE,FAIL,PRODUCT_VALIDATION_FAIL},
            {PAYMENT_SERVICE,  ESagaStatus.SUCCESS, INVENTORY_SUCCESS},

            {INVENTORY_SERVICE,ROLLBACK_PENDING,INVENTORY_FAIL },
            {INVENTORY_SERVICE,FAIL,PAYMENT_FAIL},
            {INVENTORY_SERVICE,  ESagaStatus.SUCCESS, FINISH_SUCCESS},
    };

    public static final int EVENT_SOURCE_INDEX = 0;
    public static final int SAGA_STATUS_INDEX = 1;
    public static final int TOPIC_INDEX = 2;
}
