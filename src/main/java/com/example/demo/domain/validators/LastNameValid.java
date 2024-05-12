package com.example.demo.domain.validators;


import com.example.demo.domain.Utilizator;

public class LastNameValid implements Validator<Utilizator>{

    @Override
    public void validate(Utilizator entity) throws ValidationException
    {
        if (entity == null || entity.getLastName() == null || entity.getLastName().trim().isEmpty()||
                entity.getLastName().matches("\\d+"))
            throw new ValidationException("Numele nu poate fi nul/gol. ");
    }
}
