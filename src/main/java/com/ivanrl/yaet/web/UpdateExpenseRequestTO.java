package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.expense.ExpenseWithCategoryDO;
import com.ivanrl.yaet.domain.expense.UpdateExpenseRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

// TODO Consider if a specific annotation should be defined for web objects
record UpdateExpenseRequestTO(int id,
                              int categoryId,
                              @NotEmpty(message = "Payee must not be empty")
                              @Size(max = 50, message = "Payee cannot exceed 50 characters")
                              String payee,
                              @NotNull(message = "Date must not be empty")
                              LocalDate date,
                              @NotNull(message = "Amount must not be empty")
                              BigDecimal amount,
                              @Size(max = 255, message = "Comment must not exceed 255 characters")
                              String comment) {

    public UpdateExpenseRequest toDomainModel() {
        return new UpdateExpenseRequest(id, categoryId, payee, date, amount, comment);
    }


    public static UpdateExpenseRequestTO from(ExpenseWithCategoryDO expense) {
        return new UpdateExpenseRequestTO(expense.id(),
                                          expense.category().id(),
                                          expense.payee(),
                                          expense.date(),
                                          expense.amount(),
                                          expense.comment());
    }
}
