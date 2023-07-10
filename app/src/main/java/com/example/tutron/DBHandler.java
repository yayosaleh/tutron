package com.example.tutron;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DBHandler {

    private static final String TAG = "DBHandler";
    public static final String TUTOR_COLLECTION = "tutors";
    public static final String COMPLAINT_COLLECTION = "complaints";
    private static final String TUTOR_SUSPENSION_EXPIRY_KEY = "suspensionExpiry";

    // Interface allows front-end callers to perform operations upon successful or failed document-setting (or deletion)
    public interface SetDocumentCallback {
        void onSuccess(); // Called when a document is successfully set
        void onFailure(Exception e); // Called upon failure to set document
    }

    // GENERIC OPERATIONS //

    // Sets (i.e., creates or updates) Firestore document with specified id within specified collection
    // Invokes callback methods if given
    public static <T extends Identifiable> void setDocument(String documentId, @NonNull String collectionId, @NonNull T data, SetDocumentCallback callback) {
        // Access shared Firestore database instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // If documentId is null, this is a document creation operation
        // In this case, we must create a doc ref with an auto-generated id and set the passed object's id
        CollectionReference collectionRef = db.collection(collectionId);
        DocumentReference docRef;

        if (documentId == null) {
            // Create reference with auto-generated id using document() with no arg.
            docRef = collectionRef.document();
            // Set id of passed object
            data.setId(docRef.getId());
        } else {
            docRef = collectionRef.document(documentId);
        }

        // Attempt to set document
        docRef.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Log success
                        Log.d(TAG, "setDocument:success");
                        // Invoke success callback
                        if (callback != null) callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log failure
                        Log.w(TAG, "setDocument:failure", e);
                        // Invoke failure callback
                        if (callback != null) callback.onFailure(e);
                    }
                });
    }

    // Deletes Firestore document with specified id within specified collection
    // Invokes callback methods if given
    public static void deleteDocument(@NonNull String documentId, @NonNull String collectionId, SetDocumentCallback callback) {
        // Access shared Firestore database instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Attempt to delete document
        db.collection(collectionId).document(documentId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Log success
                        Log.d(TAG, "deleteDocument:success");
                        // Invoke success callback
                        if (callback != null) callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log failure
                        Log.w(TAG, "deleteDocument:failure", e);
                        // Invoke failure callback
                        if (callback != null) callback.onFailure(e);
                    }
                });
    }

    // Deletes Firestore document with specified id within specified collection
    // Navigates to specified activity if given
    public static void deleteDocument(String documentId, String collectionId, Context context, Class <?> destinationActivity) {
        // Access shared Firestore database instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assume name of document type is the same as collection id
        final String documentType = collectionId.substring(0,1).toUpperCase() +
                collectionId.substring(1, collectionId.length() - 1);

        // Attempt to delete document
        db.collection(collectionId).document(documentId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Log and toast success
                        Log.d(TAG, "deleteDocument:success");
                        String message = documentType + " deleted!";
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        // Navigate to destination activity (if given)
                        if (destinationActivity == null) return;
                        Intent intent = new Intent(context, destinationActivity);
                        context.startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log and toast failure
                        Log.w(TAG, "deleteDocument:failure", e);
                        String message = "Failed to delete " + documentType + "! " + e.getMessage();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Converts Firestore documents (in QuerySnapshot) into ArrayList of objects
    public static <T> ArrayList<T> querySnapshotToList(QuerySnapshot querySnapshot, Class<T> itemClass) {
        ArrayList<T>  objectList = new ArrayList<>();

        // QueryDocumentSnapshot extends DocumentSnapshot and is guaranteed to exist
        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
            T object = documentSnapshot.toObject(itemClass);
            objectList.add(object);
        }

        return objectList;
    }

    // SPECIFIC OPERATIONS //

    // Suspends tutor temporarily if numDays specified, indefinitely otherwise
    // Calls deleteDocument() on corresponding complaint if successful
    public static void suspendTutor(Complaint complaint, String numDays,  Context context, Class <?> destinationActivity) {

        String tutorId, complaintId, suspensionExpiry, successMessage;
        successMessage = "Tutor suspended ";

        // Get tutor and complaint ids
        tutorId = complaint.getTutorId();
        complaintId = complaint.getId();

        // Set suspension expiry and success message
        if (numDays == null) {
            suspensionExpiry = Tutor.INDEF_SUSPENSION;
            successMessage += "indefinitely!";
        } else {
            suspensionExpiry = DateTimeHandler.
                    dateToString(DateTimeHandler.
                            addDaysToCurrentDate(Integer.parseInt(numDays)));
            successMessage += "for " + numDays + " days (lifted on " + suspensionExpiry + " )!";
        }
        final String finalSuccessMessage = successMessage;

        // Access shared Firestore database instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Attempt to suspend tutor
        // Here, we use update() to modify a single field in the tutor document
        db.collection(TUTOR_COLLECTION).document(tutorId).update(TUTOR_SUSPENSION_EXPIRY_KEY, suspensionExpiry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Log and toast success
                        Log.d(TAG, "suspendTutor:success");
                        Toast.makeText(context, finalSuccessMessage, Toast.LENGTH_SHORT).show();

                        // Attempt to delete complaint
                        deleteDocument(complaintId, COMPLAINT_COLLECTION, context, destinationActivity);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log and toast failure
                        Log.w(TAG, "suspendTutor:failure", e);
                        String message = "Failed to suspend tutor! " + e.getMessage();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}