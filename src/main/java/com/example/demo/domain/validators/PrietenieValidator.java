package com.example.demo.domain.validators;

import com.example.demo.domain.Prietenie;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;


public class PrietenieValidator implements Validator<Prietenie> {
    @Override
    public void validate(Prietenie entity) throws ValidationException {
        List<String> errors = new ArrayList<>();

        BiConsumer<Long, String> validateId = (id, error) -> {
            if(id < 0)
                errors.add(error);
        };
        BiConsumer<com.example.demo.domain.Tuple<Long, Long>, String> validateFriend = (id, error) ->{
            if(id.getLeft().equals(id.getRight()))
                errors.add(error);
        };

        validateId.accept(entity.getId().getLeft(), "ID1 este invalid! ");
        validateId.accept(entity.getId().getRight(), "ID2 este invalid! ");
        validateFriend.accept(entity.getId(), "Nu poti fi prieten cu propria persoana! ");

        if(!errors.isEmpty()) throw new ValidationException("Eroare! " + String.join("", errors));
    }
}