package pl.kucharski.Kordi.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.NotOwnerOfCollectionException;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.repository.CollectionRepository;
import pl.kucharski.Kordi.repository.UserRepository;

import java.util.Objects;

/**
 * An aspect that check if logged user is collection owner.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class IsCollectionOwnerAspect {

    private final UserRepository userRepository;
    private final CollectionRepository collectionRepository;

    @Around("@annotation(IsCollectionOwner) && args(collectionId,..)")
    public Object isCollectionOwnerAdvice(ProceedingJoinPoint joinPoint, long collectionId) throws Throwable {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() ->  new CollectionNotFoundException("Collection with id " + collectionId + " not found"));
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
        if (!Objects.equals(collection.getUserId(), user.getId())) {
            throw new NotOwnerOfCollectionException("User with id " + user.getId() +
                    " is not an owner of collection with id " + collectionId);
        }
        return joinPoint.proceed();
    }

}
