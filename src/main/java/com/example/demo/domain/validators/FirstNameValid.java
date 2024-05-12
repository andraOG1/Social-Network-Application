package com.example.demo.domain.validators;

import com.example.demo.domain.Utilizator;

public class FirstNameValid implements Validator<Utilizator>{

    @Override
    public void validate(Utilizator entity) throws ValidationException
    {
        if(entity == null || entity.getFirstName() == null || entity.getFirstName().trim().isEmpty()
                || entity.getFirstName().matches("\\d+"))
            throw new ValidationException("Prenumele nu poate fi nul/gol/sa contina cifre. ");
    }

}