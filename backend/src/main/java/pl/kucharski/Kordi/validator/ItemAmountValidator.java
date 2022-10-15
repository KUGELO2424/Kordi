package pl.kucharski.Kordi.validator;

import pl.kucharski.Kordi.model.collection_item.CollectionItemDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemAmountValidator implements ConstraintValidator<ItemValidator, CollectionItemDTO> {

    /**
     * Validate zipcode and city depending on the country
     */
    public boolean isValid(CollectionItemDTO object, ConstraintValidatorContext context) {
        if (object == null) {
            throw new IllegalArgumentException("@ItemValidator only applies to CollectionItemDTO objects");
        }
        int currentAmount = object.getCurrentAmount();
        int maxAmount = object.getMaxAmount();
        if (currentAmount > maxAmount) {
            return false;
        } else {
            return currentAmount >= 0;
        }
    }
}