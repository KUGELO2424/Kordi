package pl.kucharski.Kordi.config;

public class ErrorCodes {

    public static final String USER_NOT_FOUND = "user.not.found";
    public static final String USER_NOT_VERIFIED_EMAIL = "user.not.verified.email";
    public static final String USER_NOT_VERIFIED_PHONE = "user.not.verified.phone";
    public static final String USER_ALREADY_VERIFIED = "user.already.verified";
    public static final String USERNAME_ALREADY_EXISTS = "user.username.exists";
    public static final String EMAIL_ALREADY_EXISTS = "user.email.exists";
    public static final String PHONE_ALREADY_EXISTS = "user.phone.exists";
    public static final String EMAIL_NOT_VALID = "email.not.valid";
    public static final String CURRENT_USER_NOT_AN_OWNER = "user.not.collection.owner";
    public static final String USER_NOT_FOUND_WITH_GIVEN_TOKEN = "user.not.found.with.given.token";
    public static final String USER_BAD_CREDENTIALS = "user.bad.credentials";

    public static final String PASSWORD_OLD_DOES_NOT_MATCH = "password.old.password.not.match";
    public static final String PASSWORD_TOO_SHORT = "password.too.short";

    public static final String ADDRESS_EXISTS = "address.exists";
    public static final String COMMENT_NOT_FOUND = "comment.notfound";

    public static final String EMAIL_FAILED_CONNECTION = "email.send.failed.connection";
    public static final String EMAIL_FAILED = "email.send.failed";
    public static final String EMAIL_ALREADY_CONFIRMED = "email.already.confirmed";
    public static final String VERIFICATION_ERROR = "email.verification.error";
    public static final String PHONE_VERIFICATION_CODE_NOT_VALID = "phone.verification.code.not.valid";
    public static final String PHONE_VERIFICATION_ERROR = "phone.verification.error";

    public static final String COLLECTION_NOT_FOUND = "collection.notfound";
    public static final String COLLECTION_ITEM_NOT_FOUND = "collection.item.notfound";
    public static final String COLLECTION_ITEM_CURRENT_BIGGER_THAN_MAX = "collection.item.current.bigger.than.max";
    public static final String COLLECTION_END_DATE_INVALID = "collection.end.date.invalid";
    public static final String AMOUNT_CANNOT_BE_EMPTY = "collection.item.submit.amount.empty";
    public static final String ITEM_ID_CANNOT_BE_EMPTY = "collection.item.submit.itemId.empty";

    public static final String TOKEN_NOT_FOUND = "token.notfound";
    public static final String TOKEN_EXPIRED = "token.expired";

    public static final String TITLE_CANNOT_BE_EMPTY = "collection.title.empty";
    public static final String ITEM_NAME_CANNOT_BE_EMPTY = "item.name.empty";
    public static final String ITEM_TYPE_CANNOT_BE_EMPTY = "item.type.empty";
    public static final String ITEM_CATEGORY_CANNOT_BE_EMPTY = "item.category.empty";

    public static final String USERNAME_CANNOT_BE_EMPTY = "username.empty";
    public static final String FIRSTNAME_CANNOT_BE_EMPTY = "firstname.empty";
    public static final String LASTNAME_CANNOT_BE_EMPTY = "lastname.empty";
    public static final String EMAIL_CANNOT_BE_EMPTY = "email.empty";
    public static final String PHONE_CANNOT_BE_EMPTY = "phone.empty";
    public static final String VERIFICATION_TYPE_CANNOT_BE_EMPTY = "verification.type.empty";

}
