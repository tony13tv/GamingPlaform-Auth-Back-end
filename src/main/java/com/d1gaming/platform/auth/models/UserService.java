package com.d1gaming.platform.auth.models;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.hash.Scrypt;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    public static final String userCollection = "users";

    @Autowired
    private Firestore firestore;

    //get users collection from Firestore.
    private CollectionReference getUsersCollection() {
        return firestore.collection(userCollection);
    }

    public User login(String userEmail, String userPassword) throws FirebaseAuthException {
        User user = new User();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Scrypt scrypt = Scrypt.builder().setKey("ZtrTJcmTu6kZatl09cRYN4DerIVhtLIx738VoNmqaXOfP5HyG5d5a38Ra/a5QCcAPkWI4Yp1FM4iR/5B1rudQQ==".getBytes())
                .setMemoryCost(14)
                .setSaltSeparator("Bw==".getBytes())
                .setRounds(4).build();
        UserRecord userRecord = auth.getUserByEmail(userEmail);
        // See the UserRecord reference doc for the contents of userRecord.
        System.out.println("Successfully fetched user data: " + userRecord.getEmail());
        String uid = userRecord.getUid();
        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("premiumAccount", true);

        String customToken = FirebaseAuth.getInstance()
                .createCustomToken(uid, additionalClaims);
        user.setUserId(userRecord.getUid());
        user.setUserJWT(customToken);
        return user;
    }

    public User login(String userEmail, String userPassword, int ignored) throws ExecutionException, InterruptedException {
        //Perform a query based on a user's Name.
        Query query = getUsersCollection()
                .whereEqualTo("userEmail", userEmail)
                .whereEqualTo("userPassword", userPassword);
        QuerySnapshot snapshot = query.get().get();
        if (!snapshot.isEmpty()) {
            List<User> userList = snapshot.toObjects(User.class);
            //Since there is a unique userName for each document,
            //there will only be one User object on the list, we will retrieve the first one.
            for (User currUser : userList) {
                return currUser;
            }
        }
        return null;
    }

    public User signup(User user) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getUserEmail())
                .setEmailVerified(false)
                .setPassword(user.getUserPassword())
                .setDisabled(false);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        System.out.println("Successfully created new user: " + userRecord.getUid());
        user.setUserEmail(userRecord.getEmail());
        return user;
    }

    public String signup(User user, int ignored) throws ExecutionException, InterruptedException {
        Query query = getUsersCollection().whereEqualTo("userName", user.getUserName());
        QuerySnapshot querySnapshot = query.get().get();
        //Query to validate if userName is already in use.
        if (querySnapshot.isEmpty()) {
            ApiFuture<DocumentReference> document = getUsersCollection().add(user);
            DocumentReference reference = document.get();
            String userId = document.get().getId();
            //Assign auto-generated to userId field.
            WriteBatch batch = FirestoreClient.getFirestore().batch();
            batch.update(reference, "userId", userId);
            batch.commit();
            return "Created user with ID: " + "'" + userId + "'";
        }
        return "Username is already in use";
    }
}
