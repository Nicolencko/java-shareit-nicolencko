package ru.practicum.shareit.util.validation;

import ru.practicum.shareit.booking.dto.BookingInputDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndAfterStartValidator implements ConstraintValidator<EndAfterStartValidation, BookingInputDto> {
    @Override
    public boolean isValid(BookingInputDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        return bookingDto.getStart() != null &&
                bookingDto.getEnd() != null &&
                bookingDto.getStart().isBefore(bookingDto.getEnd());
    }
}
