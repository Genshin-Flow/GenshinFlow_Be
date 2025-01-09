package com.next.genshinflow.application.validation;

import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import org.springframework.stereotype.Component;

@Component
public class ReportValidationManager {

    public void validateNotSelfReport(String reporterEmail, String targetEmail) {
        if (reporterEmail.equals(targetEmail)) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_REPORT_YOURSELF);
        }
    }

    // todo: 제재 로직이 수정된 후 Role의 징계 사항만 분리해 검증하는 로직이 필요함
}
