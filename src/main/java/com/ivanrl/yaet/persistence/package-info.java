@FilterDef(name = "userFilter",
        parameters = @ParamDef(name = "userId", type = Integer.class),
        defaultCondition = "user_id = :userId")
package com.ivanrl.yaet.persistence;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;