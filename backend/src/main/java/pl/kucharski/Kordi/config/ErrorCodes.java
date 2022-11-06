package pl.kucharski.Kordi.config;

public class ErrorCodes {

    public static final String USER_NOT_FOUND = "user.notfound";
    public static final String USER_NOT_VERIFIED = "user.notverified";
    public static final String USER_ALREADY_VERIFIED = "user.alreadyverified";
    public static final String USERNAME_ALREADY_EXISTS = "username.exists";
    public static final String EMAIL_ALREADY_EXISTS = "email.exists";
    public static final String PHONE_ALREADY_EXISTS = "phone.exists";
    public static final String EMAIL_NOT_VALID = "email.notvalid";
    public static final String CURRENT_USER_NOT_AN_OWNER = "user.notcollectionowner";
    public static final String USER_NOT_FOUND_WITH_GIVEN_TOKEN = "user.notfoundwithgiventoken";
    public static final String USER_BAD_CREDENTIALS = "user.badcredentials";

    public static final String PASSWORD_OLD_DOES_NOT_MATCH = "password.oldpassword.notmatch";
    public static final String PASSWORD_TOO_SHORT = "password.tooshort";

    public static final String ADDRESS_EXISTS = "address.exists";
    public static final String COMMENT_NOT_FOUND = "comment.notfound";

    public static final String EMAIL_FAILED_CONNECTION = "email.sendfailedconnection";
    public static final String EMAIL_FAILED = "email.sendfailed";
    public static final String EMAIL_ALREADY_CONFIRMED = "email.alreadyconfirmed";
    public static final String VERIFICATION_ERROR = "verification.error";

    public static final String COLLECTION_NOT_FOUND = "collection.notfound";
    public static final String COLLECTION_ITEM_NOT_FOUND = "collection.item.notfound";
    public static final String COLLECTION_ITEM_CURRENT_BIGGER_THAN_MAX = "collection.item.currentbiggerthanmax";

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
    public static final String VERIFICATION_TYPE_CANNOT_BE_EMPTY = "verificationtype.empty";

}
