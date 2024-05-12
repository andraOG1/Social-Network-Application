package com.example.demo.domain.validators;


import com.example.demo.domain.Utilizator;
import com.example.demo.domain.validators.ValidationException;
import com.example.demo.domain.validators.Validator;

public class UtilizatorValidator implements Validator<Utilizator> {

    private LastNameValid ln;
    private FirstNameValid fn;
    private Validator<Utilizator> strategie_validare;

    public UtilizatorValidator()
    {
        //this.strategie_validare = strategie_validare;
        this.ln = new LastNameValid();
        this.fn = new FirstNameValid();
    }

    @Override
    public void validate(Utilizator entity) throws ValidationException {
        //TODO: implement method validate
        //strategie_validare.validate(entity);
        //new LastNameValid().validate(entity);
        //new FirstNameValid().validate(entity);
        ln.validate(entity);
        fn.validate(entity);
        if(entity.getPassword() == null)
            throw new ValidationException("Parola nu poate fi null");
        else if(entity.getPassword().isEmpty())
            throw new ValidationException("Trebuie sa scrieti o parola");

    }
}

